package Driving;

public class ForwardCtrl {

	ForwardOperator fo;

	float target;

	ForwardController(){
		fo = new ForwardOperator();
		target = 0.0F;
	}

	public void setTarget(float target){
		this.target= target;
	}

	public void OperateForward(){
		fo.setworking(target);
	}

}
