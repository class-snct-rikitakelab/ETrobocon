package Drive;

public class TurnOperater {

	private MotorOperater wmctrl;
	private float operate;

	public TurnOperater(){
		wmctrl = new MotorOperater();
	}

	/**
	 *
	 */
	public void MotorCtrl() {
		wmctrl.setTurnValue(operate);
		//wmctrl.Drive();

	}

	/**
	 * @param turn
	 *
	 */
	public void setOperate(float op) {
		operate = op;
		return;
	}

	public float getOperate(){
		return operate;
	}

}
