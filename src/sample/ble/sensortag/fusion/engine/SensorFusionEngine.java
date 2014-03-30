/************************************************************************************
 * Copyright (c) 2012 Paul Lawitzki
 *               2014 Steven Rudenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 ************************************************************************************/

package sample.ble.sensortag.fusion.engine;

import android.hardware.SensorManager;
import sample.ble.sensortag.config.AppConfig;

import java.util.TimerTask;

/**
 * Sensor Fusion engine implemented on recommendations from
 * http://www.thousand-thoughts.com/2012/03/android-sensor-fusion-tutorial/
 *
 * Created by steven on 10/18/13.
 */
public class SensorFusionEngine extends TimerTask {

    // some consts for performance optimization
    private static final float PI = (float)Math.PI;
    private static final float TWO_PI = (float)(2.0 * Math.PI);
    private static final float HALF_PI = (float)(0.5 * Math.PI);

    private static final float EPSILON = 0.000000001f;
    private static final float NS2S = 1.0f / 1000000000.0f;

    private static final float FILTER_COEFFICIENT = 0.98f;

    /**
     * Angular speeds from gyro
     */
    private final float[] gyro = new float[3];

    /**
     * Rotation matrix from gyro data
     */
    private float[] gyroMatrix = new float[9];
    /**
     * Indicates whether gyroMatrix is initialized
     */
    private boolean isGyroInitialized = false;

    /**
     * Orientation angles from gyro matrix
     */
    private final float[] gyroOrientation = new float[3];

    /**
     * Magnetic field vector
     */
    private final float[] magnet = new float[3];

    // accelerometer vector
    private final float[] accel = new float[3];

    // orientation angles from accel and magnet
    private final float[] accMagOrientation = new float[3];

    // final orientation angles from sensor fusion
    private final float[] fusedOrientation = new float[3];

    // accelerometer and magnetometer based rotation matrix
    private final float[] rotationMatrix = new float[9];
    private float timestamp;

    public SensorFusionEngine() {
        gyroOrientation[0] = 0.0f;
        gyroOrientation[1] = 0.0f;
        gyroOrientation[2] = 0.0f;

        // initialise gyroMatrix with identity matrix
        gyroMatrix[0] = 1.0f; gyroMatrix[1] = 0.0f; gyroMatrix[2] = 0.0f;
        gyroMatrix[3] = 0.0f; gyroMatrix[4] = 1.0f; gyroMatrix[5] = 0.0f;
        gyroMatrix[6] = 0.0f; gyroMatrix[7] = 0.0f; gyroMatrix[8] = 1.0f;
    }

    public float[] getFusedOrientation() {
        return fusedOrientation;
    }

    public void onAccDataUpdate(float[] accel) {
        System.arraycopy(accel, 0, this.accel, 0, 3);
        calculateAccMagOrientation();
    }

    public void onMagDataUpdate(float[] magnet) {
        if (AppConfig.SENSOR_FUSION_USE_MAGNET_SENSOR)
            System.arraycopy(magnet, 0, this.magnet, 0, 3);
    }

    public void onGyroDataUpdate(float[] gyro) {
        // initialisation of the gyroscope based rotation matrix
        if (!isGyroInitialized) {
            float[] initMatrix = getRotationMatrixFromOrientation(accMagOrientation);
            gyroMatrix = matrixMultiplication(gyroMatrix, initMatrix);
            isGyroInitialized = true;
        }

        // copy the new gyro values into the gyro array
        // convert the raw gyro data into a rotation vector
        float[] deltaVector = new float[4];
        final long currentTimestamp = System.nanoTime();
        if(timestamp != 0) {
            final float dT = (currentTimestamp - timestamp) * NS2S;
            System.arraycopy(gyro, 0, this.gyro, 0, 3);
            getRotationVectorFromGyro(this.gyro, deltaVector, dT / 2.0f);
        }

        // measurement done, save current time for next interval
        timestamp = currentTimestamp;

        // convert rotation vector into rotation matrix
        float[] deltaMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaVector);

        // apply the new rotation interval on the gyroscope based rotation matrix
        gyroMatrix = matrixMultiplication(gyroMatrix, deltaMatrix);

