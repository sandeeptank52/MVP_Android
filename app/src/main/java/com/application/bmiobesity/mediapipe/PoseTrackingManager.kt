package com.application.bmiobesity.mediapipe

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.application.bmiobesity.PoseTrackingIndex
import com.application.bmiobesity.utils.calculateAngle
import com.application.bmiobesity.utils.calculateDistance
import com.google.mediapipe.formats.proto.LandmarkProto
import java.util.concurrent.TimeUnit

class PoseTrackingManager private constructor() {

    private val mResultText: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val resultText: LiveData<String> = mResultText

    private val mLandMarkList: MutableLiveData<LandmarkProto.NormalizedLandmarkList> by lazy { MutableLiveData<LandmarkProto.NormalizedLandmarkList>() }
    val landMarkList: LiveData<LandmarkProto.NormalizedLandmarkList> = mLandMarkList
    var countDownTime: Long = 0
    fun setLandMarkList(item: LandmarkProto.NormalizedLandmarkList) {
        this.mLandMarkList.postValue(item)

        validateBondarevskyTest(item.landmarkList)
    }

    private var isRunning = false
    private var incorrectPoseNum = 0
    private var isFinished = false
    private val timer = object : CountDownTimer(20000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            countDownTime = millisUntilFinished
            isRunning = true
        }

