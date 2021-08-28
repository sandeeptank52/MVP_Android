package com.application.bmiobesity.viewModels

import androidx.lifecycle.ViewModel
import com.application.bmiobesity.common.ProfileManager
import com.application.bmiobesity.mediapipe.PoseTrackingManager
import com.google.mediapipe.formats.proto.LandmarkProto

class CameraTrackViewModel : ViewModel(){
    val poseTrackingManager = PoseTrackingManager.getPoseTrackingManager()

    fun updateCameraLandMark(landmark: LandmarkProto.NormalizedLandmarkList){
        poseTrackingManager.setLandMarkList(landmark)
    }
}