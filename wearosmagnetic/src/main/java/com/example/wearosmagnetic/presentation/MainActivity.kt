package com.example.wearosmagnetic.presentation

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var magX by mutableStateOf("Fetching...")
    private var magY by mutableStateOf("Fetching...")
    private var magZ by mutableStateOf("Fetching...")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SensorManager 초기화
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        magneticFieldSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }

        setContent {
            MagneticFieldWearOSApp(magX, magY, magZ)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            magX = "%.2f μT".format(it.values[0])
            magY = "%.2f μT".format(it.values[1])
            magZ = "%.2f μT".format(it.values[2])
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

@Composable
fun MagneticFieldWearOSApp(magX: String, magY: String, magZ: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Magnetic Field Data", style = MaterialTheme.typography.body1, modifier = Modifier.padding(16.dp))
        Text("X: $magX", modifier = Modifier.padding(8.dp))
        Text("Y: $magY", modifier = Modifier.padding(8.dp))
        Text("Z: $magZ", modifier = Modifier.padding(8.dp))
    }
}