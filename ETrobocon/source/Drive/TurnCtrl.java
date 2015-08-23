package Drive;

public class TurnCtrl {

	private TurnOperater turnOperater;

	private Brightness brightness;

	private PCtrl pCtrl;

	TurnCtrl(){
		turnOperater = new TurnOperater();
		brightness = new Brightness();
		pCtrl = new PCtrl();
	}


	//モータを制御する
	public void MotorCtrl() {
		float bright = brightness.getBrightness();
		float turn = pCtrl.calcBrightnessCtrl(bright);
		turnOperater.setOperate(turn);
		
		turnOperater.MotorCtrl();

	}

	public void MotorCtrl(float turn){
		turnOperater.setOperate(turn);
		turnOperater.MotorCtrl();
	}

}
