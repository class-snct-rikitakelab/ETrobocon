public class LogSender{
	private static final int SOCKET_PORT = 7360;

	public static void sendLog(void){
		LogSaver saver = new Logsaver();
		
		ArrayList<Log> array = savaer.readLog();
		int size = array.size();
		try{
			ServerSocket server = new SeverSocket(SOCKET_PORT);
			Socket socket = server.accept();
			OutputStream outputStream = socket.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
			
			for(int i =0;i<size;i++){
				Log log = array.get(i);
				String str = "" + log.getTimestamp() + " " + log.getTurn() + " " + log.getForward() + " " + log.getBrightness() +"\n";
				dataOutStream.writeUTF(str);
			}
						
			sever.close();
		}catch(IOException e){
		}
	}
}