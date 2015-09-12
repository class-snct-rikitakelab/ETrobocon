package Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class LogSender{
	private static final int SOCKET_PORT = 7360;

	public static void sendLog(){
		LogSaver saver = new LogSaver();

		ArrayList<Log> array = saver.readLog();
		int size = array.size();
		Log log;

		try{
			ServerSocket server = new ServerSocket(SOCKET_PORT);
			Socket socket = server.accept();
			OutputStream outputStream = socket.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

			for(int i =0;i<size;i++){
				//ArrayListからログをひとつずつ取り出して送信
				log = array.get(i);
				String str = "" + log.getTimestamp() + " " + log.getTurn() + " " + log.getForward() + " " + log.getBrightness() +"\n";
				dataOutputStream.writeUTF(str);
			}

			server.close();
		}catch(IOException e){
		}
	}
}

