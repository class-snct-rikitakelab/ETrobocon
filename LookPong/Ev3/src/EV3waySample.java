/*
 *  EV3waySample.java (for leJOS EV3)
 *  Created on: 2015/05/09
 *  Author: INACHI Minoru
 *  Copyright (c) 2015 Embedded Technology Software Design Robot Contest
 */

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import lejos.hardware.Battery;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.BasicMotorPort;
import lejos.utility.Delay;

/**
 * Java sample program for leJOS EV3 of two-wheel inverted pendulum line trace robot.
 */
public class EV3waySample {
    // The following parameters will need to be tuned to suit the individual sensor / environment
    private static final float GYRO_OFFSET          = 0.0F; //gyro sensor offset value
    private static final float LIGHT_WHITE          = 0.4F; // white color sensor brightness value

    private static final float LIGHT_BLACK          = 0.0F; //  black color sensor brightness value
    private static final float SONAR_ALERT_DISTANCE = 0.3F; // obstacle detection distance by ultrasonic sensor [m]
    private static final int   TAIL_ANGLE_STAND_UP  = 94;   //  complete stop when the angle [degrees]
    private static final int   TAIL_ANGLE_DRIVE     = 3;    // balance during running of the angle [degrees]
    private static final float P_GAIN               = 2.5F; //  complete stop motor control proportionality coefficient
    private static final int   PWM_ABS_MAX          = 60;   // complete stop motor control PWM absolute maximum
    private static final int   SOCKET_PORT          = 7360; // port to be connected to the PC
    private static final int   REMOTE_COMMAND_START = 71;   // 'g'
    private static final int   REMOTE_COMMAND_STOP  = 83;   // 's'
    private static final float THRESHOLD = (LIGHT_WHITE+LIGHT_BLACK)/2.0F;	// line threshold of trace

    private static ServerSocket    server = null;
    private static Socket          client = null;
    private static InputStream     inputStream = null;
    private static DataInputStream dataInputStream = null;
    private static int             remoteCommand = 0;

    private static EV3Body         body    = new EV3Body();
    private static int             counter = 0;
    private static boolean         alert   = false; //Detect some range before found obstacles
	private static boolean         aware   = false; //Detect an obstacles
	private static int             graycounter = 0; //Count gray color point


