package Driving;

public class ForwardCommander {

	static float NORM_FOR = 30.0F;
	static float STOP_FOR = -10.0F;
	static float WAIN_FOR = 0.0F;

	ForwardController fc;

	ForwardCommander(){
		fc  = new ForwardController();
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
		fc.setTarget(WAIN_FOR);
		fc.OperateForward();
	}

}