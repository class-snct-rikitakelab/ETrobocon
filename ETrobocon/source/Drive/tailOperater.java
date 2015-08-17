package Drive;

public class tailOperater {

	private EV3Body body;

	private float pwm;

	public void setPWM(float op){
		this.pwm = op;
	}

	public float getPWM(){
		return this.pwm;
	}

	public void MotorCtrl(){
		body.motorPortT.controlMotor((int) pwm, 1);
	}

}
