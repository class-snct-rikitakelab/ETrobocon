package Drive;

public class DriveCtrl {

	public tailCommander tail;
	public ForwardCommander fc;
	public TurnCtrl tc;

	public DriveCtrl(){
		tail = new tailCommander();
		fc = new ForwardCommander();
		tc = new TurnCtrl();
	}

	public void init(){
		fc.fc.fo.wmctrl.init();
	}

	//コースを走行しているときの動作を定義
	public void Drive(){
		tail.sitTail();
		tc.MotorCtrl();
		fc.driveNormal();
	}

	//スタートするときの動作を定義
	public void startDrive(){
		tail.sitTail();
		tc.MotorCtrl();
		fc.driveStart();

	}

	//完全停止状態に移るときの動作を定義
	public void stopDrive(){
		tc.MotorCtrl(0.0F);
		fc.driveStop();
		tail.stopTail();
	}

	//完全停止して待機しているときの動作を定義
	public void Waiting(){
		tail.stopTail();
		tc.MotorCtrl(0.0F);
		//fc.driveWaiting();
		fc.driveEnd();
	}
}
