package Drive;
//OnOff制御
public class OnOffCtrl {

	private static final float THRESHOLD = 0.2f;//しきい値


	//ONOFF制御クラス分ける
	public float calcOnOffCtrl(float bright) {
		float turn;

		if(bright < THRESHOLD){
			turn = -50.0F;
		}
		else{
			turn = 50.0F;
		}

		return turn;
	}

}
