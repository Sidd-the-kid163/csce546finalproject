package com.example.acccomparison.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
//import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acccomparison.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.*
import java.util.LinkedList
import java.util.Queue
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.acccomparison.ui.Acceleration
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Calendar
import java.util.jar.Manifest


class GameViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {
    private val app = application
    //private val _uiState = MutableStateFlow(GameUiState())
    //val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    private var mSensorManager : SensorManager
    private var pauseRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())
    val queue1: Queue<Acceleration> = LinkedList()
    var queue2: Queue<Acceleration>? = LinkedList()
    var currenttime: Long = 0L
    var value = 0
    private var mAccelerometer : Sensor ?= null
    private var gson: Gson? = GsonBuilder().serializeNulls().setPrettyPrinting().create()
    private val _requestFileCreation = MutableLiveData<Boolean>()
    val requestFileCreation: LiveData<Boolean> get() = _requestFileCreation
    private val _acc = MutableLiveData<GameUiState>()
    val acc: LiveData<GameUiState> get() = _acc
    init {
        mSensorManager = app.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        Log.e("confirm", Thread.currentThread().name)
    }
    fun createJsonFile() {
        if (_requestFileCreation.value != true) {
            _requestFileCreation.value = true
        }
        Log.e("jsonfileboolean", "works")
    }
    fun createacc(updatedScore: Boolean, excellents: Int, goods: Int, oks: Int, totals: Int, color: Color) {
        //if (_acc.value != GameUiState(updatedScore, excellents, goods, oks, totals, color)) {
            _acc.postValue(GameUiState(updatedScore, excellents, goods, oks, totals, color))
        //}
        Log.e("accchanging", "works")
    }
    fun resetFileCreationTrigger() {
        if (_requestFileCreation.value != false) {
            _requestFileCreation.value = false
        }
    }
    fun writeToFile(uri: Uri) {
        Log.e("Write to File Function", "executes")
        val context = getApplication<Application>()
        if (uri != null)
        {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(gson?.toJson(queue1)?.toByteArray())
            }
        } catch (e: Exception) {
            Log.e("FileViewModel", "Failed to write JSON", e)
        }}
        else {
            Log.e("uri", "not detected")
        }
        queue1.clear()
    }

    fun readToFile(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.IO) {
            val stringBuilder = StringBuilder()
            try {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    var line = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line).append('\n')
                        line = reader.readLine()
                    }
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Failed to read file: ${e.message}")
            }
            val jsonArray = JSONArray(stringBuilder.toString())
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val m = obj.getDouble("m").toFloat()
                val time = obj.getLong("time")
                val x = obj.getDouble("x").toFloat()
                val y = obj.getDouble("y").toFloat()
                val z = obj.getDouble("z").toFloat()
                queue2?.offer(Acceleration(time, x, y, z, m))
            }
            //Log.e("confirm", Thread.currentThread().name)
            Log.e("value", queue2?.count().toString())
        }
    }
    //gson?.toJson(queue1)
    /*
    fun updateGameState(updatedScore: Boolean, excellents: Int, goods: Int, oks: Int, totals: Int, color: Color) {
        _uiState.update { currentState ->
            currentState.copy(
                accStatus = updatedScore,
                excellents = excellents,
                goods = goods,
                oks = oks,
                totals = totals,
                color = color
            )
        }
    }
     */
    fun checkresult(event1: Float, event2: Float, event3: Float, time: Long) {
        viewModelScope.launch(Dispatchers.IO) {
        //Log.e("checkresult:", "running")
            /*
            Log.e("values:" , roundToNearestHundred(time).toString() + " == " + (roundToNearestHundred(queue2?.peek()?.time ?: 0L).toString()))
        if (roundToNearestHundred(time) == roundToNearestHundred(
                queue2?.peek()?.time ?: 0L
            )
        )
             */
        if (kotlin.math.abs(time - (queue2?.peek()?.time ?: 0L)) <= 100)
        {
            //value += 1
            //Log.e("time:", value.toString())
            var x = (queue2?.peek()?.m ?: 0f) - (magnitude(event1, event2, event3))
            Log.e("value:", x.toString())
            if (kotlin.math.abs((queue2?.peek()?.m ?: 0f) - (magnitude(
                    event1,
                    event2,
                    event3
                ))) < 5
            ) {
                Log.e("value:", "e")
                createacc(
                    true,
                    _acc.value!!.excellents + 1,
                    _acc.value!!.goods,
                    _acc.value!!.oks,
                    _acc.value!!.totals + 1,
                    Color(0xFF00FF00)
                )
                //color light
                //points tally
                queue2?.poll()
            } else if (kotlin.math.abs((queue2?.peek()?.m ?: 0f) - (magnitude(
                    event1,
                    event2,
                    event3
                ))) < 10
            ) {
                Log.e("value:", "g")
                createacc(
                    true,
                    _acc.value!!.excellents,
                    _acc.value!!.goods + 1,
                    _acc.value!!.oks,
                    _acc.value!!.totals + 1,
                    Color(0xFF339933)
                )
                queue2?.poll()
            } else if (kotlin.math.abs((queue2?.peek()?.m ?: 0f) - (magnitude(
                    event1,
                    event2,
                    event3
                ))) < 20
            ) {
                Log.e("value:", "o")
                createacc(
                    true,
                    _acc.value!!.excellents,
                    _acc.value!!.goods,
                    _acc.value!!.oks + 1,
                    _acc.value!!.totals + 1,
                    Color(0xFFFFFF00)
                )
                queue2?.poll()
            } else if (kotlin.math.abs((queue2?.peek()?.m ?: 0f) - (magnitude(
                    event1,
                    event2,
                    event3
                ))) > 20
            ) {
                Log.e("value:", "t")
                createacc(
                    true,
                    _acc.value!!.excellents,
                    _acc.value!!.goods,
                    _acc.value!!.oks,
                    _acc.value!!.totals + 1,
                    Color(0xFFFF0000)
                )
                queue2?.poll()
            }
        }
        }
    }
    fun magnitude(x: Float, y: Float, z: Float): Float {
        return sqrt(x*x+y*y+z*z)
    }
    fun roundToNearestHundred(number: Long): Long {
        return (number + 50) / 100 * 100
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }
    override fun onSensorChanged(event: SensorEvent?) {
        //Log.e("confirm", queue2?.isNotEmpty().toString())
        if (event != null && _acc.value!!.accStatus && queue2?.isEmpty() == true) {
            if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
                queue1.offer(
                    Acceleration(
                        System.currentTimeMillis() - currenttime,
                        event.values[0],
                        event.values[1],
                        event.values[2],
                        magnitude(event.values[0], event.values[1], event.values[2])
                    )
                )
                //Log.e("acc", event.values[0].toString() + " " + event.values[1].toString() + " " + event.values[2].toString())
            }
        } else if (event != null && _acc.value!!.accStatus && queue2?.isNotEmpty() == true) {
            //Log.e("is", "triggering")
            if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
                //queue1.offer(Acceleration(System.currentTimeMillis() - currenttime, event.values[0], event.values[1], event.values[2],magnitude(event.values[0], event.values[1], event.values[2])))
                    checkresult(event.values[0], event.values[1], event.values[2], System.currentTimeMillis() - currenttime)
            }
        }
    }
    fun onResume() {
        Log.e("value", queue2.toString())
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        //updateGameState(true, 0, 0, 0, 0, Color(0xFF6200EE))
        createacc(
            true,
            0,
            0,
            0,
            0,
            Color(0xFF6200EE),
        )
        currenttime = System.currentTimeMillis()
        pauseRunnable = Runnable {
            onPause()
        }
        handler.postDelayed(pauseRunnable!!, 50000)
    }
    fun onPause() {
        mSensorManager.unregisterListener(this)
        //writeTextToFile(gson?.toJson(queue1))
        Log.e("value", queue2?.count().toString())
        createacc(
            false,
            _acc.value!!.excellents,
            _acc.value!!.goods,
            _acc.value!!.oks,
            _acc.value!!.totals,
            Color(0xFF6200EE),
        )
        Log.e("count:", queue2?.count().toString())
        if(_acc.value!!.totals == 0) {
            createJsonFile()
        }
    }
}