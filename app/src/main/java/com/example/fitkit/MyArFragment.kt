package com.example.fitkit

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.ar.core.Config
import com.google.ar.core.exceptions.*
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.activity_measure.*

class MyArFragment : ArFragment() {
    override fun onArUnavailableException(sessionException: UnavailableException) {
        val message: String
        when (sessionException) {
            is UnavailableArcoreNotInstalledException -> message = "Please install ARCore"
            is UnavailableApkTooOldException -> message = "Please upgrade ARCore"
            is UnavailableSdkTooOldException -> message = "Please upgrade the app"
            is UnavailableDeviceNotCompatibleException -> message = "The current device department does not support AR"
            else -> {
                message = "Failed to create AR session, please check model adaptation, arcore version and system version"
            }
        }
        Toast.makeText(context,message, Toast.LENGTH_LONG)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (UI_ArSceneView as MyArFragment).apply {
            setOnSessionConfigurationListener{session,config ->
                if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                    config.depthMode = Config.DepthMode.AUTOMATIC
                }
                config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            }
        }
    }
}