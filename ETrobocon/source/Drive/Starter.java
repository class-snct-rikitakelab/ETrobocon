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
	private static final int REMOTE_COMMAND_LOG = 71; //"L"

	private static ServerSocket    server = null;
	private static Socket          client = null;
	private static InputStream     inputStream = null;
	private static DataInputStream dataInputStream = null;
	private static int             remoteCommand = 0;

	public void RemoteStart(){
		EV3Body body = new EV3Body();
		final tailCommander tail = new tailCommander();
		final ForwardCommander fcom = new ForwardCommander();
		fcom.fc.fo.wmctrl.init();
		LCD.drawString("PLEASE CONNECT", 0, 0);
		for(;;){
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
				tail.standTail();
				LCD.clear();
				LCD.drawString("REMOTE START",0,0);
				LCD.drawString("PRESS START KEY", 2, 2);
				Delay.msDelay(4);
				try {
					if (dataInputStream.available() > 0) {
						remoteCommand = dataInputStream.readInt();
						if(remoteCommand == REMOTE_COMMAND_LOG){
							break;
						}
				}
				} catch (IOException ex) {
				}
			}
		}
		LCD.clear();
		LCD.drawString("REMOTE OK", 0, 0);

		//以下テスト用
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
		//Driver driver = new Driver();
		//driver.calibration();
		//driver.run();
	}

	public void TouchStart(){
		EV3Body body = new EV3Body();
		final ForwardCommander fcom = new ForwardCommander();//テスト用
		final TurnCtrl tcon = new TurnCtrl();//テスト用
		final tailCommander tail = new tailCommander();//final修飾子はテスト用
		fcom.fc.fo.wmctrl.init();
		LCD.drawString("Touch Strat",0,2);
		for(;;){
			tail.standTail();
			if(body.touchSensorIsPressed()){
				break;
			}
		}
		LCD.clear();
		LCD.drawString("TOUCH OK", 0, 0);

		//以下turnのテスト用
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run(){
				tail.sitTail();
				fcom.driveNormal();
				tcon.MotorCtrl(80.0F);


			}
		};
		timer.scheduleAtFixedRate(task, 0, 4);
		//Driver driver = new Driver();
		//driver.calibration();
		//driver.run();
	}
}
