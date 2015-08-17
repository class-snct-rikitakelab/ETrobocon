package Driving;

public class ForwardOperator {

	WheelMotorCtrl wmctrl;
	private float working;

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
