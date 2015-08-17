/**
******************************************************************************
** FILE NAME : Balancer.java
**
** ABSTRUCT : Two wheeled self-balancing robot "NXTway-GS" balance control program.
** NXTway-GS balance control algorithum is based on modern control theory
** which is servo control (state + integral feedback).
** To develop the controller and indentify the plant, The MathWorks
** MATLAB&Simulink had been used and this design methodology is
** called MBD (Model-Based Design/Development). This C source code
** is automatically generated from a Simulink model by using standard features
** of Real-Time Workshop Embedded Coder. All control parameters need to be defined
** by user and the sample parameters are defined in nxtOSEK\samples\nxtway_gs\balancer_param.c.
** For more detailed information about the controller alogorithum, please check:
** English : http://www.mathworks.com/matl･･･/fileexchange/loadFile.do･･･
** Japanese: http://www.cybernet.co.jp/･･･/library/library/detail.php･･･
**
** MODEL INFO:
** MODEL NAME : balancer.mdl
** VERSION : 1.893
** HISTORY : y_yama - Tue Sep 25 11:37:09 2007
** takashic - Sun Sep 28 17:50:53 2008
** INACHI Minoru - Thu Apr 16 00:10:24 2015
** ported from balancer.c
**
** Copyright (c) 2009-2015 MathWorks, Inc.
** All rights reserved.
******************************************************************************
**/


/**
 * Balancer クラス
 */
public class Balancer {
	/*
	 * Low-pass filter coefficient (for average rotation angle of the left and right wheels)
	 */
	private static float A_D = 0.8F;

	/*
	 *  Low-pass filter coefficient (for target average rotation angle of the left and right wheels)
	 */
	private static float A_R = 0.996F;

	/*
	* State feedback coefficient for servo control
	* K_F [0]: wheel rotation angular coefficient
	* K_F [1]: the body tilt angle coefficient
	* K_F [2]: wheel rotation angular velocity coefficient
	* K_F [3]: the body tilt angular velocity coefficient
	*/
	private static float[] K_F = new float[] { -0.870303F, -31.9978F, -1.1566F*0.9F,
		-2.78873F };

	/*
	 * Servo control integral feedback coefficient
	 */
	private static float K_I = -0.44721F;

	/*
	 * Target plane rotation speed of the vehicle body (dφ / dt) coefficient
	 */
	private static float K_PHIDOT = 25.0F*2.5F;

	/*
	 * Average rotational speed of the left and right wheels (dθ / dt) coefficient
	 */
	private static float K_THETADOT = 7.5F;

	/*
	 * For PWM output calculated battery voltage correction coefficient
	 */
	private static float BATTERY_GAIN = 0.001089F;

	/*
	 * PWM output calculation for the battery voltage correction offset
	 */
	private static float BATTERY_OFFSET = 0.625F;

    /*
     *  Forward / turning instruction absolute maximum
     */
	private static final float CMD_MAX = 100.0F;

	/*
	 * Angle unit conversion factor (= pi / 180)
	 */
	private static final float DEG2RAD = 0.01745329238F;

	 /*
	  *  Balance control execution period (s)
	  */
	private static final float EXEC_PERIOD = 0.00400000019F;

	private static float ud_err_theta = 0.0F;            /*  average rotational angle of the left and right wheels (θ) target error state value */
	private static float ud_psi = 0.0F;                  /* body pitch angle (ψ) state value */
	private static float ud_theta_lpf = 0.0F;            /* average rotational angle of the left and right wheels (θ) state value */
	private static float ud_theta_ref = 0.0F;            /* target average rotation angle of the left and right wheels (θ) state value */
	private static float ud_thetadot_cmd_lpf = 0.0F;     /*  the left and right wheels of the target average rotation angular velocity (dθ / dt) status value */

	private static int pwm_l = 0;                        /* the left motor PWM output value */
	private static int pwm_r = 0;                        /* the right motor PWM output value */

	private static float[] tmp = new float[4];
	private static float[] tmp_theta_0 = new float[4];

	/**
	 *  Get a low-pass filter coefficient (for the average rotation angle of the left and right wheels).
	 * @return The low-pass filter coefficient (for the average rotation angle of the left and right wheels)
	 */
	public static final float getAD() {
		return A_D;
	}

	/**
	 * Set the low-pass filter coefficient (for the average rotation angle of the left and right wheels).
	 * @param a_d low-pass filter coefficient (for the average rotation angle of the left and right wheels)
	 */
	public static final void setAD(float a_d) {
		A_D = a_d;
	}

