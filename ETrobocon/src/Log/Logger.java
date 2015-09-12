package Log;

public class Logger{

	public void CreateLog (){
		LogSaver saver = new LogSaver();
		LogCreater creater = new LogCreater();

		Log log = creater.getLog();
		saver.saveLog(log);

	}
}