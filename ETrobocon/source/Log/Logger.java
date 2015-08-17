public class Logger{

	public void CreateLog (void){
		LogSaver saver = new LogSaver();
		LogCreater creater = new LogCreater();

		Log log = creater.getLog();
		saver.saveLog(log);

	}
}