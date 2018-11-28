/*
 * Copyright (C) GOYOURLIFE INC. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Jethro E. Lee (Exce) <jethro.lee@goyourlife.com>, July 2018.
 * 
 * Project : GoFIT SDK (code name : GoFIT SDK)
 * 
 * @author Jethro E. Lee (Exce) <jethro.lee@goyourlife.com>
 * @link http://www.goyourlife.com
 * @copyright Copyright &copy; 2018 GOYOURLIFE INC.
 */
package com.goyourlife.remotecamera;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private Activity mActivity;
    private SurfaceHolder mHolder;
    @SuppressWarnings("deprecation")
    private Camera mCamera;

    public CameraPreview(Activity activity) {
        super(activity);
        mActivity = activity;
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @SuppressWarnings("deprecation")
    public void setCamera(Camera camera) {
        mCamera = camera;
    }

    public void setPreviewDisplay() throws IOException {
        setCameraDisplayOrientation();
        mCamera.setPreviewDisplay(mHolder);
        startPreview();
    }

    public void startPreview() {
        mCamera.startPreview();
    }

    @SuppressWarnings("deprecation")
    private void setCameraDisplayOrientation() {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
//        android.hardware.Camera.getCameraInfo(cameraId, info);
        android.hardware.Camera.getCameraInfo(0, info);
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);
    }

    public void stopPreview() {
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            setPreviewDisplay();
        } catch (IOException e) {
            Log.d("CameraPreview", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null) {
            return;
        }

        stopPreview();

        try {
            setPreviewDisplay();
        } catch (Exception e) {
            Log.d("CameraPreview", "Error starting camera preview: " + e.getMessage());
        }
    }
}