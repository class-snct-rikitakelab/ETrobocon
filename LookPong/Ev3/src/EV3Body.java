/*
 *  EV3Body.java (for leJOS EV3)
 *  Created on: 2015/05/09
 *  Author: INACHI Minoru
 *  Copyright (c) 2015 Embedded Technology Software Design Robot Contest
 */


import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.port.TachoMotorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;

/**
 *  Classes dealing with motor and sensor of EV3.
 */
final class EV3Body {
    //  Motor control object
    // EV3LargeRegulatedMotor because the can not PWM control, I will use the TachoMotorPort
    public TachoMotorPort motorPortL; // Left motor
    public TachoMotorPort motorPortR; // Right motor
    public TachoMotorPort motorPortT; // Tail motor

    // Touch sensor
    public EV3TouchSensor touch;
    public SensorMode touchMode;
    public float[] sampleTouch;

    // Ultrasonic sensor
    public EV3UltrasonicSensor sonar;
    public SampleProvider distanceMode;  // Distance detection mode
    public float[] sampleDistance;

    // Color sensor
    public EV3ColorSensor colorSensor;
    public SensorMode redMode;           // Luminance detection mode
    public float[] sampleLight;

    // Gyro sensor
    public EV3GyroSensor gyro;
    public SampleProvider rate;          // Angular velocity detection mode
    public float[] sampleGyro;

    /**
     * Constructor.
     */
    public EV3Body() {
        motorPortL = MotorPort.C.open(TachoMotorPort.class); // Left motor
        motorPortR = MotorPort.B.open(TachoMotorPort.class); // Right motor
        motorPortT = MotorPort.A.open(TachoMotorPort.class); // Tail motor

        // Touch sensor
        touch = new EV3TouchSensor(SensorPort.S1);
        touchMode = touch.getTouchMode();
        sampleTouch = new float[touchMode.sampleSize()];

        // Ultrasonic sensor
        sonar = new EV3UltrasonicSensor(SensorPort.S2);
        distanceMode = sonar.getDistanceMode(); // Distance detection mode
        sampleDistance = new float[distanceMode.sampleSize()];

        // カラーセンサ
        colorSensor = new EV3ColorSensor(SensorPort.S3);
        redMode = colorSensor.getRedMode();     // Luminance detection mode
        sampleLight = new float[redMode.sampleSize()];

        // ジャイロセンサ
        gyro = new EV3GyroSensor(SensorPort.S4);
        rate = gyro.getRateMode();              // Angular velocity detection mode
        sampleGyro = new float[rate.sampleSize()];
    }

    /**
     * Check the touch sensor is pressed
     * @return true :touch sensor is pressed.
     */
    public final boolean touchSensorIsPressed() {
        touchMode.fetchSample(sampleTouch, 0);
        return ((int)sampleTouch[0] != 0);
    }

    /**
     * By the ultrasonic sensor get the distance of the obstacle.
     * @return Distance to the obstacle (m).
     */
    public final float getSonarDistance() {
        distanceMode.fetchSample(sampleDistance, 0);
        return sampleDistance[0];
    }

    /**
     * Get the brightness value from the color sensor.
     * @return Brightness value.
     */
    public final float getBrightness() {
        redMode.fetchSample(sampleLight, 0);
        return sampleLight[0];
    }

    /**
     * Get the angular velocity from the gyro sensor.
     * @return Angular velocity.
     */
    public final float getGyroValue() {
        rate.fetchSample(sampleGyro, 0);
        return sampleGyro[0];
    }
}
