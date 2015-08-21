package Drive;

public class ForwardCommander {

	static float NORM_FOR = 30.0F;
	static float STOP_FOR = -10.0F;
	static float WAIT_FOR = 0.0F;
	static float START_FOR = 10.0F;

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

}
