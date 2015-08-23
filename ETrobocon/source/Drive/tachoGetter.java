package Drive;

public class tachoGetter {

	EV3Body body;

	public int getTachoCount(){
		return body.motorPortT.getTachoCount();
	}

	public int getTachoCountL(){
		return body.motorPortL.getTachoCount();
	}

	public int getTachoCountR(){
		return body.motorPortR.getTachoCount();
	}


}
