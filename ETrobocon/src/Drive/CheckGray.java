package Drive;

public class CheckGray {

	BrightnessMeasure bMeasure = new BrightnessMeasure();

	private static final int grayC = 200;//灰色を何回検知したら灰色の部分とするか 変更必要
	private static float GRAYTOWHITE = 0.15f;
	private static float BLACKTOGRAY = 0.05f;

	private int grayPoint = 0;// 灰色を検知すると増える 黒を検知で0に これが一定以上になったら灰色の部分である

	public boolean checkGray(){

		float bright;
		bright = bMeasure.getBrightness();
		if(bright>GRAYTOWHITE){//白の時
			;
		}
		else if(bright>BLACKTOGRAY){//灰色の時
			grayPoint++;
		}
		else{//黒の時
			grayPoint = 0;
		}



		if(grayPoint>grayC)return true;
		else return false;
	}

	public int getGrayPoint(){
		return grayPoint;
	}

	public void setGrayPoint(int gray){
		grayPoint = gray;
	}

	public void setTargetWhite(float tval){
		GRAYTOWHITE = tval;
	}

	public void setTargetBlack(float tval){
		BLACKTOGRAY = tval;
	}


}
