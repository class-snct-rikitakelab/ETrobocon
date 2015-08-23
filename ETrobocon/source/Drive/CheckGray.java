package Drive;

public class CheckGray {

	BrightnessMeasure bMeasure;

	private static final int grayC = 400;//灰色を何回検知したら灰色の部分とするか 変更必要
	private static final float WHITETOGRAY = 0.10f;
	private static final float GRAYTOBRACK = 0.30f;

	private int grayPoint = 0;// 灰色を検知すると増える 黒を検知で0に これが一定以上になったら灰色の部分である

	public boolean checkGray(){

		float bright;
		bright = bMeasure.getBrightness();
		if(bright<WHITETOGRAY){//白の時
			;
		}
		else if(bright<GRAYTOBRACK){//灰色の時
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
}
