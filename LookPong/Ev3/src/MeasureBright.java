import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;


public class MeasureBright {

	static EV3Body body = new EV3Body();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		float value;

		while(true){

			value = body.getBrightness();

			LCD.drawString("value="+value, 0, 4);

			Button.waitForAnyEvent();
		}
	}

}
