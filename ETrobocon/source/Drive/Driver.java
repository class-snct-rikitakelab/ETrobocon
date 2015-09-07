package Drive;

import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class Driver {

	DriveCtrl dCtrl = new DriveCtrl();
	StopRun stop = new StopRun();
	EV3Body body = new EV3Body();
	BrightnessMeasure bMeasure = new BrightnessMeasure();


	public void Calibration(){

		Brightness bright = dCtrl.tc.brightness;
		CheckGray cGray = stop.cGray;

		Delay.msDelay(200);

		LCD.clear();
		LCD.drawString("SET White COLOR", 2, 2);
		float wColor = getColor();
		LCD.drawString("SET Black COLOR", 2, 2);
		float bColor = getColor();
		LCD.drawString("SET Gray COLOR ", 2, 2);
		float gColor = getColor();

		bright.setTarget((wColor + bColor) / 2);
		cGray.setTargetWhite((wColor + gColor) / 2);
		cGray.setTargetBlack((gColor + bColor) / 2);

	}

	private float getColor(){
	Delay.msDelay(300);
	for(;;){
		if(body.touchSensorIsPressed())break;
		Delay.msDelay(20);
	}
	Delay.msDelay(200);
	float color = bMeasure.getBrightness();

	return color;
	}

}
