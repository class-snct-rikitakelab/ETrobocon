public class Log {
	private String timestamp;
	private float turn;
	private float forward;
	private float brightness;
	
	public void setTimestamp(String timestamp){
	     	this.timestamp = timestamp;
	}
	public String getTimestamp(void){
		return timestamp;
	}
	public void setTurn(float turn){
	     	this.turn = turn;
	}
	public float getTurn(void){
		return turn;
	}
	public void setForward(float forward){
	     	this.forward = forward;
	}
	public static float getForward(void){
		return forward;
	}
	public void setBrightness(float brightness){
	     	this.brightness = brightness;
	}
	public float getBrightness(void){
		return brightness;
	}
}
	