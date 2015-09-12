package Drive;

public class runner {
	ForwardCommander fc;
	tailCommander tc;
	TurnCtrl turnc;

	runner(){
		fc = new ForwardCommander();
		tc = new tailCommander();
		turnc = new TurnCtrl();
	}

	public void run(){
		fc.driveStart();
		tc.sitTail();
		turnc.MotorCtrl();
	}

}
