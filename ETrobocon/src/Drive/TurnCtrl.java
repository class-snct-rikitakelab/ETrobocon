package Drive;

import Log.TurnForwardKeeper;

public class TurnCtrl {

	private TurnOperater turnOperater;

	private Brightness brightness;

	private TurnForwardKeeper keeper;

	private PCtrl pCtrl;

	private OnOffCtrl onoff;	//テスト用

	TurnCtrl(){
		turnOperater = new TurnOperater();
		brightness = new Brightness();
		keeper = new TurnForwardKeeper();
		pCtrl = new PCtrl();
		onoff = new OnOffCtrl();
	}


	//モータを制御する
	public void MotorCtrl() {
		float bright = brightness.getBrightness();
		float turn = pCtrl.calcBrightnessCtrl(bright);
		//float turn = onoff.calcOnOffCtrl(bright);	//テスト用
		turnOperater.setOperate(turn);
		keeper.setTurn(turn);
		turnOperater.MotorCtrl();

	}

	//静止など一定の旋回量保つ時
	public void MotorCtrl(float turn){
		turnOperater.setOperate(turn);
		turnOperater.MotorCtrl();
	}

}
