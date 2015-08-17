//P制御

package Drive;

public class PCtrl {

	private static final float THRESHOLD = 0.2f;//しきい値
	private float Pparam = 200;


	//操作量を算出
	public float calcBrightnessCtrl(float bright) {
		
		float p = bright - THRESHOLD;
    	
    	p *= Pparam;
		
		return  p;
	}

}
