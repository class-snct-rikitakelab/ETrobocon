package Drive;

public class CheckCurve {

	tachoGetter tacho;

	private static final int CALC_DIST = 10;//計算に使うデータの間隔
	private static final int CALC_COUNT = 10;//計算回数
	private static final int ROT_COUNT = CALC_DIST * CALC_COUNT + 1;

	private int[] rotL;
	private int[] rotR;

	CheckCurve(){
		rotL = new int[ROT_COUNT];
		rotR = new int[ROT_COUNT];
	}

	public float calcCurve(){
		float rotCL;
		float rotCR;
		float rotSum = 0;

		for(int i=0;i<ROT_COUNT-1;++i){
			rotL[i+1] = rotL[i];
			rotR[i+1] = rotR[i];
		}
		rotL[0] = tacho.getTachoCountL();
		rotR[0] = tacho.getTachoCountR();

		// 偏差を求めて足して合計を足した回数で割る
		for(int i=0;i<CALC_COUNT;++i){
			rotCL = rotL[i*CALC_DIST] - rotL[(i+1)*CALC_DIST];
			rotCR = rotR[i*CALC_DIST] - rotR[(i+1)*CALC_DIST];
			rotSum += (rotCL - rotCR) / (rotCL + rotCR);
		}
		rotSum /= CALC_COUNT;

		return rotSum;

	}

}
