import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;


public class LineFollwer {
	public static void main(String[] args)
    {
        Controller controller = new Controller(SensorPort.S3, MotorPort.B, MotorPort.C);

        controller.run();
    }
}
