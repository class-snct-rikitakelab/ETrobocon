package Drive;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class Starter{
	private static final int SOCKET_PORT = 7360;
	private static final int REMOTE_COMMAND_START = 71; //"ｇ"

	private static ServerSocket    server = null;
	private static Socket          client = null;
	private static InputStream     inputStream = null;
	private static DataInputStream dataInputStream = null;
	private static int             remoteCommand = 0;

	private static EV3Body body;
	private static Driver driver;
	private static DriveCtrl dc;
	public Starter(){
		body = new EV3Body();
		driver = new Driver();
		dc = new DriveCtrl();
	}
	
	public void RemoteStart(){
		//final tailCommander tail = new tailCommander();
		//final ForwardCommander fcom = new ForwardCommander();
		

		dc.init();
		driver.Calibration();
		LCD.drawString("PLEASE CONNECT", 0, 0);
		Timer timer = new Timer();
		TimerTask task = new TimerTask(){
			public void run(){
				dc.tail.standTail();
			}
		};
		timer.scheduleAtFixedRate(task, 0, 4);
		for(;;){
			dc.tail.standTail();
			if(server == null){
				try {
					server = new ServerSocket(SOCKET_PORT);
					client = server.accept();
					inputStream = client.getInputStream();
					dataInputStream = new DataInputStream(inputStream);
				} catch (IOException ex) {
					server = null;
					dataInputStream = null;
				}
			} else {
				LCD.clear();
				LCD.drawString("REMOTE START",0,0);
				LCD.drawString("PRESS START KEY", 2, 2);
				Delay.msDelay(4);
				try {
					if (dataInputStream.available() > 0) {
						remoteCommand = dataInputStream.readInt();
						if(remoteCommand == REMOTE_COMMAND_START){
							break;
						}
				}
				} catch (IOException ex) {
				}
			}
		}

		
		//driver.log();

				
		LCD.clear();
		LCD.drawString("REMOTE OK", 0, 0);

		task.cancel();
		
		driver.run();
		//以下テスト用
		/*
		Timer timer = new Timer();
		TimerTask task = new TimerTask(){
			@Override
			public void run(){
				tail.sitTail();
				fcom.driveNormal();
			}
		};
		timer.scheduleAtFixedRate(task, 0, 4);
		for(;;){

		}
		*/
		
	}

	public void TouchStart(){
		//final ForwardCommander fcom = new ForwardCommander();//テスト用
		//final TurnCtrl tcon = new TurnCtrl();//テスト用
		//final DriveCtrl dc = new DriveCtrl();
		//final tailCommander tail = new tailCommander();//final修飾子はテスト用
		//Driver driver = new Driver();
		
		dc.init();
		driver.Calibration();
		//driver.dCtrl.init();
		
		LCD.drawString("Touch Strat",0,2);
		//driver.log();

		for(;;){
			dc.tail.standTail();
			if(body.touchSensorIsPressed()){
				break;
			}
		}
		LCD.clear();
		LCD.drawString("TOUCH OK", 0, 0);

		driver.run();
		
		//以下turnのテスト用
		/*
		dc.tail.startTail();
		Timer timer = new Timer();
		TimerTask stask = new TimerTask(){
			public void run(){
				dc.startDrive();
				//Delay.msDelay(2);
			}
		};
		timer.scheduleAtFixedRate(stask, 0, 4);
		for(int i = 0; i<1000;i++){
			LCD.drawInt(i, 2, 2);
		}
		stask.cancel();
		
		TimerTask task = new TimerTask() {
			public void run(){
				//tail.sitTail();
				//fcom.driveNormal();
				//tcon.MotorCtrl(80.0F);
				dc.Drive();
				//Delay.msDelay(2);

			}
		};
		timer.scheduleAtFixedRate(task, 0, 4);
		for(int i = 0;i<10000;i++){
			LCD.drawInt(i, 3, 3);
		}
		task.cancel();
		TimerTask stoptask = new TimerTask(){
			public void run(){
				dc.stopDrive();
			}
		};
		timer.scheduleAtFixedRate(stoptask, 0, 4);
		for(int i =0;i<1000;i++){
			LCD.drawInt(i, 2, 2);
		}
		stoptask.cancel();
		for(;;){
			dc.Waiting();
			
		}
		//Driver driver = new Driver();
		//driver.calibration();
		//driver.run();]
		 * 
		 */
	}
}
