public class LogSaver{

	public void saveLog(Log log) {
		String str = "" + log.getTimestamp + " "+ log.getTurn + " "+ log.getForward + " " + log.getBrightness;
		try{
			File file = new File("log_file.txt");
			FileWriter filewriter = new FileWriter(file, true);
			BufferWriter buffwriter = new BufferWriter(filewriter);
			PrintWriter printwriter = new PrintWriter(buffwriter);

			printwriter.println(str);
			printwriter.close();
		}catch(IOException e){
		}
	}

	public ArrayList<Log> readLog(void){
		ArrayList<Log> array = new ArrayList<Log>();
		 try{
			File file = new File("log_file.txt");
			BufferedReader buffreader = new BufferedReader(new FileReader(file));
			String str;
			Log log = new Log();
			while((str = buffreader.readLine()) != null){
				log.setTimestamp(str.split(" ", 0));
				log.setTurn(Float.parseFloat(str.split(" ", 1)));
				log.setForward(Float.parseFloat(str.split(" ", 2)));
				log.setBrightness(Float.parseFloat(str.split(" ", 3)));

				array.add(log);
	 		}
			buffreader.close();
		}catch(IOException e){
		}
		return array;
	}
}