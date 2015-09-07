package Drive;
//P制御

public class PCtrl {

	private float threshold = 0.2f;//しきい値
	private float Pparam = 400;//しきい値と実際の量の差に掛けるやつ


	//操作量を算出
	public float calcBrightnessCtrl(float bright) {

		float p = bright - threshold;

		p *= Pparam;

		return  p;
	}


	public float getThreshold() {
		return threshold;
	}


	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}


}