        override fun onFinish() {
            isRunning = false
            isFinished = true
            mResultText.postValue("Congratulation!")
        }
    }


    private fun validateBondarevskyTest(list: List<LandmarkProto.NormalizedLandmark>) {
        var poseLandmarkStr = ""
        val connectionLength = getConnectionLength(list)
        //Distance between left knee and right foot
        val normDistLeftKneeRightFoot = calculateDistance(
            list[PoseTrackingIndex.LEFT_KNEE.index].x,
            list[PoseTrackingIndex.LEFT_KNEE.index].y,
            list[PoseTrackingIndex.RIGHT_FOOT.index].x,
            list[PoseTrackingIndex.RIGHT_FOOT.index].y
        ) / connectionLength
        //Distance between right knee and left foot
        val normDistRightKneeLeftFoot = calculateDistance(
            list[PoseTrackingIndex.RIGHT_KNEE.index].x,
            list[PoseTrackingIndex.RIGHT_KNEE.index].y,
            list[PoseTrackingIndex.LEFT_FOOT.index].x,
            list[PoseTrackingIndex.LEFT_FOOT.index].y
        ) / connectionLength
        // Distance between left hip and left hand
        val normDistLeftHipLeftHand = calculateDistance(
            list[PoseTrackingIndex.LEFT_HIP.index].x,
            list[PoseTrackingIndex.LEFT_HIP.index].y,
            list[PoseTrackingIndex.LEFT_PINKY.index].x,
            list[PoseTrackingIndex.LEFT_PINKY.index].y
        ) / connectionLength
        // Distance between right hip and right hand
        val normDistRightHipRightHand = calculateDistance(
            list[PoseTrackingIndex.RIGHT_HIP.index].x,
            list[PoseTrackingIndex.RIGHT_HIP.index].y,
            list[PoseTrackingIndex.RIGHT_PINKY.index].x,
            list[PoseTrackingIndex.RIGHT_PINKY.index].y
        ) / connectionLength
        // Angle of left leg (to determine leg is straight or not)
        val angleLeftThighLeftCalf = calculateAngle(
            list[PoseTrackingIndex.LEFT_ANKLE.index].x,
            list[PoseTrackingIndex.LEFT_ANKLE.index].y,
            list[PoseTrackingIndex.LEFT_KNEE.index].x,
            list[PoseTrackingIndex.LEFT_KNEE.index].y,
            list[PoseTrackingIndex.LEFT_HIP.index].x,
            list[PoseTrackingIndex.LEFT_HIP.index].y
        )

        // Angle of right leg (to determine leg is straight or not)
        val angleRightThighRightCalf = calculateAngle(
            list[PoseTrackingIndex.RIGHT_ANKLE.index].x,
            list[PoseTrackingIndex.RIGHT_ANKLE.index].y,
            list[PoseTrackingIndex.RIGHT_KNEE.index].x,
            list[PoseTrackingIndex.RIGHT_KNEE.index].y,
            list[PoseTrackingIndex.RIGHT_HIP.index].x,
            list[PoseTrackingIndex.RIGHT_HIP.index].y
        )
        // Angle of left upper body and left lower body (to determine the body is straight or not)
        val angleLeftUpperLeftLower = calculateAngle(
            list[PoseTrackingIndex.LEFT_ANKLE.index].x,
            list[PoseTrackingIndex.LEFT_ANKLE.index].y,
            list[PoseTrackingIndex.LEFT_HIP.index].x,
            list[PoseTrackingIndex.LEFT_HIP.index].y,
            list[PoseTrackingIndex.LEFT_SHOULDER.index].x,
            list[PoseTrackingIndex.LEFT_SHOULDER.index].y
        )
        // Angle of right upper body and right lower body (to determine the body is straight or not)
        val angleRightUpperRightLower = calculateAngle(
            list[PoseTrackingIndex.RIGHT_ANKLE.index].x,
            list[PoseTrackingIndex.RIGHT_ANKLE.index].y,
            list[PoseTrackingIndex.RIGHT_HIP.index].x,
            list[PoseTrackingIndex.RIGHT_HIP.index].y,
            list[PoseTrackingIndex.RIGHT_SHOULDER.index].x,
            list[PoseTrackingIndex.RIGHT_SHOULDER.index].y
        )

        if (normDistLeftKneeRightFoot < 0.03) {
            if (angleLeftThighLeftCalf < 160)
                poseLandmarkStr = "Left leg is not straight"
            else if (angleLeftUpperLeftLower < 160)
                poseLandmarkStr = "Body is not straight"
        } else if (normDistRightKneeLeftFoot < 0.03) {
            if (angleRightThighRightCalf < 160)
                poseLandmarkStr = "Right leg is not straight"
            else if (angleRightUpperRightLower < 160)
                poseLandmarkStr = "Body is not straight"
        } else {
            poseLandmarkStr = "Not bending leg at knee"
        }
        if (normDistLeftHipLeftHand > 0.05) {
            poseLandmarkStr = "Left hand is not on belt"
        }
        if (normDistRightHipRightHand > 0.05) {
            poseLandmarkStr = "Right hand is not on belt"
        }
        if (poseLandmarkStr.isBlank()) {
            incorrectPoseNum = 0
            if (!isFinished) {
                poseLandmarkStr =
                    "Stay still in ${TimeUnit.MILLISECONDS.toSeconds(countDownTime)}"
                if (!isRunning) {
                    timer.start()
                }
            }
        } else {
            incorrectPoseNum.plus(1)
            if (incorrectPoseNum > 3) {
                timer.cancel()
                isRunning = false
            }

        }
        this.mResultText.postValue(poseLandmarkStr)
    }


    private fun getConnectionLength(landmarkList: List<LandmarkProto.NormalizedLandmark>): Double {
        val collectionArray = arrayOf(
            Pair(PoseTrackingIndex.LEFT_SHOULDER, PoseTrackingIndex.LEFT_ELBOW),
            Pair(PoseTrackingIndex.LEFT_ELBOW, PoseTrackingIndex.LEFT_PINKY),
            Pair(PoseTrackingIndex.LEFT_HIP, PoseTrackingIndex.LEFT_KNEE),
            Pair(PoseTrackingIndex.LEFT_KNEE, PoseTrackingIndex.LEFT_FOOT),
            Pair(PoseTrackingIndex.LEFT_HIP, PoseTrackingIndex.LEFT_SHOULDER),
            Pair(PoseTrackingIndex.RIGHT_SHOULDER, PoseTrackingIndex.RIGHT_ELBOW),
            Pair(PoseTrackingIndex.RIGHT_ELBOW, PoseTrackingIndex.RIGHT_PINKY),
            Pair(PoseTrackingIndex.RIGHT_HIP, PoseTrackingIndex.RIGHT_KNEE),
            Pair(PoseTrackingIndex.RIGHT_KNEE, PoseTrackingIndex.RIGHT_FOOT),
            Pair(PoseTrackingIndex.RIGHT_HIP, PoseTrackingIndex.RIGHT_SHOULDER),
            Pair(PoseTrackingIndex.RIGHT_SHOULDER, PoseTrackingIndex.LEFT_SHOULDER),
            Pair(PoseTrackingIndex.RIGHT_HIP, PoseTrackingIndex.LEFT_HIP)
        )
        return collectionArray.map {
            calculateDistance(
                landmarkList[it.first.index].x,
                landmarkList[it.first.index].y,
                landmarkList[it.second.index].x,
                landmarkList[it.second.index].y
            )
        }.sum().toDouble()
    }

    companion object {
        @Volatile
        private var INSTANCE: PoseTrackingManager? = null

        fun getPoseTrackingManager(): PoseTrackingManager {
            return INSTANCE ?: synchronized(this) {
                val instance = PoseTrackingManager()
                INSTANCE = instance
                instance
            }
        }
    }


}