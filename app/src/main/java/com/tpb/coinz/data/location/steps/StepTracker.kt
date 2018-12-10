package com.tpb.coinz.data.location.steps

interface StepTracker {

    fun startStepTracking()

    fun stopStepTracking()

    fun getStepsToday(): Int

    fun isStepTrackingAvailable(): Boolean

}