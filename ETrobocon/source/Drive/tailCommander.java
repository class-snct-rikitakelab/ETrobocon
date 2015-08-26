package Drive;

public class tailCommander {

	private static final int ANGLE_STAND = 94;
	private static final int ANGLE_SIT = 0;

	tailCtrl tailctrl;

	public tailCommander(){
		tailctrl = new tailCtrl();
	}

	public void standTail(){
		tailctrl.setTargetValue(ANGLE_STAND);
		float pwm = tailctrl.calcTailCtrl();
		tailctrl.operateTail(pwm);
	}

	public void sitTail(){
		tailctrl.setTargetValue(ANGLE_SIT);
		float pwm = tailctrl.calcTailCtrl();
		tailctrl.operateTail(pwm);
	}

}
