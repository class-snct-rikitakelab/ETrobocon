package Drive;

public class CheckDistance {

	tachoGetter tacho = new tachoGetter();

	private float distance;

	public float getDistance(){
		int distL = tacho.getTachoCountL();
		int distR = tacho.getTachoCountR();

		distance = (distL + distR) / 2;

		return distance;
	}

}
