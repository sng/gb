package com.google.code.geobeagle.shakewaker;

import android.hardware.SensorManager;

class ForceThresholdStrategy {
    public boolean exceedsThreshold(float[] values) {
        double totalForce = 0.0f;
        totalForce += Math.pow(values[SensorManager.DATA_X] / SensorManager.GRAVITY_EARTH, 2.0);
        totalForce += Math.pow(values[SensorManager.DATA_Y] / SensorManager.GRAVITY_EARTH, 2.0);
        totalForce += Math.pow(values[SensorManager.DATA_Z] / SensorManager.GRAVITY_EARTH, 2.0);
        totalForce = Math.sqrt(totalForce);
        double abs = Math.abs(1.0 - totalForce);
        return abs > 0.1;
    }
}