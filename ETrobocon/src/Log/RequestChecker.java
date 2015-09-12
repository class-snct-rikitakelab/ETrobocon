package Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class RequestChecker{
	private static final int SOCKET_PORT = 7360;
	private static final int REMOTE_COMMAND_LOG = 76; //"g"

	private static ServerSocket    server = null;
	private static Socket          client = null;
	private static InputStream     inputStream = null;
	private static DataInputStream dataInputStream = null;
	private static int             remoteCommand = 0;

	public static void checkRequest(){
		Timer logTimer = new Timer();
		TimerTask logTask = new TimerTask() {
			@Override
			public void run(){
				//送信要求受信待ちのタスク
				if(server == null){
					try {
						server = new ServerSocket(SOCKET_PORT);
						client = server.accept();
						inputStream = client.getInputStream();
						dataInputStream = new DataInputStream(inputStream);
						} catch (IOException ex) {
						ex.printStackTrace();
						server = null;
						dataInputStream = null;
						}
				} else {
					try {
						if (dataInputStream.available() > 0) {
							remoteCommand = dataInputStream.readInt();
						}
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		};
		logTimer.schedule(logTask, 0, 20);
		for(;;){
			//ログ送信キーが押されたら待ちを終了する
			if(remoteCommand == REMOTE_COMMAND_LOG){
				logTimer.cancel();
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		//送信
		LogSender sender = new LogSender();
		sender.sendLog();
	}
}