package Nansyo;

import lejos.hardware.lcd.LCD;
import lejos.hardware.port.BasicMotorPort;
import lejos.utility.Delay;

public class NansyoSolver {

	private static float TARGET = 0.12F;
	private static float PARAM_P = 150.0F;
	private static float PARAM_D = 50.0F;
	private static int BASIC_PWM = 30;

	private static float PWM_RATE_R = 1.033F;
	private static float PWM_RATE_L = 0.9F;

    private static int   PWM_ABS_MAX          = 60;
    private static float P_GAIN               = 2.5F;

    private static int GOAL_TO_GARAGE = 200; //スタート地点からガレージまでの道のり
    private static int BRAKE_DISTANCE = 30;  //制動距離


    private static float WHEEL_LENGH = 26.2F;

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		EV3Body body = new EV3Body();
		GarageSolver gs = new GarageSolver(40,94,BASIC_PWM);

		body.gyro.reset();
		body.sonar.enable();
		body.motorPortL.setPWMMode(BasicMotorPort.PWM_BRAKE);
		body.motorPortR.setPWMMode(BasicMotorPort.PWM_BRAKE);
		body.motorPortT.setPWMMode(BasicMotorPort.PWM_BRAKE);

		body.motorPortL.controlMotor(0, 0);
		body.motorPortR.controlMotor(0, 0);
		body.motorPortT.controlMotor(0, 0);
		body.motorPortL.resetTachoCount();
		body.motorPortR.resetTachoCount();
		body.motorPortT.resetTachoCount();

		LCD.drawString("Detect BLACK", 0, 4);

		boolean touchPressed = false;

		for(;;){
			tailControl(body,90);

			if(body.touchSensorIsPressed()){
				touchPressed = true;
			}else{
				if(touchPressed) break;
			}

			Delay.msDelay(20);
		}

		float black = body.getBrightness();

		LCD.clear();
		LCD.drawString("Detect WHITE", 0, 4);

		touchPressed = false;

		for(;;){
			tailControl(body,90);

			if(body.touchSensorIsPressed()){
				touchPressed = true;
			}else{
				if(touchPressed) break;
			}

			Delay.msDelay(20);
		}

		float white = body.getBrightness();

		float target = (black + white) / 2.0F;

		LCD.clear();
		LCD.drawString("On the START", 0, 4);

		touchPressed = false;

		for(;;){
			tailControl(body,90);

			if(body.touchSensorIsPressed()){
				touchPressed = true;
			}else{
				if(touchPressed) break;
			}

			Delay.msDelay(20);
		}

		touchPressed = false;

		for(;;){
			tailControl(body,90);

			if(body.touchSensorIsPressed()){
				touchPressed = true;
			}else{
				if(touchPressed) break;
			}

			Delay.msDelay(20);
		}

		ThreepointsDrive(body, target);

	}

	private static void ThreepointsDrive(EV3Body body,float target){

		float prevdiff = 0;

		while(true){

			tailControl(body, 90);

			int curDistance = (int)getDistance(body);

			float brightness = body.getBrightness();

			int forward = forwardSetter(BASIC_PWM,curDistance , GOAL_TO_GARAGE,BRAKE_DISTANCE);

			if(forward > 60) forward = 60;

			if(forward <0) forward = 0;

			float curdiff = target - brightness;

			int turn = (int)((target - brightness) * PARAM_P + (curdiff - prevdiff) * PARAM_D );

			prevdiff = curdiff;

			Drive(turn,forward,body);

			if(curDistance > GOAL_TO_GARAGE) break;

			Delay.msDelay(10);


		}

	}

	private static void Drive(int turn, int forward, EV3Body body){	//正だと左車輪が強くなり負だと右車輪が強くなる

		int pwmL = forward + turn;
		int pwmR = forward - turn;

		pwmL = (int)(pwmL / PWM_RATE_L);
		pwmR = (int)(pwmR * PWM_RATE_R);

		body.motorPortL.controlMotor(pwmL, 1);
		body.motorPortR.controlMotor(pwmR, 1);

	}

	private static final void tailControl(EV3Body body, int angle) {
        float pwm = (float)(angle - body.motorPortT.getTachoCount()) * P_GAIN; // 比例制御
        // PWM出力飽和処理
        if (pwm > PWM_ABS_MAX) {
            pwm = PWM_ABS_MAX;
        } else if (pwm < -PWM_ABS_MAX) {
            pwm = -PWM_ABS_MAX;
        }
        body.motorPortT.controlMotor((int)pwm, 1);
    }

	private static float getDistance(EV3Body body){
		int angleL = body.motorPortL.getTachoCount();
		int angleR = body.motorPortR.getTachoCount();

		float averageAngle = (angleL + angleR) / 2.0F;

		return averageAngle / 360.0F * WHEEL_LENGH;

	}

	private static int forwardSetter(int basicpwm,int curpos, int distance, int brakeDistance){

		if(curpos <= distance - brakeDistance){
			return basicpwm;
		}else{

			return (int)(-((float)basicpwm/(float)brakeDistance)*(distance - curpos));

		}

	}

}
