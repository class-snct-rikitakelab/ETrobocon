package Nansyo;

import lejos.utility.Delay;

public class GarageSolver {

	int distanceToGarage = 40;			//ガレージに進入するために前進する距離cm単位
	int tailangle = 90;					//三点倒立するためのしっぽの角度
	private int Motor_Power = 60;	//モータの基礎的なPWM値
	private static float Distance;		//走行終了時の位置
	private static int BREAKING_DISTANCE = 23; //止まる際の制動距離

	private static float WHEEL_LENGH = 26.2F;	//車輪の周の長さ

	private static float PARAMR = 2.5F;	//右タイヤ比例制御用のパラメータ
	private static float PARAML = 2.5F;	//左タイヤ比例制御用のパラメータ

	private int PowerL = Motor_Power;
	private int PowerR = Motor_Power;

    private static int   PWM_ABS_MAX          = 60;
    private static float P_GAIN               = 2.5F;


	GarageSolver(int distanceToGarage, int tailangle, int power){
		this.distanceToGarage = distanceToGarage - BREAKING_DISTANCE;
		this.tailangle = tailangle;
		this.Motor_Power = power;
	}

	public void SolveGarage(EV3Body body){

		BeforeTakeInto(body);
		TakeInto(body);
		KeepSec(body);

	}


	public void BeforeTakeInto(EV3Body body){		//進入待ち状態にはいるためのメソッド 三点倒立で1秒間静止

		tailControl(body, tailangle);						//（94 - tailangle） * 0.1秒かけて3点倒立する
		Delay.msDelay(100);
		for(int i = 94; i < tailangle ; i--){
			tailControl(body, i);
			Delay.msDelay(100);
		}
	}

	public void TakeInto(EV3Body body){										//distanceToGarageで定めた距離だけ前進
		Distance = getDistance(body) + distanceToGarage;

		float start = getDistance(body);

		do{
			float currentDistance = getDistance(body);

			int tachoL = body.motorPortL.getTachoCount();
			int tachoR = body.motorPortR.getTachoCount();

			int target = (tachoL + tachoR)/2;

			int PowerL = Motor_Power + (int)((target - tachoL) * PARAML);
			int PowerR = Motor_Power + (int)((target - tachoR) * PARAMR);

        	body.motorPortL.controlMotor(PowerL, 1); // 左モータPWM出力セット
        	body.motorPortR.controlMotor(PowerR, 1); // 右モータPWM出力セット

        	tailControl(body,tailangle);
		}while(getDistance(body) < Distance);
	}

	public void KeepSec(EV3Body body){	//経過待ち状態の時のメソッド 完全停止状態で3秒間維持

		int currentPower = Motor_Power;

		//float start = getDistance(body);

		int time = 0;

		while(true){
			currentPower = (int)(currentPower * 0.99);
			body.motorPortR.controlMotor(currentPower, 1);
			body.motorPortL.controlMotor(currentPower, 1);

			tailControl(body, tailangle);

			Delay.msDelay(20);

			time += 20;

			if(time == 1000) break;
		}

		//float end = getDistance(body);

		body.motorPortL.controlMotor(0, 0); // 左モータPWM出力セット
		body.motorPortR.controlMotor(0, 0); // 右モータPWM出力セット

		time = 0;

		while(true){
			tailControl(body,tailangle);
			Delay.msDelay(20);
			time += 20;
			if(time > 10000) break;
       	}

		//LCD.drawString("seidokyori:"+(end-start), 0, 4);

		//Button.waitForAnyEvent();
	}

	private float getDistance(EV3Body body){
		int angleL = body.motorPortL.getTachoCount();
		int angleR = body.motorPortR.getTachoCount();

		float averageAngle = (angleL + angleR) / 2.0F;

		return averageAngle / 360.0F * WHEEL_LENGH;

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

}
