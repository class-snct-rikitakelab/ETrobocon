package Drive;

import lejos.hardware.Battery;
import lejos.hardware.port.BasicMotorPort;
import lejos.utility.Delay;

public class MotorOperater{

	private static EV3Body body;

	private static final float GYRO_OFFSET = 0.0F;

	private static float turn;
	private static float forward;
	private static float pwm;

	MotorOperater(){
		this.turn = 0.0F;
		this.forward = 0.0F;
		this.pwm = 0.0F;

		body = new EV3Body();
	}

	public void setTurnValue(float turn){
		this.turn = turn;
	}

	public void setForwardValue(float forward){
		this.forward = forward;
	}

	public void setPWMValue(float pwm){
		this.pwm = pwm;
	}

	public void Drive(){
		float gyroNow = body.getGyroValue();
		int thetaL = body.motorPortL.getTachoCount();
		int thetaR = body.motorPortR.getTachoCount();
		int battery = Battery.getVoltageMilliVolt();
		Balancer.control(this.forward,this.turn,gyroNow,GYRO_OFFSET,thetaL,thetaR,battery);
		body.motorPortL.controlMotor(Balancer.getPwmL(), 1);
		body.motorPortR.controlMotor(Balancer.getPwmR(), 1);
	}

	public void operateTail(){
		body.motorPortT.controlMotor((int)this.pwm,1);
	}

	public void init(){
		body.gyro.reset();
        body.sonar.enable();
        body.motorPortL.setPWMMode(BasicMotorPort.PWM_BRAKE);
        body.motorPortR.setPWMMode(BasicMotorPort.PWM_BRAKE);
        body.motorPortT.setPWMMode(BasicMotorPort.PWM_BRAKE);

        for (int i=0; i < 1500; i++) {
            body.motorPortL.controlMotor(0, 0);
            body.getBrightness();
            body.getSonarDistance();
            body.getGyroValue();
            Battery.getVoltageMilliVolt();
            Balancer.control(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8000);
        }
        Delay.msDelay(1000);

        body.motorPortL.controlMotor(0, 0);
        body.motorPortR.controlMotor(0, 0);
        body.motorPortT.controlMotor(0, 0);
        body.motorPortL.resetTachoCount();   // left motor encoder reset
        body.motorPortR.resetTachoCount();   // right motor encoder reset
        body.motorPortT.resetTachoCount();   // tail motor encoder reset
        Balancer.init();            // inverted pendulum control initialization

	}

}