        // get the gyroscope based orientation from the rotation matrix
        SensorManager.getOrientation(gyroMatrix, gyroOrientation);
    }

    // calculates orientation angles from accelerometer and magnetometer output
    public void calculateAccMagOrientation() {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet)) {
            SensorManager.getOrientation(rotationMatrix, accMagOrientation);
        }
    }

    // This function is borrowed from the Android reference
    // at http://developer.android.com/reference/android/hardware/SensorEvent.html#values
    // It calculates a rotation vector from the gyroscope angular speed values.
    private void getRotationVectorFromGyro(float[] gyroValues,
                                           float[] deltaRotationVector,
                                           float timeFactor)
    {
        float[] normValues = new float[3];

        // Calculate the angular speed of the sample
        float omegaMagnitude =
                (float)Math.sqrt(gyroValues[0] * gyroValues[0] +
                        gyroValues[1] * gyroValues[1] +
                        gyroValues[2] * gyroValues[2]);

        // Normalize the rotation vector if it's big enough to get the axis
        if (omegaMagnitude > EPSILON) {
            normValues[0] = gyroValues[0] / omegaMagnitude;
            normValues[1] = gyroValues[1] / omegaMagnitude;
            normValues[2] = gyroValues[2] / omegaMagnitude;
        }

        // Integrate around this axis with the angular speed by the timestep
        // in order to get a delta rotation from this sample over the timestep
        // We will convert this axis-angle representation of the delta rotation
        // into a quaternion before turning it into the rotation matrix.
        float thetaOverTwo = omegaMagnitude * timeFactor;
        float sinThetaOverTwo = (float)Math.sin(thetaOverTwo);
        float cosThetaOverTwo = (float)Math.cos(thetaOverTwo);
        deltaRotationVector[0] = sinThetaOverTwo * normValues[0];
        deltaRotationVector[1] = sinThetaOverTwo * normValues[1];
        deltaRotationVector[2] = sinThetaOverTwo * normValues[2];
        deltaRotationVector[3] = cosThetaOverTwo;
    }

    private float[] getRotationMatrixFromOrientation(float[] o) {
        float[] xM = new float[9];
        float[] yM = new float[9];
        float[] zM = new float[9];

        float sinX = (float)Math.sin(o[1]);
        float cosX = (float)Math.cos(o[1]);
        float sinY = (float)Math.sin(o[2]);
        float cosY = (float)Math.cos(o[2]);
        float sinZ = (float)Math.sin(o[0]);
        float cosZ = (float)Math.cos(o[0]);

        // rotation about x-axis (pitch)
        xM[0] = 1.0f; xM[1] = 0.0f; xM[2] = 0.0f;
        xM[3] = 0.0f; xM[4] = cosX; xM[5] = sinX;
        xM[6] = 0.0f; xM[7] = -sinX; xM[8] = cosX;

        // rotation about y-axis (roll)
        yM[0] = cosY; yM[1] = 0.0f; yM[2] = sinY;
        yM[3] = 0.0f; yM[4] = 1.0f; yM[5] = 0.0f;
        yM[6] = -sinY; yM[7] = 0.0f; yM[8] = cosY;

        // rotation about z-axis (azimuth)
        zM[0] = cosZ; zM[1] = sinZ; zM[2] = 0.0f;
        zM[3] = -sinZ; zM[4] = cosZ; zM[5] = 0.0f;
        zM[6] = 0.0f; zM[7] = 0.0f; zM[8] = 1.0f;

        // rotation order is y, x, z (roll, pitch, azimuth)
        float[] resultMatrix = matrixMultiplication(xM, yM);
        resultMatrix = matrixMultiplication(zM, resultMatrix);
        return resultMatrix;
    }

    private static float[] matrixMultiplication(float[] A, float[] B) {
        float[] result = new float[9];

        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];

        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];

        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];

        return result;
    }

    @Override
    public void run() {
        float oneMinusCoeff = 1.0f - FILTER_COEFFICIENT;

        /*
         * Fix for 179° <--> -179° transition problem:
         * Check whether one of the two orientation angles (gyro or accMag) is negative while the other one is positive.
         * If so, add 360° (2 * math.PI) to the negative value, perform the sensor fusion, and remove the 360° from the result
         * if it is greater than 180°. This stabilizes the output in positive-to-negative-transition cases.
         */

        // azimuth
        if (gyroOrientation[0] < -HALF_PI && accMagOrientation[0] > 0.0) {
            fusedOrientation[0] = FILTER_COEFFICIENT * (gyroOrientation[0] + TWO_PI) + oneMinusCoeff * accMagOrientation[0];
            if (fusedOrientation[0] > PI)
                fusedOrientation[0] -= TWO_PI;
        } else if (accMagOrientation[0] < -HALF_PI && gyroOrientation[0] > 0.f) {
            fusedOrientation[0] = FILTER_COEFFICIENT * gyroOrientation[0] + oneMinusCoeff * (accMagOrientation[0] + TWO_PI);
            if (fusedOrientation[0] > PI)
                fusedOrientation[0] -= TWO_PI;
        } else {
            fusedOrientation[0] = FILTER_COEFFICIENT * gyroOrientation[0] + oneMinusCoeff * accMagOrientation[0];
        }

        // pitch
        if (gyroOrientation[1] < -HALF_PI && accMagOrientation[1] > 0.f) {
            fusedOrientation[1] = FILTER_COEFFICIENT * (gyroOrientation[1] + TWO_PI) + oneMinusCoeff * accMagOrientation[1];
            if (fusedOrientation[1] > PI)
                fusedOrientation[1] -= TWO_PI;
        } else if (accMagOrientation[1] < -HALF_PI && gyroOrientation[1] > 0.f) {
            fusedOrientation[1] = FILTER_COEFFICIENT * gyroOrientation[1] + oneMinusCoeff * (accMagOrientation[1] + TWO_PI);
            if (fusedOrientation[1] > PI)
                fusedOrientation[1] -= TWO_PI;
        } else {
            fusedOrientation[1] = FILTER_COEFFICIENT * gyroOrientation[1] + oneMinusCoeff * accMagOrientation[1];
        }

        // roll
        if (gyroOrientation[2] < -HALF_PI && accMagOrientation[2] > 0.f) {
            fusedOrientation[2] = FILTER_COEFFICIENT * (gyroOrientation[2] + TWO_PI + oneMinusCoeff * accMagOrientation[2]);
            if (fusedOrientation[2] > PI)
                fusedOrientation[2] -= TWO_PI;
        } else if (accMagOrientation[2] < -HALF_PI && gyroOrientation[2] > 0.f) {
            fusedOrientation[2] = FILTER_COEFFICIENT * gyroOrientation[2] + oneMinusCoeff * (accMagOrientation[2] + TWO_PI);
            if (fusedOrientation[2] > PI)
                fusedOrientation[2] -= TWO_PI;
        } else {
            fusedOrientation[2] = FILTER_COEFFICIENT * gyroOrientation[2] + oneMinusCoeff * accMagOrientation[2];
        }

        // overwrite gyro matrix and orientation with fused orientation
        // to compensate gyro drift
        gyroMatrix = getRotationMatrixFromOrientation(fusedOrientation);
        System.arraycopy(fusedOrientation, 0, gyroOrientation, 0, 3);
    }
}
