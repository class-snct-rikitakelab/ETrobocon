package Drive;

import java.util.Timer;
import java.util.TimerTask;

import lejos.hardware.lcd.LCD;
import Log.Logger;
import Log.RequestChecker;


public class Driver {

	DriveCtrl dCtrl = new DriveCtrl();
	StopRun stop = new StopRun();
	EV3Body body = new EV3Body();
	BrightnessMeasure bMeasure = new BrightnessMeasure();
	calibration calib = new calibration();
	Timer timer = new Timer();
	
	Boolean wflag = false;
	
	public void run(){
		//dCtrl.init();
		//log();	//Logの取得を開始
		//LCD.drawString("Loging started", 0, 0);
		
		//自立開始の処理
		dCtrl.tail.startTail(); //最初にちょっと前傾になるため尻尾をたたきつける
		TimerTask startTask = new TimerTask(){
			public void run(){
				dCtrl.startDrive();
			}
		};
		timer.scheduleAtFixedRate(startTask, 0, 4);

		for(int i=0;i<3000;i++){
			LCD.drawString("Starting", 2, 2);
		}
		//前に進んでいるときに本走行に移る
		while((Balancer.getPwmL()<0) || (Balancer.getPwmR()<0)){
			;
		}
		startTask.cancel();
		
		//走行処理
		TimerTask drivetask = new TimerTask() {
			public void run(){
				dCtrl.Drive();
			}
		};
		StopRun stopR = new StopRun();
		timer.scheduleAtFixedRate(drivetask, 0, 4);
		//止まる条件を見たなさい限り走行を続ける
		while(!stopR.judgeGoal()){
			LCD.drawString("not GOALl", 4, 4);
		}
		LCD.clear();
		drivetask.cancel();
		
		//止まるために一瞬後ろに加速度をかける
		TimerTask stoptask = new TimerTask(){
			public void run(){
				dCtrl.stopDrive();
			}
		};
		timer.scheduleAtFixedRate(stoptask, 0, 4);
		for(int i =0;i<1800;i++){
			LCD.drawString("Stop", 2, 2);
		}
		
		stoptask.cancel();
		
		//尻尾を立てて停止
		TimerTask waittask = new TimerTask(){
			public void run(){
			dCtrl.Waiting();
			}
		};
		timer.scheduleAtFixedRate(waittask, 0, 4);
		//wflag = true;
		//log();

	}


	public void log(){
		final Logger logger = new Logger();
		TimerTask logtask = new TimerTask(){
			public void run(){
				logger.CreateLog();
			}
		};
		RequestChecker rc = new RequestChecker();
		if(wflag){
			//走行終了していればログの送信処理
			//rc.checkRequest();
			LCD.clear();
			LCD.drawString("Log sended", 0, 0);
		}else{
			//0.4秒ごとにログを取得
			timer.scheduleAtFixedRate(logtask, 0, 400);
			LCD.clear();
			LCD.drawString("Loggging started", 0, 0);
		}
	}

	public void Calibration(){

		calib.Calibration();

	}

}
