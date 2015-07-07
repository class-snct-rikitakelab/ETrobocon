import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
public class TestSample {

	public static void main(String[] args) {
		int n = 0;
		while(true){
			LCD.drawString(n+"sec", 0, 1);
			n++;
			Delay.msDelay(1000);
		}
	}

}
