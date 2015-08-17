

package turn;

public class TurnCtrl {

	private TurnOperater turnOperater;

	private Brightness brightness;

	private PCtrl pCtrl;


	//モータを制御する
	public void MotorCtrl() {
		float bright = brightness.getBrightness();
		float turn = pCtrl.calcBrightnessCtrl(bright);
		turnOperater.setOperate(turn);
		turnOperater.operate();

	}

}
