package Driving;

import jp.etrobo.ev3.balancer.Balancer;
import lejos.hardware.Battery;

public class WheelMotorCtrl {
	EV3Body body;

	private static final float GYRO_OFFSET = 0.0F;

	static private float turn = 0.0F;
	static private float forward = 0.0F;

	WheelMotorCtrl(){
		body = new EV3Body();
	}

	public void setTurnValue(float turn){
		this.turn = turn;
	}

	public void setForwardValue(float forward){
		this.forward = forward;
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
}
