package com.george.assignment3cameragalleryapp;

/**
 * Created by George on 2016-01-28.
 */


import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;

public class accelerometer implements SensorEventListener {

    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;
    private static final int maxShakeDur = 1000;
    private static final int minShakeAcc = 5;
    private static final int minMove = 3;
    private float[] mGrav = { 0.0f, 0.0f, 0.0f };
    private float[] mLinAcc = { 0.0f, 0.0f, 0.0f };
    private OnShakeListener onshakeL;

    long time_start = 0;
    int count_move = 0;

    public accelerometer(OnShakeListener shakeListener) {
        onshakeL = shakeListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        setCurrAcc(event);
        float maxLinAccF = getMaxCurrentLinearAcceleration();
        if (maxLinAccF > minShakeAcc) {
            long now = System.currentTimeMillis();

            if (time_start == 0) {
                time_start = now;
            }

            long timeElap = now - time_start;
            if (timeElap > maxShakeDur) {
                resetShakeDetection();
            }
            else {
                count_move++;
                if (count_move > minMove) {
                    onshakeL.onShake();
                    resetShakeDetection();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void setCurrAcc(SensorEvent event) {
        final float alpha = 0.8f;
        mGrav[X] = alpha * mGrav[X] + (1 - alpha) * event.values[X];
        mGrav[Y] = alpha * mGrav[Y] + (1 - alpha) * event.values[Y];
        mGrav[Z] = alpha * mGrav[Z] + (1 - alpha) * event.values[Z];
        mLinAcc[X] = event.values[X] - mGrav[X];
        mLinAcc[Y] = event.values[Y] - mGrav[Y];
        mLinAcc[Z] = event.values[Z] - mGrav[Z];

    }

    private float getMaxCurrentLinearAcceleration() {
        float maxLinAccF = mLinAcc[X];
        if (mLinAcc[Y] > maxLinAccF) {
            maxLinAccF = mLinAcc[Y];
        }
        if (mLinAcc[Z] > maxLinAccF) {
            maxLinAccF = mLinAcc[Z];
        }
        return maxLinAccF;
    }

    private void resetShakeDetection() {
        time_start = 0;
        count_move = 0;
    }

    public interface OnShakeListener {
        public void onShake();
    }
}