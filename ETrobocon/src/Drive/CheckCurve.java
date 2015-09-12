package Drive;


public class CheckCurve {

	tachoGetter tacho = new tachoGetter();

	private static final int CALC_DIST = 4;//計算に使うデータの間隔
	private static final int CALC_COUNT = 5;//計算回数
	private static final int ROT_COUNT = CALC_DIST * CALC_COUNT + 1;

	private int[] rotL;
	private int[] rotR;
	private float rotSum = 0;
	private char callCount = 0;

	CheckCurve(){
		rotL = new int[ROT_COUNT];
		rotR = new int[ROT_COUNT];
		for(int i=0;i<ROT_COUNT;++i){
			rotL[i] = 0;
			rotR[i] = 0;
		}
	}

	public float calcCurve(){
		if(++callCount >= 10){//計算負荷とかメモリ消費とかを軽減するため何回かに一回だけ回転数読み込み・計算する

			callCount = 0;
			float rotCL;
			float rotCR;
			rotSum = 0;

			for(int i=ROT_COUNT-1;i>0;--i){
				rotL[i] = rotL[i-1];
				rotR[i] = rotR[i-1];
			}
			rotL[0] = tacho.getTachoCountL();
			rotR[0] = tacho.getTachoCountR();

			// 偏差を求めて足して合計を足した回数で割る
			for(int i=0;i<CALC_COUNT;++i){
				rotCL = rotL[i*CALC_DIST] - rotL[(i+1)*CALC_DIST];
				rotCR = rotR[i*CALC_DIST] - rotR[(i+1)*CALC_DIST];
				rotSum += (rotCL - rotCR) / (rotCL + rotCR + 1);
			}
			rotSum /= CALC_COUNT;
		}
		return rotSum;

	}

}
