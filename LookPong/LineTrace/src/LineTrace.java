import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;


public class LineTrace {

	static RegulatedMotor LeftMotor = Motor.C;
	static RegulatedMotor RightMotor = Motor.B;
	static EV3ColorSensor color = new EV3ColorSensor(SensorPort.S3);
	static int BLACK = 7;

	public static void main(String[] args){
		motor_init();
		while(true){
			System.out.println(color.getColorID());
			if(color.getColorID() == BLACK){
				motor_set(100,30);
			} else {
				motor_set(30,100);
			}
		}
	}

	private static void motor_init() {
		// TODO Auto-generated method stub
		LeftMotor.resetTachoCount();
		RightMotor.resetTachoCount();

		LeftMotor.rotateTo(0);
		RightMotor.rotateTo(0);
	}

	private static void motor_set(int l_motor_pow, int r_motor_pow) {
		// TODO Auto-generated method stub
		LeftMotor.setSpeed(l_motor_pow);
		RightMotor.setSpeed(r_motor_pow);

		if(l_motor_pow > 0){
			LeftMotor.forward();
		} else if(l_motor_pow < 0){
			LeftMotor.backward();
		} else {
			LeftMotor.stop();
		}

		if(r_motor_pow > 0){
			RightMotor.forward();
		} else if(r_motor_pow < 0){
			RightMotor.backward();
		} else {
			RightMotor.stop();
		}

	}

}