	/**
	 * Get a low-pass filter coefficient (for a target average rotation angle of the left and right wheels).
	 * @return The low-pass filter coefficient (for the target average rotation angle of the left and right wheels)
	 */
	public static final float getAR() {
		return A_R;
	}

	/**
	 * Set the low-pass filter coefficient (for the target average rotation angle of the left and right wheels).
	 * @param a_r low-pass filter coefficient (for a target average rotation angle of the left and right wheels)
	 */
	public static final void setAR(float a_r) {
		A_R = a_r;
	}

	/**
	 * Get the state feedback coefficient for the servo control.
	 * @return Servo control for state feedback coefficient <br>
	 *            [0]: wheel rotation angular coefficient [1]: the body tilt angle coefficient [2]: wheel rotation angular velocity coefficient [3]:
	 *            Vehicle body inclination angular velocity coefficient
	 */
	public static final float[] getKF() {
		return K_F;
	}

	/**
	 * Set the state feedback coefficient for the servo control.
	 * @param k_f servo control for state feedback coefficient <br>
	 *            k_f[0]:  wheel rotation angular coefficient k_f [1]: the body tilt angle coefficient k_f [2]: wheel rotation angular velocity coefficient k_f [3]:
	 *            Vehicle body inclination angular velocity coefficient
	 */
	public static final void setKF(float[] k_f) {
		K_F = k_f;
	}

	/**
	 * Get a servo control integration factor.
	 * @param k_i servo control for integral coefficient
	 */
	public static final float getKI() {
		return K_I;
	}

	/**
	 * Set the servo control integration factor.
	 * @param k_i servo control for integral coefficient
	 */
	public static final void setKI(float k_i) {
		K_I = k_i;
	}

	/**
	 *  Get a target plane rotation speed (dφ / dt) coefficient of the vehicle body.
	 * @return Target plane rotation speed of the vehicle body (dφ / dt) coefficient
	 */
	public static final float getKPhidot() {
		return K_PHIDOT;
	}

	/**
	 * Set the target plane rotation speed (dφ / dt) coefficient of the vehicle body.
	 * @param k_phidot target plane rotation speed of the vehicle body (dφ / dt) coefficient
	 */
	public static final void setKPhidot(float k_phidot) {
		K_PHIDOT = k_phidot;
	}

	/**
	 * Get the average rotational speed (dθ / dt) coefficient of the left and right wheels.
	 * @return Average rotational speed of the left and right wheels (dθ / dt) coefficient
	 */
	public static final float getKThetadot() {
		return K_THETADOT;
	}

	/**
	 * Average rotational speed of the left and right wheels I set the (dθ / dt) coefficient.
	 * @param k_thetadot average rotational speed of the left and right wheels (dθ / dt) coefficient
	 */
	public static final void setKThetadot(float k_thetadot) {
		K_THETADOT = k_thetadot;
	}

	/**
	 * Get a PWM output calculation for the battery voltage correction coefficient.
	 * @return The battery voltage correction coefficient for the PWM output calculation
	 */
	public static final float getBatteryGain() {
		return BATTERY_GAIN;
	}

	/**
	 * Set the PWM output calculation for the battery voltage correction coefficient.
	 * @param battery_gain battery voltage correction coefficient for the PWM output calculation
	 */
	public static final void setBatteryGain(float battery_gain) {
		BATTERY_GAIN = battery_gain;
	}

	/**
	 * Get a PWM output calculation for the battery voltage correction offset.
	 * @return PWM output calculated for the battery voltage correction offset
	 */
	public static final float getBatteryOffset() {
		return BATTERY_OFFSET;
	}

	/**
	 * Set the PWM output calculation for the battery voltage correction offset.
	 * @param battery_offset PWM output calculated for the battery voltage correction offset
	 */
	public static void setBatteryOffset(float battery_offset) {
		BATTERY_OFFSET = battery_offset;
	}

	/**
	 *  EV3way-ET balance control initialization method.
     * It will initialize the internal state quantity variable. To initialize the balance control function by this method,
     * Please reset the encoder values ​​of the left and right wheel drive motor to 0 together.
	 */
	/* Model initialize function */
	public static final void init() {
		/* Registration code */

		/* states (dwork) */

		/* custom states */
		ud_err_theta = 0.0F;
		ud_theta_ref = 0.0F;
		ud_thetadot_cmd_lpf = 0.0F;
		ud_psi = 0.0F;
		ud_theta_lpf = 0.0F;
	}

