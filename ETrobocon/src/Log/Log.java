package Log;
public class Log {
	private String timestamp;
	private float turn;
	private float forward;
	private float brightness;

	public void setTimestamp(String timestamp){
		this.timestamp = timestamp;
	}
	public String getTimestamp(){
		return timestamp;
	}
	public void setTurn(float turn){
		this.turn = turn;
	}
	public float getTurn(){
		return turn;
	}
	public void setForward(float forward){
		this.forward = forward;
	}
	public float getForward(){
		return forward;
	}
	public void setBrightness(float brightness){
		this.brightness = brightness;
	}
	public float getBrightness(){
		return brightness;
	}
}