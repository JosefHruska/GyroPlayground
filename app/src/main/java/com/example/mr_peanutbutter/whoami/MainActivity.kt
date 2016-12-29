package com.example.mr_peanutbutter.whoami

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity(), SensorEventListener {

    private var mSensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private var currentOrientation: Orientation = Orientation.MIDDLE
    private var currentMove: Orientation = Orientation.NONE

    enum class Orientation {
        UP,
        DOWN,
        MIDDLE,
        NONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        accelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        initListeners()
    }


    fun initListeners() {
        mSensorManager!!.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST)
        mSensorManager!!.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST)
    }

    public override fun onDestroy() {
        mSensorManager!!.unregisterListener(this)
        super.onDestroy()
    }

    override fun onBackPressed() {
        mSensorManager!!.unregisterListener(this)
        super.onBackPressed()
    }

    public override fun onResume() {
        initListeners()
        super.onResume()
    }

    override fun onPause() {
        mSensorManager!!.unregisterListener(this)
        super.onPause()
    }

    internal var inclineGravity = FloatArray(3)
    internal var mGravity: FloatArray? = null
    internal var mGeomagnetic: FloatArray? = null
    internal var orientation = FloatArray(3)
    internal var pitch: Float = 0.toFloat()
    internal var roll: Float = 0.toFloat()

    override fun onSensorChanged(event: SensorEvent) {
        //If type is accelerometer only assign values to global property mGravity
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values

            if (isTiltDownward && currentMove != Orientation.DOWN) {
                currentMove = Orientation.DOWN
                onOrientationChanged(Orientation.DOWN)
                vCurrentMove.text = "MOVE : " + currentMove.toString()
            } else if (isTiltUpward && currentMove != Orientation.UP) {
                currentMove = Orientation.UP
                vCurrentMove.text = "MOVE : " + currentMove.toString()
                onOrientationChanged(Orientation.UP)
            } else if (isTiltNormal) {
                currentMove = Orientation.MIDDLE
                vCurrentMove.text = "MOVE : " + currentMove.toString()
                onOrientationChanged(Orientation.MIDDLE)
            }
        }

    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // TODO Auto-generated method stub

    }

    /*
                * If the roll is positive, you're in reverse landscape (landscape right), and if the roll is negative you're in landscape (landscape left)
                *
                * Similarly, you can use the pitch to differentiate between portrait and reverse portrait.
                * If the pitch is positive, you're in reverse portrait, and if the pitch is negative you're in portrait.
                *
                * orientation -> azimut, pitch and roll
                *
                *
                */// Normalize the accelerometer vector
    //Checks if device is flat on ground or not
    /*
                * Float obj1 = new Float("10.2");
                * Float obj2 = new Float("10.20");
                * int retval = obj1.compareTo(obj2);
                *
                * if(retval > 0) {
                * System.out.println("obj1 is greater than obj2");
                * }
                * else if(retval < 0) {
                * System.out.println("obj1 is less than obj2");
                * }
                * else {
                * System.out.println("obj1 is equal to obj2");
                * }
                */ val isTiltUpward: Boolean
        get() {
            if (mGravity != null && mGeomagnetic != null) {
                val R = FloatArray(9)
                val I = FloatArray(9)

                val success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)

                if (success) {
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(R, orientation)

                    pitch = orientation[1]
                    roll = orientation[2]

                    inclineGravity = mGravity!!.clone()

                    val norm_Of_g = Math.sqrt((inclineGravity[0] * inclineGravity[0] + inclineGravity[1] * inclineGravity[1] + inclineGravity[2] * inclineGravity[2]).toDouble())
                    inclineGravity[0] = (inclineGravity[0] / norm_Of_g).toFloat()
                    inclineGravity[1] = (inclineGravity[1] / norm_Of_g).toFloat()
                    inclineGravity[2] = (inclineGravity[2] / norm_Of_g).toFloat()
                    val inclination = Math.round(Math.toDegrees(Math.acos(inclineGravity[2].toDouble()))).toInt()
                    val objPitch = pitch
                    val objZero = 0.0
                    val objZeroPointTwo = 0.2
                    val objZeroPointTwoNegative = -0.2

                    val objPitchZeroResult = objPitch.compareTo(objZero)
                    val objPitchZeroPointTwoResult = objZeroPointTwo.compareTo(objPitch)
                    val objPitchZeroPointTwoNegativeResult = objPitch.compareTo(objZeroPointTwoNegative)

                    if ( inclination > 30 && inclination < 40) {
                        return true
                    } else {
                        return false
                    }
                }
            }

            return false
        }

    // Normalize the accelerometer vector
    //Checks if device is flat on groud or not
    val isTiltDownward: Boolean
        get() {
            if (mGravity != null && mGeomagnetic != null) {
                val R = FloatArray(9)
                val I = FloatArray(9)

                val success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)

                if (success) {
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(R, orientation)

                    pitch = orientation[1]
                    roll = orientation[2]

                    inclineGravity = mGravity!!.clone()

                    val norm_Of_g = Math.sqrt((inclineGravity[0] * inclineGravity[0] + inclineGravity[1] * inclineGravity[1] + inclineGravity[2] * inclineGravity[2]).toDouble())
                    inclineGravity[0] = (inclineGravity[0] / norm_Of_g).toFloat()
                    inclineGravity[1] = (inclineGravity[1] / norm_Of_g).toFloat()
                    inclineGravity[2] = (inclineGravity[2] / norm_Of_g).toFloat()
                    val inclination = Math.round(Math.toDegrees(Math.acos(inclineGravity[2].toDouble()))).toInt()

                    val objPitch = pitch
                    val objZero = 0.0
                    val objZeroPointTwo = 0.2
                    val objZeroPointTwoNegative = -0.2

                    val objPitchZeroResult = objPitch.compareTo(objZero)
                    val objPitchZeroPointTwoResult = objZeroPointTwo.compareTo(objPitch)
                    val objPitchZeroPointTwoNegativeResult = objPitch.compareTo(objZeroPointTwoNegative)

                    if (roll < 0 && (objPitchZeroResult > 0 && objPitchZeroPointTwoResult > 0 || objPitchZeroResult < 0 && objPitchZeroPointTwoNegativeResult > 0) && inclination > 140 && inclination < 170) {
                        return true
                    } else {
                        return false
                    }
                }
            }

            return false
        }
    val isTiltNormal: Boolean
        get() {
            if (mGravity != null && mGeomagnetic != null) {
                val R = FloatArray(9)
                val I = FloatArray(9)

                val success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)

                if (success) {
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(R, orientation)

                    pitch = orientation[1]
                    roll = orientation[2]

                    inclineGravity = mGravity!!.clone()

                    val norm_Of_g = Math.sqrt((inclineGravity[0] * inclineGravity[0] + inclineGravity[1] * inclineGravity[1] + inclineGravity[2] * inclineGravity[2]).toDouble())
                    inclineGravity[0] = (inclineGravity[0] / norm_Of_g).toFloat()
                    inclineGravity[1] = (inclineGravity[1] / norm_Of_g).toFloat()
                    inclineGravity[2] = (inclineGravity[2] / norm_Of_g).toFloat()
                    val inclination = Math.round(Math.toDegrees(Math.acos(inclineGravity[2].toDouble()))).toInt()

                    val objPitch = pitch
                    val objZero = 0.0
                    val objZeroPointTwo = 0.2
                    val objZeroPointTwoNegative = -0.2

                    val objPitchZeroResult = objPitch.compareTo(objZero)
                    val objPitchZeroPointTwoResult = objZeroPointTwo.compareTo(objPitch)
                    val objPitchZeroPointTwoNegativeResult = objPitch.compareTo(objZeroPointTwoNegative)

                    Log.d("NORMAL TILT?", " inclination : $inclination")

                    if (inclination > 80 && inclination < 100) {
                        return true
                    } else {
                        return false
                    }
                }
            }

            return false
        }

    private fun onOrientationChanged(move: Orientation) {
        if (move == Orientation.DOWN) {
            currentOrientation = Orientation.DOWN
            onFailed()
        } else if (move == Orientation.UP) {
            currentOrientation = Orientation.UP
            onPass()
        } else if (move == Orientation.MIDDLE) {
            currentOrientation = Orientation.MIDDLE
            onNormal()
        }

        vOutput.text = "OUTPUT : " + currentOrientation.toString()
        vCurrentDirection.text = "DIRECTION : " + currentOrientation.toString()
    }

    private fun onNormal(){
//        TODO: SHow next thing
    }

    private fun onPass() {
//        TODO: Point +1
//        TODO: Add to right answers
    }

    private fun onFailed() {
//        TODO: Point -1
//        TODO: Add to failed answers
    }

    private
}
