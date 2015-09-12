package Drive;

public class tailCommander {

	private static final int ANGLE_STAND = 94;
	private static final int ANGLE_SIT = 0;
	private static final int ANGLE_START = 100;
	private static final int ANGLE_STOP = 89;

	tailCtrl tailctrl;

	public tailCommander(){
		tailctrl = new tailCtrl();
	}

	//テイルを接地する動作を定義
	public void standTail(){
		tailctrl.setTargetValue(ANGLE_STAND);
		float pwm = tailctrl.calcTailCtrl();
		tailctrl.operateTail(pwm);
	}

	//テイルを地面から離す動作を定義
	public void sitTail(){
		tailctrl.setTargetValue(ANGLE_SIT);
		float pwm = tailctrl.calcTailCtrl();
		tailctrl.operateTail(pwm);
	}
	
	//走り出しの時ちょっと前傾姿勢になるようにする動作
	public void startTail(){
		tailctrl.setTargetValue(ANGLE_START);
		float pwm = tailctrl.calcTailCtrl();
		tailctrl.operateTail(pwm);
	}
	
	//後ろに倒れてテイルを支えに停止するときの動作定義
	public void stopTail(){
		tailctrl.setTargetValue(ANGLE_STOP);
		float pwm = tailctrl.calcTailCtrl();
		tailctrl.operateTail(pwm);
	}

}
