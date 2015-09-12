package Log;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import Drive.BrightnessMeasure;

public class LogCreater{
	public Log getLog(){
		Log log = new Log();
		Timestamp time = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat simpledate = new SimpleDateFormat("HH:mm:ss.SSS");
		String timestamp = simpledate.format(time);

		float turn = TurnForwardKeeper.getTurn();
		float forward = TurnForwardKeeper.getForward();

		BrightnessMeasure bright = new BrightnessMeasure();
		float brightness = bright.getBrightness();

		log.setTimestamp(timestamp);
		log.setTurn(turn);
		log.setForward(forward);
		log.setBrightness(brightness);

		return log;
	}
}