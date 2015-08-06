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
    private static final int		CNTLIMIT = 95000000;
    private static final float	KU = 350;		//
    private static final float 	PU = 0.4f;		//
    private static final float 	KP = KU  * 0.6f;		//
    private static final float 	TI = PU  * 0.5f;		//
    private static final float 	TD = PU  * 0.125f;		//
    private static final float 	KI = KP / TI;		//
    private static final float 	KD = KP * TD;		//
    



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
	private static int			sensorcounter = 0;
	private static float			diff[];
	private static float			integral = 0;


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
        diff = new float[2];
        diff[0] = 0;diff[1] = 0;

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

                    if(checkNotBlackzone(body)==true){ //Check for gray color point
                    	if(checkGrayzone(body)==true){
                    		if(sensorcounter%2 == 0)
                    			graycounter++;
                    	}
                    }else{
                    	if(sensorcounter%2==0)
                    		graycounter=0;
                    	//if(graycounter<0)graycounter=0;
                    }

                    sensorcounter++;

                    float forward =  0.0F; // forward-reverse instruction
                    float turn    =  0.0F; // turning instruction
                    float p, i, d;
                    
                    if (graycounter>CNTLIMIT) {           // Slow After found too much gray point
                    	if(checkGrayzone(body)==true){
                    	forward = -25.0F;  // forward instruction
                        turn = 0.0F;
                    	//graycounter+=2;
                        }
                    }

                    else {
                    	forward = 80.0F;  // forward instruction
                    	/*
                    	if (body.getBrightness() > THRESHOLD) {
                    		turn = 50.0F;  // right turn instruction
                    	} else {
                    		turn = -50.0F; // left turn instruction
                    	}
                    	*/
                    	//2.5Hz	0.4秒
                    	diff[0] = diff[1];
                    	diff[1] = body.getBrightness() - THRESHOLD;
                    	integral += (diff[1] + diff[0]) / 2.0 * 0.004f;
                    	
                    	p = KP * diff[1];
                    	i = KI * integral;
                    	d = KD * (diff[1] - diff[0]) / 0.004f;
                    	
                    	turn = p + i + d;
                    	
                    	if(turn > 50.0F){
                    		turn = 50.0F;
                    	}
                    	else if(turn < -50.0F){
                    		turn = -50.0F;
                    	}

                    }


                    float gyroNow = body.getGyroValue();              // gyro sensor value
                    int thetaL = body.motorPortL.getTachoCount();     //  left motor rotation angle
                    int thetaR = body.motorPortR.getTachoCount();     // right motor rotation angle
                    int battery = Battery.getVoltageMilliVolt();      //  battery voltage [mV]
                    if(body.touchSensorIsPressed()){
                        body.motorPortL.resetTachoCount();   // left motor encoder reset
                        body.motorPortR.resetTachoCount();   // right motor encoder reset
                        Balancer.init();            // inverted pendulum control initialization
                        Balancer.control (0, 0, gyroNow, GYRO_OFFSET, thetaL, thetaR, battery); // inverted pendulum control
                    	body.motorPortL.controlMotor(0, 1); // left motor PWM output set
                    	body.motorPortR.controlMotor(0, 1); // right motor PWM output set
                    }
                    else{
                    	Balancer.control (forward, turn, gyroNow, GYRO_OFFSET, thetaL, thetaR, battery); // inverted pendulum control
                    	body.motorPortL.controlMotor(Balancer.getPwmL(), 1); // left motor PWM output set
                    	body.motorPortR.controlMotor(Balancer.getPwmR(), 1); // right motor PWM output set
                    }
                }
            };
        driveTimer.scheduleAtFixedRate(driveTask, 0, 4);

        for (;;) {


        	if (graycounter>CNTLIMIT) {
                rcTimer.cancel();
                driveTimer.cancel();
                // disconnect with run loop

                Delay.msDelay(300);

                for(int k = 0;k<200;++k){
                	tailControl(body, 82); //complete stop for tail control

                	body.motorPortL.controlMotor(0,1); //left motor PWM output set
                	body.motorPortR.controlMotor(0,1); //right motor PWM output set

                	graycounter=0; //reset gray color point counter

                	Delay.msDelay(10);
                }

                for(int k = 0;k<300;++k){
                	tailControl(body, 82); //complete stop for tail control
                	//Balancer.control (10.0f, 0.0f, body.getGyroValue(), GYRO_OFFSET, body.motorPortL.getTachoCount(), body.motorPortR.getTachoCount(), Battery.getVoltageMilliVolt()); // inverted pendulum control
                	//body.motorPortL.controlMotor(Balancer.getPwmL(), 1); // left motor PWM output set
                	//body.motorPortR.controlMotor(Balancer.getPwmR(), 1); // right motor PWM output set
                	body.motorPortL.controlMotor(30,1); //left motor PWM output set
                	body.motorPortR.controlMotor(30,1); //right motor PWM output set

                	Delay.msDelay(10);
                }
                while(true){
                	tailControl(body, 82); //complete stop for tail control;
                	body.motorPortL.controlMotor(0,1); //left motor PWM output set
                	body.motorPortR.controlMotor(0,1); //right motor PWM output set
                }

        	}

        	Delay.msDelay(20);

            /*
            if (body.touchSensorIsPressed() // running end Once the touch sensor is pressed
                || checkRemoteCommand(REMOTE_COMMAND_STOP)) { // running end Once the 's' key is pressed in the PC
                rcTimer.cancel();
                driveTimer.cancel();
                break;
            }
            Delay.msDelay(20);
            */
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
    	if(value >= 0.06 && value<=0.14){
    		return true;
    	}else{
    		return false;
    	}
    }
    private static final boolean checkNotBlackzone(EV3Body body){
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
