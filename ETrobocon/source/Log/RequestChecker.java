public class RequestChecker{
	private static final int SOCKET_PORT = 7360;
	private static final int REMOTE_COMMAND_LOG = 76; //"L"�L�[

	private static ServerSocket    server = null;
	private static Socket          client = null;
	private static InputStream     inputStream = null;
	private static int             remoteCommand = 0;

	public static void checkRequest(void){
		Timer logTimer = new Timer();
		TimerTask logTask = new TimerTask() {
			@Override
			public void run(){
				if(sever == null){
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
					}
				}
			}
		};
		logTimer.schedule(logtask, 0, 20);
		for(;;){
			if(remoteCommand == REMOTE_COMMAND_LOG){
				logTimer.cancel();
				sever.close();
				break;
			}
		}
		LogSender sender = new LogSender();
		sender.sendLog();
	}
}