	/**
	 *  EV3way-GS balance control method.
	 * After executing this method, you get the left and right motor PMW output value in getPwmL and getPwmR.
	 *
	 * This method is designed on the assumption that it is started in 4msec cycle.
	 * The left and right wheel drive motors due to individual differences, if the rotational speed can be given the same PWM output is different
	 * It has. You need to add a separate correction function if the.
	 *
	 * @param args_cmd_forward
	 *            Forward / backward command. 100 (forward maximum value) to -100 (reverse maximum value)
	 * @param args_cmd_turn
	 *            Turning instruction. 100 (right turn maximum) to -100 (left turn maximum)
	 * @param args_gyro
	 *            Gyro sensor value
	 * @param args_gyro_offset
	 *            Gyro sensor offset value
	 * @param args_theta_m_l
	 *           Left motor encoder value
	 * @param args_theta_m_r
	 *            The right motor encoder value
	 * @param args_battery
	 *            Battery voltage value (mV)
	 */
	/* Model step function */
	public static final void control(float args_cmd_forward, float args_cmd_turn,
			float args_gyro, float args_gyro_offset, float args_theta_m_l,
			float args_theta_m_r, float args_battery) {
		float tmp_theta;
		float tmp_theta_lpf;
		float tmp_pwm_r_limiter;
		float tmp_psidot;
		float tmp_pwm_turn;
		float tmp_pwm_l_limiter;
		float tmp_thetadot_cmd_lpf;
		int tmp_0;

		/*
		 * Sum: '<S8>/Sum' incorporates: Constant: '<S3>/Constant6' Constant:
		 * '<S8>/Constant' Constant: '<S8>/Constant1' Gain: '<S3>/Gain1' Gain:
		 * '<S8>/Gain2' Inport: '<Root>/cmd_forward' Product: '<S3>/Divide'
		 * Product: '<S8>/Product' Sum: '<S8>/Sum1' UnitDelay: '<S8>/Unit Delay'
		 */
		tmp_thetadot_cmd_lpf = (((args_cmd_forward / CMD_MAX) * K_THETADOT) * (1.0F - A_R))
				+ (A_R * ud_thetadot_cmd_lpf);

		/*
		 * Gain: '<S4>/Gain' incorporates: Gain: '<S4>/deg2rad' Gain:
		 * '<S4>/deg2rad1' Inport: '<Root>/theta_m_l' Inport: '<Root>/theta_m_r'
		 * Sum: '<S4>/Sum1' Sum: '<S4>/Sum4' Sum: '<S4>/Sum6' UnitDelay:
		 * '<S10>/Unit Delay'
		 */
		tmp_theta = (((DEG2RAD * args_theta_m_l) + ud_psi) + ((DEG2RAD * args_theta_m_r) + ud_psi)) * 0.5F;

		/*
		 * Sum: '<S11>/Sum' incorporates: Constant: '<S11>/Constant' Constant:
		 * '<S11>/Constant1' Gain: '<S11>/Gain2' Product: '<S11>/Product' Sum:
		 * '<S11>/Sum1' UnitDelay: '<S11>/Unit Delay'
		 */
		tmp_theta_lpf = ((1.0F - A_D) * tmp_theta) + (A_D * ud_theta_lpf);

		/*
		 * Gain: '<S4>/deg2rad2' incorporates: Inport: '<Root>/gyro'
		 * Sum: '<S4>/Sum2'
		 */
		tmp_psidot = (args_gyro - args_gyro_offset) * DEG2RAD;

		/*
		 * Gain: '<S2>/Gain' incorporates: Constant: '<S3>/Constant2' Constant:
		 * '<S3>/Constant3' Constant: '<S6>/Constant' Constant: '<S9>/Constant'
		 * Gain: '<S1>/FeedbackGain' Gain: '<S1>/IntegralGain' Gain:
		 * '<S6>/Gain3' Inport: '<Root>/battery' Product: '<S2>/Product'
		 * Product: '<S9>/Product' Sum: '<S1>/Sum2' Sum: '<S1>/sum_err' Sum:
		 * '<S6>/Sum2' Sum: '<S9>/Sum' UnitDelay: '<S10>/Unit Delay' UnitDelay:
		 * '<S11>/Unit Delay' UnitDelay: '<S5>/Unit Delay' UnitDelay: '<S7>/Unit
		 * Delay'
		 */
		tmp[0] = ud_theta_ref;
		tmp[1] = 0.0F;
		tmp[2] = tmp_thetadot_cmd_lpf;
		tmp[3] = 0.0F;
		tmp_theta_0[0] = tmp_theta;
		tmp_theta_0[1] = ud_psi;
		tmp_theta_0[2] = (tmp_theta_lpf - ud_theta_lpf) / EXEC_PERIOD;
		tmp_theta_0[3] = tmp_psidot;
		tmp_pwm_r_limiter = 0.0F;
		for (tmp_0 = 0; tmp_0 < 4; tmp_0++) {
			tmp_pwm_r_limiter += (tmp[tmp_0] - tmp_theta_0[tmp_0])
					* K_F[(tmp_0)];
		}

		tmp_pwm_r_limiter = (((K_I * ud_err_theta) + tmp_pwm_r_limiter) / ((BATTERY_GAIN * args_battery) - BATTERY_OFFSET)) * 100.0F;

		/*
		 * Gain: '<S3>/Gain2' incorporates: Constant: '<S3>/Constant1' Inport:
		 * '<Root>/cmd_turn' Product: '<S3>/Divide1'
		 */
		tmp_pwm_turn = (args_cmd_turn / CMD_MAX) * K_PHIDOT;

		/* Sum: '<S2>/Sum' */
		tmp_pwm_l_limiter = tmp_pwm_r_limiter + tmp_pwm_turn;

		/* Saturate: '<S2>/pwm_l_limiter' */
		tmp_pwm_l_limiter = rt_SATURATE(tmp_pwm_l_limiter, -100.0F, 100.0F);

		/*
		 * Outport: '<Root>/pwm_l' incorporates: DataTypeConversion: '<S1>/Data
		 * Type Conversion'
		 */
		pwm_l = (int) tmp_pwm_l_limiter;

		/* Sum: '<S2>/Sum1' */
		tmp_pwm_r_limiter -= tmp_pwm_turn;

		/* Saturate: '<S2>/pwm_r_limiter' */
		tmp_pwm_r_limiter = rt_SATURATE(tmp_pwm_r_limiter, -100.0F, 100.0F);

		/*
		 * Outport: '<Root>/pwm_r' incorporates: DataTypeConversion: '<S1>/Data
		 * Type Conversion6'
		 */
		pwm_r = (int) tmp_pwm_r_limiter;

		/*
		 * Sum: '<S7>/Sum' incorporates: Gain: '<S7>/Gain' UnitDelay: '<S7>/Unit
		 * Delay'
		 */
		tmp_pwm_l_limiter = (EXEC_PERIOD * tmp_thetadot_cmd_lpf) + ud_theta_ref;

		/*
		 * Sum: '<S10>/Sum' incorporates: Gain: '<S10>/Gain' UnitDelay:
		 * '<S10>/Unit Delay'
		 */
		tmp_pwm_turn = (EXEC_PERIOD * tmp_psidot) + ud_psi;

		/*
		 * Sum: '<S5>/Sum' incorporates: Gain: '<S5>/Gain' Sum: '<S1>/Sum1'
		 * UnitDelay: '<S5>/Unit Delay' UnitDelay: '<S7>/Unit Delay'
		 */
		tmp_pwm_r_limiter = ((ud_theta_ref - tmp_theta) * EXEC_PERIOD)
				+ ud_err_theta;

		/* user code (Update function Body) */
		/* System '<Root>' */
		/* 次回演算用状態量保存処理 */

		/* Update for UnitDelay: '<S5>/Unit Delay' */
		ud_err_theta = tmp_pwm_r_limiter;

		/* Update for UnitDelay: '<S7>/Unit Delay' */
		ud_theta_ref = tmp_pwm_l_limiter;

		/* Update for UnitDelay: '<S8>/Unit Delay' */
		ud_thetadot_cmd_lpf = tmp_thetadot_cmd_lpf;

		/* Update for UnitDelay: '<S10>/Unit Delay' */
		ud_psi = tmp_pwm_turn;

		/* Update for UnitDelay: '<S11>/Unit Delay' */
		ud_theta_lpf = tmp_theta_lpf;
	}

	/**
	 *  Left motor PMW output value acquisition
	 *
	 * @return 100 (forward maximum value) to -100 (reverse maximum value)
	 */
	/* left motor PWM output */
	public static final int getPwmL() {
		return pwm_l;
	}

	/**
	 * The right motor PMW output value acquisition
	 *
	 * @return 100 (forward maximum value) to -100 (reverse maximum value)
	 */
	/* right motor PWM output */
	public static final int getPwmR() {
		return pwm_r;
	}

	/* rt_SATURATE.h */
	private static final float rt_SATURATE(float sig, float ll, float ul) {
		return (((sig) >= (ul)) ? (ul) : (((sig) <= (ll)) ? (ll) : (sig)));
	}
}
