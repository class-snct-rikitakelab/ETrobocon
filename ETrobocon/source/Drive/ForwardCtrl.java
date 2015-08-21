package Drive;

public class ForwardCtrl {

	ForwardOperator fo;

	float target;

	ForwardCtrl(){
		fo = new ForwardOperator();
		target = 0.0F;
	}

	public void setTarget(float target){
		this.target= target;
	}

	public void OperateForward(){
		fo.setOperate(target);
	}

}
