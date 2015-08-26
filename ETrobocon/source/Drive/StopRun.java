package Drive;

import lejos.hardware.lcd.LCD;


public class StopRun {

	private static CheckDistance cDistance = new CheckDistance();
	private static CheckGray cGray = new CheckGray();
	private static CheckCurve cCurve = new CheckCurve();

	private static final float targetDistance = 1000;//停止目標距離 この３つは変更必要
	private static final float targetCurve = 0.5f;//カーブ判定角度
	private static final float targetNotCurve = 0.1f;//カーブ判定してからカーブしていないと判定する角度


	private int grayCount = 0;// 灰色の部分を何回通ったか 二回通ったら止まる信号を出す
	private boolean isGray = false;//灰色の部分であるときに最初の一度だけカウント増加する用
	private int curveCount = 0;
	private boolean isCurve = false;
	private float distanceFromCurve = 0;



	public boolean judgeGoal(){
		// カーブを測る処理 ************************************************************************
		LCD.clear();
		float curve = cCurve.calcCurve();
		curve = Math.abs(curve);
		if(curve>targetCurve){
			if(!isCurve){
				++curveCount;
				isCurve = true;
			}
			//目標角度以上なら他の部分リセット
			cGray.setGrayPoint(0);
			grayCount = 0;
			isGray = false;
			distanceFromCurve = cDistance.getDistance();
		}else if(curve<targetNotCurve){
			isCurve = false;
		}

		LCD.drawString("Curve:" + curve, 2, 0);
		LCD.drawString("CurveC:" + curveCount, 2, 1);

		if(curveCount>=2){//カーブ回数が少ないとこっちの処理に入らないため常にfalseを返す
			// 距離を測る処理 **************************************************************************
			float distance = cDistance.getDistance();
			LCD.drawString("Dist: " + distance, 2, 2);
			LCD.drawString("DisiC:" + distanceFromCurve, 2, 3);

			// 灰色を測る処理 **************************************************************************
			int gray = cGray.getGrayPoint();
			if(cGray.checkGray()){
				if(!isGray){
					++grayCount;//ここを灰色の部分だと判定
					isGray = true;}
			}else{isGray = false;}
			LCD.drawString("Gray: " + gray, 2, 4);
			LCD.drawString("GrayC:" + grayCount, 2, 5);

			// 条件を満たしたらtrueを返す
			if(grayCount >= 2){return true;}
			if((distance-distanceFromCurve)>targetDistance){return true;}
		}

		return false;
	}

	public void reset(){
		grayCount = 0;// 灰色の部分を何回通ったか 二回通ったら止まる信号を出す
		isGray = false;//灰色の部分であるときに最初の一度だけカウント増加する用
		curveCount = 0;
		isCurve = false;
		distanceFromCurve = 0;
		cGray.setGrayPoint(0);


	}


}
