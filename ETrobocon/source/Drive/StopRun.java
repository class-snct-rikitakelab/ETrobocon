package Drive;


public class StopRun {

	private static CheckDistance cDistance;
	private static CheckGray cGray;
	private static CheckCurve cCurve;

	private static final float targetDistance = 1000;//停止目標距離 この３つは変更必要
	private static final float targetCurve = 0.5f;//カーブ判定角度
	private static final float targetNotCurve = 0.2f;//カーブ判定してからカーブしていないと判定する角度


	private int grayCount = 0;// 灰色の部分を何回通ったか 二回通ったら止まる信号を出す
	private boolean isGray = false;//灰色の部分であるときに最初の一度だけカウント増加する用
	private int curveCount = 0;
	private boolean isCurve = false;
	private float distanceFromCurve = 0;



	public boolean JudgeGoal(){
		// カーブを測る処理 ************************************************************************
		if(cCurve.calcCurve()>targetCurve){
			if(!isCurve){
				++curveCount;
				isCurve = true;
			}
			//目標角度以上なら他の部分リセット
			grayCount = 0;
			isGray = false;
			distanceFromCurve = cDistance.getDistance();
		}else if(cCurve.calcCurve()<targetNotCurve){
			isCurve = false;
		}

		if(curveCount>=2){//カーブ回数が少ないとこっちの処理に入らないため常にfalseを返す

			// 距離を測る処理 **************************************************************************
			float distance = cDistance.getDistance();


			// 灰色を測る処理 **************************************************************************
			if(cGray.checkGray() || !isGray){
				++grayCount;//ここを灰色の部分だと判定
				isGray = true;
			}else{isGray = false;}

			if(grayCount >= 2){return true;}
			if((distance-distanceFromCurve)>targetDistance){return true;}

		}


		return false;

	}
}
