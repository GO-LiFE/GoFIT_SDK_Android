package com.goyourlife.remotecamera;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.golife.contract.AppContract;
import com.goyourlife.gofit_demo.R;

@SuppressWarnings("ResourceType")
public class RemoteCamera extends Activity {

    public final static int NOTIFICATION_ID_NOTIFICATION_ACCESS = 0x400;
    public final static int NOTIFICATION_ID_REMOTE_SHUTTER = NOTIFICATION_ID_NOTIFICATION_ACCESS + 1;

    private int NUMBER_OF_CAMERA = 0;
    private int mCurrentCameraIndex = 1;

    private CameraPreview mPreview;

    private float mScaleLevel = 1f;
    private float mMaxLevel = 0f;
    private ScaleGestureDetector mScaleGestureDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_remote_camera);

        try {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIFICATION_ID_REMOTE_SHUTTER);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        if (!checkCameraHardware(RemoteCamera.this) || getNumberOfCameras() < 1) {
            showCameraErrorDialog("Sorry, your device didn`t support camera.");
        } else {
            mCurrentCameraIndex = 1;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NUMBER_OF_CAMERA > 1) {
            findViewById(R.id.img_remotecamera_switchcamera).setVisibility(View.VISIBLE);
        }

        try {
            CameraClass.cameraInit(mCurrentCameraIndex - 1);
            mMaxLevel = CameraClass.cameraGetMaxZoom();
            mScaleLevel = 1f;

            mPreview = new CameraPreview(this);
            mPreview.setCamera(CameraClass.cameraGetCurrent());
            FrameLayout preview = (FrameLayout) findViewById(R.id.fl_remotecamera_camerapreview);
            preview.addView(mPreview);

            mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        } catch (Exception e) {
            if (CameraClass.checkPermission(AppContract.PermissionType.camera, this)) {
                showCameraErrorDialog("Cannot get camera, is there other app occupy?" + "\n" + e.toString());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraClass.cameraUnInit();
    }

    private void showCameraErrorDialog(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RemoteCamera.this);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(true);
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                RemoteCamera.this.finish();
            }
        });
        alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                RemoteCamera.this.finish();
            }
        });
        if (!RemoteCamera.this.isFinishing())
            alertDialog.show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                CameraClass.cameraSetZoom(false);
                return true;

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                CameraClass.cameraSetZoom(true);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mScaleGestureDetector.onTouchEvent(ev);
        return true;
    }


    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private int getNumberOfCameras() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                return NUMBER_OF_CAMERA = (((CameraManager) getSystemService(Context.CAMERA_SERVICE)).getCameraIdList()).length;
            } catch (CameraAccessException e) {
                Log.e("CameraAccessException", e.toString());
                return 0;
            }
        } else {
            //noinspection deprecation
            return NUMBER_OF_CAMERA = Camera.getNumberOfCameras();
        }
    }

    public void onSwitchCameraClicked(View v) {
        mCurrentCameraIndex = (NUMBER_OF_CAMERA > mCurrentCameraIndex) ? mCurrentCameraIndex + 1 : 1;
        try {
            mPreview.stopPreview();
            CameraClass.cameraSwitch(mCurrentCameraIndex - 1);
            mMaxLevel = CameraClass.cameraGetMaxZoom();
            mScaleLevel = 1f;

            mPreview.setCamera(CameraClass.cameraGetCurrent());
            mPreview.setPreviewDisplay();
        } catch (Exception e) {
            showCameraErrorDialog("Cannot get camera, is there other app occupy?" + "\n" + e.toString());
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleLevel = detector.getScaleFactor() > 1
                    ? mScaleLevel + detector.getScaleFactor()
                    : mScaleLevel - detector.getScaleFactor();
            mScaleLevel = Math.max(1f, Math.min(mScaleLevel, mMaxLevel + 1));
            CameraClass.cameraSetZoom((int) mScaleLevel - 1);
            return true;
        }
    }
}
