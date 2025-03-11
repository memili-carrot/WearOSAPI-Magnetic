package com.example.wearosmagnetic.complication

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService

/**
 * Complication service that displays real-time magnetic field data.
 */
class MainComplicationService : SuspendingComplicationDataSourceService(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var magX: Float = 0.0f
    private var magY: Float = 0.0f
    private var magZ: Float = 0.0f

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        registerMagneticFieldSensor()
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        if (type != ComplicationType.SHORT_TEXT) {
            return null
        }
        return createComplicationData("0.0 μT", "Magnetic Field Data")
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData {
        val magneticFieldData = getMagneticFieldData()
        return createComplicationData(magneticFieldData, "Current Magnetic Field")
    }

    private fun createComplicationData(text: String, contentDescription: String) =
        ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text).build(),
            contentDescription = PlainComplicationText.Builder(contentDescription).build()
        ).build()

    private fun registerMagneticFieldSensor() {
        val magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        magneticFieldSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun getMagneticFieldData(): String {
        return "%.2f μT".format(magX) // X축 자기장 값만 표시
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            magX = it.values[0] // X축 자기장 데이터
            magY = it.values[1] // Y축 자기장 데이터
            magZ = it.values[2] // Z축 자기장 데이터
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 정확도 변경 이벤트 필요 없음
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}