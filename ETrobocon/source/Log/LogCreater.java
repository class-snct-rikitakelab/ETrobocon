public class LogCreater{
	public Log getLog(){
		Log log = new Log();
		Timestamp time = new Timestamp(System.currentTimeMillis());
		String timestamp = SimpleDateFormat("HH:mm:ss.SSS").format(time);
		
		TurnForwardKeeper keeper = new TurnForwardKeeper();
		float turn = keeper.getTurn();
		float forward = keeper.getForward;

		Brightmesure bright = new Brightmesure();
		float brightness = bright.getbrightness();

		log.setTimestamp(timestamp);
		log.setTurn(turn);
		log.setFollow(forward);
		log.setBrightness(brightness);

		return log;
	}
}