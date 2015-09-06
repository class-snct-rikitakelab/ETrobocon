package Drive;

import log.TurnForwardKeeper;

public class ForwardCtrl {

	ForwardOperator fo;
	TurnForwardKeeper keeper;

	float target;

	ForwardCtrl(){
		fo = new ForwardOperator();
		keeper = new TurnForwardKeeper();
		target = 0.0F;
	}

	public void setTarget(float target){
		this.target= target;
	}

	public void OperateForward(){
		fo.setOperate(target);
		keeper.setForward(target);
		fo.MotorCtrl();
	}

}