package Drive;

public class ForwardOperator {

	public MotorOperater wmctrl;
	private float working;

	public ForwardOperator(){
		wmctrl = new MotorOperater();
	}

	public void setOperate(float forward){
		this.working = forward;
	}

	public float getOperate(){
		return this.working;
	}

	public void MotorCtrl(){
		wmctrl.setForwardValue(working);
		wmctrl.Drive();
	}
}
