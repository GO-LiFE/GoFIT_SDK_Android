package com.goyourlife.remotecamera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.golife.contract.AppContract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CameraClass {
    private static final int REQUEST_PERMISSION_CAMERA = 1;
    private static final int REQUEST_PERMISSION_CONTACTS = 2;
    private static final int REQUEST_PERMISSION_LOCATION = 3;
    private static final int REQUEST_PERMISSION_PHONE = 4;
    private static final int REQUEST_PERMISSION_SMS = 5;
    private static final int REQUEST_PERMISSION_STORAGE = 6;

    public static int[] PERMISSION_REQUEST_CODE = {
            REQUEST_PERMISSION_CAMERA,
            REQUEST_PERMISSION_CONTACTS,
            REQUEST_PERMISSION_LOCATION,
            REQUEST_PERMISSION_PHONE,
            REQUEST_PERMISSION_SMS,
            REQUEST_PERMISSION_STORAGE
    };

    public static String[] PERMISSION = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static String[] PERMISSION_GROUP = {
            Manifest.permission_group.CAMERA,
            Manifest.permission_group.CONTACTS,
            Manifest.permission_group.LOCATION,
            Manifest.permission_group.PHONE,
            Manifest.permission_group.SMS,
            Manifest.permission_group.STORAGE
    };

    @SuppressWarnings("deprecation")
    public static Camera cameraGetCurrent() {
        return AppContract.mCamera;
    }

    @SuppressWarnings("deprecation")
    public static void cameraInit(int which) throws Exception {
        AppContract.mCamera = Camera.open(which);

        cameraSetAutoFocus(AppContract.mCamera.getParameters());
        cameraSetPictureSize(AppContract.mCamera.getParameters());
    }

    public static void cameraUnInit() {
        if (AppContract.mCamera != null) {
            AppContract.mCamera.release();        // release the camera for other applications
            AppContract.mCamera = null;
        }
    }

    public static void cameraSwitch(int which) throws Exception {
        AppContract.mCamera.release();
        cameraInit(which);
    }

    @SuppressWarnings("deprecation")
    private static void cameraSetAutoFocus(Camera.Parameters params) {
        List<String> focusModes = params.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            AppContract.mCamera.setParameters(params);
        }
    }

    @SuppressWarnings("deprecation")
    public static void cameraSetPictureSize(Camera.Parameters params) {
        params.setPictureSize(params.getSupportedPictureSizes().get(0).width, params.getSupportedPictureSizes().get(0).height);
        AppContract.mCamera.setParameters(params);
    }

    @SuppressWarnings("deprecation")
    public static void cameraTakePicture(Camera.ShutterCallback shutterCallback, Camera.PictureCallback pictureCallback) {
        AppContract.mCamera.takePicture(shutterCallback, null, pictureCallback);
    }

    @SuppressWarnings("deprecation")
    public static void cameraSetZoom(boolean inZoomIn) {
        Camera.Parameters params = AppContract.mCamera.getParameters();
        int Current = params.getZoom();
        int Max = params.getMaxZoom();

        if (inZoomIn) {
            params.setZoom(Current < 1 ? 0 : Current - 1);
        } else {
            params.setZoom(Current < Max ? Current + 1 : Max);
        }

        AppContract.mCamera.setParameters(params);
    }

    @SuppressWarnings("deprecation")
    public static void cameraSetZoom(int zoomLevel) {
        Camera.Parameters params = AppContract.mCamera.getParameters();
        if (params.getZoom() != zoomLevel) {
            params.setZoom(zoomLevel);
            AppContract.mCamera.setParameters(params);
        }
    }

    @SuppressWarnings("deprecation")
    public static int cameraGetMaxZoom() {
        Camera.Parameters params = AppContract.mCamera.getParameters();
        return params.getMaxZoom();
    }

    @SuppressWarnings("deprecation")
    public static Camera.PictureCallback mCameraPictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                //showCameraErrorDialog("Failed to save image file.");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.e("[CameraClass]", e.toString());
                //showCameraErrorDialog("Failed to save image file." + "\n" + e.toString());
            } catch (IOException e) {
                //showCameraErrorDialog("Failed to save image file." + "\n" + e.toString());
            }


            Handler handler = new Handler();
            handler.postDelayed(new Runnable(){

                @Override
                public void run() {
                    AppContract.mCamera.startPreview();
                }}, 800);

        }
    };

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "GOLiFERemoteCamera");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                //showCameraErrorDialog("Failed to save image file.");
            }
        }

        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg");
    }

    public static boolean checkPermission(AppContract.PermissionType permissionType, final Activity activity) {
        return doPermissionCheck(permissionType, activity, null);
    }

    private static boolean doPermissionCheck(AppContract.PermissionType permissionType, final Activity activity, final Fragment fragment) {
        final int index = permissionType.ordinal();
        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(activity.getApplicationContext(), PERMISSION[index]);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                String label = "";
                try {
                    PackageManager packageManager = activity.getPackageManager();
                    PermissionGroupInfo permissionGroupInfo = packageManager.getPermissionGroupInfo(PERMISSION_GROUP[index], PackageManager.GET_META_DATA);
                    label = permissionGroupInfo.loadLabel(packageManager).toString();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                String message = String.format("Need you to turn on \"%s\" permission to work the function.", label);

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, PERMISSION[index])) {
                    AlertDialog.Builder descriptionDialog = new AlertDialog.Builder(activity);
                    descriptionDialog.setCancelable(false);
                    descriptionDialog.setMessage(message);
                    descriptionDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (fragment != null) {
                                fragment.requestPermissions(new String[]{PERMISSION[index]}, PERMISSION_REQUEST_CODE[index]);
                            } else {
                                ActivityCompat.requestPermissions(activity, new String[]{PERMISSION[index]}, PERMISSION_REQUEST_CODE[index]);
                            }
                            dialog.dismiss();
                        }
                    });
                    if (!activity.isFinishing()) {
                        descriptionDialog.show();
                    }
                } else {
                    message = message + "\n\n" + "Please go to [Settings] → [Apps] → [App Name] → [Permissions] to set.";
                    AlertDialog.Builder descriptionDialog = new AlertDialog.Builder(activity);
                    descriptionDialog.setCancelable(false);
                    descriptionDialog.setMessage(message);
                    descriptionDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            final Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setData(Uri.parse("package:" + activity.getPackageName()));
                            activity.startActivity(intent);
                        }
                    });
                    if (!activity.isFinishing()) {
                        descriptionDialog.show();
                    }
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
