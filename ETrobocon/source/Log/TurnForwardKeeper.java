public class TurnForwardKeeper{
	private static float turn = 0.0F;
	private static float forward = 0.0F;
	
	public static void setTurn(float turnvalue){
		turn = turnvalue;
	}
	public static void setForward(float forwardvalue){
		forward = forwardvalue;
	}
	public static float getTurn(void){
		return turn;
	}
	public static float getForward(void){
		return forward;
	}
}