package Drive

import jp.etrobo.ev3.balancer.Balancer;

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

	public Drive(){
		float gyroNow = body.getGyroValue();
		int thetaL = body.motorPortL.getTachoCount();
		int thetaR = body.motorPortR.getTachoCount();
		int battery = Battery.getVoltageMilliVolt();
		Balancer.control(this.forward,this.turn,gyroNow,GYRO_OFFSET,thetaL,thetaR,battery);
		body.motorPortL.controlMotor(Balancer.getPwmL(), 1);
		body.motorPortR.controlMotor(Balancer.getPwmR(), 1);
	}

	public operateTail(){
		body.motorPortT.controlMotor((int)this.pwm,1);
	}
}