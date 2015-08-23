package Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class LogSaver{

	public void saveLog(Log log) {
		String str = "" + log.getTimestamp() + " "+ log.getTurn() + " "+ log.getForward() + " " + log.getBrightness();
		try{
			File file = new File("log_file.txt");
			FileWriter filewriter = new FileWriter(file, true);
			BufferedWriter buffwriter = new BufferedWriter(filewriter);
			PrintWriter printwriter = new PrintWriter(buffwriter);

			printwriter.println(str);
			printwriter.close();
		}catch(IOException e){
		}
	}

	public ArrayList<Log> readLog(){
		ArrayList<Log> array = new ArrayList<Log>();
		 try{
			File file = new File("log_file.txt");
			BufferedReader buffreader = new BufferedReader(new FileReader(file));
			String str;
			Log log = new Log();
			while((str = buffreader.readLine()) != null){
				String[] splitstr = str.split(" ", 0);
				log.setTimestamp(splitstr[0]);
				log.setTurn(Float.parseFloat(splitstr[1]));
				log.setForward(Float.parseFloat(splitstr[2]));
				log.setBrightness(Float.parseFloat(splitstr[3]));

				array.add(log);
	 		}
			buffreader.close();
		}catch(IOException e){
		}
		return array;
	}
}