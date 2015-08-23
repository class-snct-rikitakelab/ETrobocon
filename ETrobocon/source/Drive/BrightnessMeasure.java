

package Drive;

public class BrightnessMeasure {

	private EV3Body body;

	public BrightnessMeasure(){
		body = new EV3Body();
	}

	public float getBrightness() {
		return body.getBrightness();
	}
}
