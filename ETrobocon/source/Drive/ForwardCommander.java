package Drive;

public class ForwardCommander {

	static float NORM_FOR = 30.0F;
	static float STOP_FOR = -20.0F;
	static float WAIT_FOR = 0.0F;
	static float START_FOR = 0.0F;

	ForwardCtrl fc;

	ForwardCommander(){
		fc  = new ForwardCtrl();
	}

	public void driveNormal(){
		fc.setTarget(NORM_FOR);
		fc.OperateForward();
	}

	public void driveStop(){
		fc.setTarget(STOP_FOR);
		fc.OperateForward();
	}

	public void driveWaiting(){
		fc.setTarget(WAIT_FOR);
		fc.OperateForward();
	}

	public void driveStart(){
		fc.setTarget(START_FOR);
		fc.OperateForward();
	}

	//全ての動作が終わって完全停止するときに使うメソッド
	public void driveEnd(){
		//左右のモータのPWM値を0にすることによってモータを完全停止させます．
		fc.fo.wmctrl.StopMotors();
	}

}
