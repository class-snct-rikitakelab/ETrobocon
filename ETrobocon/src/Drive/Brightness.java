//輝度の目標値・計測値

package Drive;

public class Brightness {

	private static float targetVal = 0.2f;//目標値

	private BrightnessMeasure brightnessMeasure = new BrightnessMeasure();


	//目標値を設定する
	public void setTarget(float tval) {
		targetVal = tval;
	}

	//目標値を取得する
	public float getTarget() {
		return targetVal;
	}

	//計測値を取得する
	public float getBrightness() {
		return brightnessMeasure.getBrightness();
	}

}
