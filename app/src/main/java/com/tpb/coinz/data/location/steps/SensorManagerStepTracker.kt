package com.tpb.coinz.data.location.steps

import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import timber.log.Timber
import java.util.*

class SensorManagerStepTracker(private val sensorManager: SensorManager, private val prefs: SharedPreferences) : StepTracker, SensorEventListener {

    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    private val stepsKey = "steps"
    private var steps: Int = 0
    set(value) {
        field = value
        prefs.edit().putInt(stepsKey, value).apply()
    }

    private val dayKey = "steps_day"
    private var stepsDay: Calendar = Calendar.getInstance()
        set(value) {
            field = value
            prefs.edit().putLong(dayKey, value.timeInMillis).apply()
        }
    init {
        if (prefs.contains(dayKey)) {
            val day = prefs.getLong(dayKey, -1)
            val cal = Calendar.getInstance()
            cal.timeInMillis = day
            stepsDay = cal
            steps = prefs.getInt(stepsKey, 0)
            checkCurrentDay()
            Timber.i("Loaded step count")
        } else { // First run
            Timber.i("First run of FireStoreCoinBank")
            steps = 0
        }
    }

    private fun checkCurrentDay() {
        val now = Calendar.getInstance()
        if (now.get(Calendar.DAY_OF_YEAR) != stepsDay.get(Calendar.DAY_OF_YEAR)) {
            Timber.i("Current day has changed. Resetting step count")
            stepsDay = now
            steps = 0
        }
    }

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
            checkCurrentDay()
            steps++
        }
    }


}