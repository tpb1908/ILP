package com.tpb.coinz.data.location.steps

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class SensorManagerStepTracker(private val sensorManager: SensorManager) : StepTracker, SensorEventListener {

    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    private var steps: Int = 0

    override fun startStepTracking() {
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun stopStepTracking() {
        sensorManager.unregisterListener(this, stepSensor)
    }

    override fun getStepsToday(): Int {
        return steps
    }

    override fun isStepTrackingAvailable(): Boolean = stepSensor != null

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
            steps++
        }
    }
}