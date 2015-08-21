package Drive;

public class tailCtrl {

	tachoGetter thaco;
	tailOperater op;

	private static final float P_GAIN = 2.5F;
	private static final int   PWM_ABS_MAX = 60;

	int TargetValue;

	public int getTargetValue(){
		return TargetValue;
	}

	public void setTargetValue(int target){
		TargetValue = target;
	}

	public float calcTailCtrl(){
		float pwm = (float)(TargetValue - thaco.getTachoCount()) * P_GAIN; // proportional control

		if(pwm > PWM_ABS_MAX){
			pwm = PWM_ABS_MAX;
		}
		else if(pwm < -PWM_ABS_MAX){
			pwm = -PWM_ABS_MAX;
		}
		return pwm;
	}

	public void operateTail(float angle){
		op.setPWM(angle);
		op.MotorCtrl();
	}

}
