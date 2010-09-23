
package com.google.code.geobeagle.shakewaker;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)

public class ShakeWakerTest {
    private SharedPreferences sharedPreferences;
    private SensorManager sensorManager;
    private ShakeListener shakeListener;

    @Before
    public void setUp() {
        sensorManager = PowerMock.createMock(SensorManager.class);
        shakeListener = PowerMock.createMock(ShakeListener.class);
        sharedPreferences = PowerMock.createMock(SharedPreferences.class);
    }

    @Test
    public void testShakeWakerRegister() {
        Sensor accelerometer = PowerMock.createMock(Sensor.class);
        List<Sensor> sensorList = new ArrayList<Sensor>();

        sensorList.add(accelerometer);
        EasyMock.expect(sharedPreferences.getBoolean(ShakeWaker.SHAKE_WAKE, false)).andReturn(true);
        EasyMock.expect(sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)).andReturn(
                sensorList);
        EasyMock.expect(
                sensorManager.registerListener(shakeListener, accelerometer,
                        SensorManager.SENSOR_DELAY_UI)).andReturn(true);
        PowerMock.replayAll();

        new ShakeWaker(sharedPreferences, sensorManager, shakeListener).register();
        PowerMock.verifyAll();
    }

    @Test
    public void testShakeWakerRegisterNoPreferenceSet() {
        Sensor accelerometer = PowerMock.createMock(Sensor.class);
        List<Sensor> sensorList = new ArrayList<Sensor>();

        sensorList.add(accelerometer);
        EasyMock.expect(sharedPreferences.getBoolean(ShakeWaker.SHAKE_WAKE, false))
                .andReturn(false);
        PowerMock.replayAll();

        new ShakeWaker(sharedPreferences, sensorManager, shakeListener).register();
        PowerMock.verifyAll();
    }

    @Test
    public void testShakeWakerUnregister() {
        sensorManager.unregisterListener(shakeListener);
        PowerMock.replayAll();

        new ShakeWaker(null, sensorManager, shakeListener).unregister();
        PowerMock.verifyAll();
    }

    @Test
    public void testShakeWakerRegisterNoAccelerometer() {
        List<Sensor> sensorList = new ArrayList<Sensor>();

        EasyMock.expect(sharedPreferences.getBoolean(ShakeWaker.SHAKE_WAKE, false)).andReturn(true);
        EasyMock.expect(sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)).andReturn(
                sensorList);
        PowerMock.replayAll();

        new ShakeWaker(sharedPreferences, sensorManager, shakeListener).register();
        PowerMock.verifyAll();
    }
}
