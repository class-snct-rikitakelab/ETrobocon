import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.utility.Delay;

public class SONARSample {

	static EV3UltrasonicSensor sonicSensor = new EV3UltrasonicSensor(SensorPort.S4);

	public static void main(String[] args){
		SensorMode sonic = sonicSensor.getMode(0);
		float value[] = new float[sonic.sampleSize()];
		LCD.clear();
		while(true){
			LCD.drawString(sonic.getName(), 1, 0);
			sonic.fetchSample(value, 0);
			for(int k=0; k<sonic.sampleSize();k++){
				LCD.drawString("val[" + k + "] : " + value[k] + "m", 1, k+1);
			}
			Delay.msDelay(100);
			LCD.refresh();
		}
	}

}
