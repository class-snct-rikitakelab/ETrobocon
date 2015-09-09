package Drive;

import java.util.Timer;
import java.util.TimerTask;

import lejos.hardware.lcd.LCD;


public class Driver {

	DriveCtrl dCtrl = new DriveCtrl();
	StopRun stop = new StopRun();
	EV3Body body = new EV3Body();
	BrightnessMeasure bMeasure = new BrightnessMeasure();
	calibration calib = new calibration();
	
	public void run(){
		//dCtrl.init();
		dCtrl.tail.startTail();
		Timer timer = new Timer();
		TimerTask startTask = new TimerTask(){
			public void run(){
				dCtrl.startDrive();
			}
		};
		timer.scheduleAtFixedRate(startTask, 0, 4);

		for(int i=0;i<3000;i++){
			LCD.drawInt(i, 2, 2);
		}
		startTask.cancel();
		
		TimerTask drivetask = new TimerTask() {
			public void run(){
				dCtrl.Drive();
			}
		};
		StopRun stopR = new StopRun();
		timer.scheduleAtFixedRate(drivetask, 0, 4);
		while(!stopR.judgeGoal()){
			LCD.drawString("not GOALl", 4, 4);
		}
		LCD.clear();
		drivetask.cancel();
		TimerTask stoptask = new TimerTask(){
			public void run(){
				dCtrl.stopDrive();
			}
		};
		timer.scheduleAtFixedRate(stoptask, 0, 4);
		for(int i =0;i<1000;i++){
			LCD.drawInt(i, 2, 2);
		}
		stoptask.cancel();
		for(;;){
			dCtrl.Waiting();
			
		}

	}


	public void log(){

	}

	public void Calibration(){

		calib.Calibration();

	}

}