    /**
     *  Main
     */
    public static void main(String[] args) {

        LCD.drawString("Please Wait...  ", 0, 4);
        body.gyro.reset();
        body.sonar.enable();
        body.motorPortL.setPWMMode(BasicMotorPort.PWM_BRAKE);
        body.motorPortR.setPWMMode(BasicMotorPort.PWM_BRAKE);
        body.motorPortT.setPWMMode(BasicMotorPort.PWM_BRAKE);


        // Initial execution performance of Java is bad, it is not possible to obtain sufficient real-time to an inverted pendulum.
        // About the methods frequently used to traveling, to empty run until HotSpot is converted to native code.
        // Default number of executions that HotSpot occurs 1500.
        for (int i=0; i < 1500; i++) {
            body.motorPortL.controlMotor(0, 0);
            body.getBrightness();
            body.getSonarDistance();
            body.getGyroValue();
            Battery.getVoltageMilliVolt();
            Balancer.control(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8000);
        }
        Delay.msDelay(1000);       // In another thread wait until the wonder time that HotSpot is complete.

        body.motorPortL.controlMotor(0, 0);
        body.motorPortR.controlMotor(0, 0);
        body.motorPortT.controlMotor(0, 0);
        body.motorPortL.resetTachoCount();   // left motor encoder reset
        body.motorPortR.resetTachoCount();   // right motor encoder reset
        body.motorPortT.resetTachoCount();   // tail motor encoder reset
        Balancer.init();            // inverted pendulum control initialization
        graycounter = 0;

        // Remote connection
        Timer rcTimer = new Timer();
        TimerTask rcTask = new TimerTask() {  //  remote command task
                @Override
                public void run() {
                    if (server == null) { // Not connected
                        try {
                            server = new ServerSocket(SOCKET_PORT);
                            client = server.accept();
                            inputStream = client.getInputStream();
                            dataInputStream = new DataInputStream(inputStream);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            server = null;
                            dataInputStream = null;
                        }
                    } else {
                        try {
                            if (dataInputStream.available() > 0) {
                                remoteCommand = dataInputStream.readInt();
                            }
                        } catch (IOException ex) {
                        }
                    }
                }
            };
        rcTimer.schedule(rcTask, 0, 20);

        // Waiting for the start of
        LCD.drawString("Touch to START", 0, 4);
        boolean touchPressed = false;
        for (;;) {
            tailControl(body, TAIL_ANGLE_STAND_UP); //complete stop for angle control
            if (body.touchSensorIsPressed()) {
                touchPressed = true;          // touch sensor is pressed
            } else {
                if (touchPressed) break;      // touch sensor I was released after being pressed
            }
            if (checkRemoteCommand(REMOTE_COMMAND_START)) break;  // in the PC 'g' key is pressed
            Delay.msDelay(20);
        }


        LCD.drawString("Running       ", 0, 4);
        Timer driveTimer = new Timer();
        TimerTask driveTask = new TimerTask() {

                @Override
                public void run() {
                    tailControl(body, TAIL_ANGLE_DRIVE); // balance for running angle to control

                    if (++counter >= 40/4) {      // for each // about 40ms and obstacle detection
                        alert = sonarAlert(body); // obstacle detection
                        aware = sonarAware(body); // detection before found obstacle
                        counter = 0;
                    }

                    if(checkGrayzone(body)==true){ //Check for gray color point
                    	graycounter++;
                    }else{
                    	graycounter=0;
                    }

                    float forward =  0.0F; // forward-reverse instruction
                    float turn    =  0.0F; // turning instruction

                    if (graycounter>350) {           // Slow After found too much gray point
                    	forward = -10.0F;  // forward instruction
                        if (body.getBrightness() > THRESHOLD) {
                            turn = 0F;  // right turn instruction
                        } else {
                            turn = 0F; // left turn instruction
                        }
                    }

                    else {
                    	forward = 50.0F;  // forward instruction
                    	if (body.getBrightness() > THRESHOLD) {
                    		turn = 50.0F;  // right turn instruction
                    	} else {
                    		turn = -50.0F; // left turn instruction
                    	}
                    }

                    float gyroNow = body.getGyroValue();              // gyro sensor value
                    int thetaL = body.motorPortL.getTachoCount();     //  left motor rotation angle
                    int thetaR = body.motorPortR.getTachoCount();     // right motor rotation angle
                    int battery = Battery.getVoltageMilliVolt();      //  battery voltage [mV]
                    Balancer.control (forward, turn, gyroNow, GYRO_OFFSET, thetaL, thetaR, battery); // inverted pendulum control
                    body.motorPortL.controlMotor(Balancer.getPwmL(), 1); // left motor PWM output set
                    body.motorPortR.controlMotor(Balancer.getPwmR(), 1); // right motor PWM output set
                }
            };
        driveTimer.scheduleAtFixedRate(driveTask, 0, 4);

        for (;;) {


        	if (graycounter>475) {
                rcTimer.cancel();
                driveTimer.cancel();
                // disconnect with run loop

                while(true){
                	tailControl(body, 82); //complete stop for tail control

                	body.motorPortL.controlMotor(0,1); //left motor PWM output set
                	body.motorPortR.controlMotor(0,1); //right motor PWM output set

                	graycounter=0; //reset gray color point counter
                }

        	}

        	Delay.msDelay(20);

            if (body.touchSensorIsPressed() // running end Once the touch sensor is pressed
                || checkRemoteCommand(REMOTE_COMMAND_STOP)) { // running end Once the 's' key is pressed in the PC
                rcTimer.cancel();
                driveTimer.cancel();
                break;
            }
            Delay.msDelay(20);
        }

    }

    /*
     * The obstacle detection by the ultrasonic sensor
     * @return true(there is an obstacle) / false (no obstacle))
     */
    private static final boolean sonarAlert(EV3Body body) {
        float distance = body.getSonarDistance();
        if ((distance <= SONAR_ALERT_DISTANCE) && (distance >= 0)) {
            return true;  // obstacle; return true
        }
        return false;
    }

    /*
     * The checking gray color point by sheck point that is not black color
     * @return true(not black color) / false (black color))
     */
    private static final boolean checkGrayzone(EV3Body body){
    	float value = body.getBrightness();
    	if(value > 0.05){
    		return true;
    	}else{
    		return false;
    	}
    }

    /*
     * The obstacle awareness by the ultrasonic sensor
     * @return true(there is an obstacle) / false (no obstacle))
     */
    private static final boolean sonarAware(EV3Body body) {
        float distance = body.getSonarDistance();
        if ((distance <= SONAR_ALERT_DISTANCE+0.2F) && (distance >= SONAR_ALERT_DISTANCE)) {
            return true;  // obstacle; return true
        }
        return false;
    }

    /*
     *  Traveling body full stop motor of angle control
     * @param angle motor target angle [degree]
     */
    private static final void tailControl(EV3Body body, int angle) {
        float pwm = (float)(angle - body.motorPortT.getTachoCount()) * P_GAIN; // proportional control
        // PWM output saturation processing
        if (pwm > PWM_ABS_MAX) {
            pwm = PWM_ABS_MAX;
        } else if (pwm < -PWM_ABS_MAX) {
            pwm = -PWM_ABS_MAX;
        }
        body.motorPortT.controlMotor((int)pwm, 1);
    }

    /*
     * Check the remote command
     */
    private static final boolean checkRemoteCommand(int command) {
        if (remoteCommand > 0) {
            if (remoteCommand == command) {
                return true;
            }
        }
        return false;
    }
}
