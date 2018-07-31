/**
 * Project : GoFIT SDK
 * 
 * Demo App for GoFIT SDK.
 *
 * @author Rik Tsai <rik.tsai@goyourlife.com>
 * @link http://www.goyourlife.com
 * @copyright Copyright &copy; 2018 GOYOURLIFE INC.
 */

package com.goyourlife.gofit_demo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.golife.customizeclass.ScanBluetoothDevice;
import com.golife.customizeclass.SetCareSetting;
import com.golife.database.table.TablePulseRecord;
import com.golife.database.table.TableSleepRecord;
import com.golife.database.table.TableStepRecord;
import com.goyourlife.gofitsdk.GoFITSdk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MainActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static String _tag = "demo_menu";
    private static GoFITSdk _goFITSdk = null;
    private ScanBluetoothDevice mSelectDevice = null;
    private String mMacAddress = null;
    private String mPairingCode = null;
    private String mPairingTime = null;
    private String mProductID = null;
    private String sdk_license = null;
    private String sdk_certificate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        addPreferencesFromResource(R.layout.demo_menu);

        InitUI();

        // Read certificate from file
        sdk_certificate = null;
        try {
            InputStream inputstream = this.getAssets().open("client_cert.crt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            sdk_certificate = sb.toString();
        }
        catch (Exception e) {
            Log.e(_tag, e.toString());
            showToast("Exception : " + e.toString());
        }
    }

    void InitUI() {
        Preference pPref = (Preference) findPreference("sdk_init_reinit");
        pPref.setTitle("SDK init / reinit");
        pPref.setOnPreferenceClickListener(this);

        pPref = (Preference) findPreference("demo_function_scan");
        pPref.setOnPreferenceClickListener(this);

        pPref = (Preference) findPreference("demo_function_new_pairing");
        pPref.setOnPreferenceClickListener(this);

        pPref = (Preference) findPreference("demo_function_connect");
        pPref.setOnPreferenceClickListener(this);

        pPref = (Preference) findPreference("demo_function_setting");
        pPref.setOnPreferenceClickListener(this);

        pPref = (Preference) findPreference("demo_function_sync");
        pPref.setOnPreferenceClickListener(this);

        pPref = (Preference) findPreference("demo_function_clear");
        pPref.setOnPreferenceClickListener(this);

        pPref = (Preference) findPreference("demo_function_init");
        pPref.setOnPreferenceClickListener(this);

        pPref = (Preference) findPreference("demo_function_dfu");
        pPref.setOnPreferenceClickListener(this);

        pPref = (Preference) findPreference("demo_function_disconnect");
        pPref.setOnPreferenceClickListener(this);

        pPref = (Preference) findPreference("demo_battery");
        pPref.setOnPreferenceClickListener(this);

        pPref = (Preference) findPreference("demo_mac");
        pPref.setOnPreferenceClickListener(this);

        pPref = (Preference) findPreference("demo_sn");
        pPref.setOnPreferenceClickListener(this);

        pPref = (Preference) findPreference("demo_fw_version");
        pPref.setOnPreferenceClickListener(this);
    }

    SetCareSetting demoGenSettingObject() {
        // Demo - new Setting object
        SetCareSetting mCareSettings = _goFITSdk.getDefaultCareSettings();

        if (mCareSettings != null) {
            // Demo - generate system unit setting
            String systemUnit = mCareSettings.getSystemUnit();
            systemUnit = "imperial";
            mCareSettings.setSystemUnit(systemUnit);

            // Demo - generate time format setting
            String timeFormat = mCareSettings.getTimeFormat();
            timeFormat = "12";
            mCareSettings.setTimeFormat(timeFormat);

            // Demo - generate disconnect alert setting
            SetCareSetting.Switch disconnectAlert = mCareSettings.getEnableDisconnectAlert();
            disconnectAlert = SetCareSetting.Switch.True;
            mCareSettings.setEnableDisconnectAlert(disconnectAlert);
        }

        return mCareSettings;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("sdk_init_reinit")) {
            // GoFIT SDK initialize
            if (_goFITSdk == null) {
                // Read license if exist in local storage
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor pe = sp.edit();
                sdk_license = sp.getString("sdk_license", null);
                pe.apply();

                _goFITSdk = GoFITSdk.getInstance(this, sdk_certificate, sdk_license, new GoFITSdk.ReceivedLicenseCallback() {
                    @Override
                    public void onSuccess(String receivedLicense) {
                        Log.i(_tag, receivedLicense);
                        sdk_license = receivedLicense;

                        // Store license in local storage
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor pe = sp.edit();
                        pe.putString("sdk_license", sdk_license);
                        pe.commit();

                        showToast("SDK init OK : \n" + sdk_license);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        Log.e(_tag, "GoFITSdk.getInstance() : (callback) onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                        showToast("SDK init Error : \n" + errorMsg);
                    }
                });
                _goFITSdk.reInitInstance();
                showToast("SDK init!");
            }
            else {
                _goFITSdk.reInitInstance();
                showToast("SDK init!");
            }
        }

        else if (preference.getKey().equals("demo_function_scan")) {
            if (_goFITSdk != null) {
                Log.i(_tag, "demo_function_scan");

                // Demo - doScanDevice API
                _goFITSdk.doScanDevice(new GoFITSdk.DeviceScanCallback() {
                    @Override
                    public void onSuccess(ScanBluetoothDevice device) {
                        // TODO : TBD
                        Log.i(_tag, "doScanDevice() : onSuccess() : device = " + device.getDevice().getName() + ", " + device.getDevice().getAddress() + ", " + device.getRSSI() + ", " + device.getProductID());
                    }

                    @Override
                    public void onCompletion(ArrayList<ScanBluetoothDevice> devices) {
                        showToast("Scan complete");
                        for (ScanBluetoothDevice device : devices) {
                            Log.i(_tag, "doScanDevice() : onCompletion() : device = " + device.getDevice().getName() + ", " + device.getDevice().getAddress() + ", " + device.getRSSI() + ", " + device.getProductID());
                        }

                        Preference pPref = (Preference) findPreference("demo_function_scan");
                        if (devices.size() > 0) {
                            mSelectDevice = devices.get(0);
                            String summary = "Recommend Device : \n" + mSelectDevice.getDevice().getAddress() + ", " + mSelectDevice.getRSSI();
                            pPref.setSummary(summary);
                            Log.i(_tag, "doScanDevice() : onCompletion() : mSelectDevice = " + mSelectDevice.getDevice().getName() + ", " + mSelectDevice.getDevice().getAddress() + ", " + mSelectDevice.getRSSI() + ", " + mSelectDevice.getProductID());
                        } else {
                            pPref.setSummary("Device Not Found");
                        }
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        Log.e(_tag, "doScanDevice() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                        showToast("doScanDevice() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                    }
                });
            }
            else {
                showToast("SDK Instance invalid, needs `SDK init`");
            }
        }

        else if (preference.getKey().equals("demo_function_new_pairing")) {
            if (_goFITSdk != null) {
                Log.i(_tag, "demo_function_new_pairing");

                if (mSelectDevice != null) {
                    mMacAddress = mSelectDevice.getDevice().getAddress();
                }
                else {
                    Toast.makeText(MainActivity.this, "No Device Selected, `Scan Device` First!", Toast.LENGTH_SHORT).show();
                    return true;
                }

                // Demo - doNewPairing API
                _goFITSdk.doNewPairing(mSelectDevice, new GoFITSdk.NewPairingCallback() {
                    @Override
                    public void onSuccess(String pairingCode, String pairingTime) {
                        Log.i(_tag, "doNewPairing() : onSuccess() : Got pairingCode = " + pairingCode);
                        Log.i(_tag, "doNewPairing() : onSuccess() : Confirming...");
                        mPairingCode = pairingCode;
                        mPairingTime = pairingTime;
                        mConfirmPairingCodeHandler.postDelayed(mConfirmPairingCodeRunnable, 5000);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        Log.e(_tag, "doNewPairing() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                        showToast("doNewPairing() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                    }
                });
            }
            else {
                showToast("SDK Instance invalid, needs `SDK init`");
            }
        }

        else if (preference.getKey().equals("demo_function_connect")) {
            if (_goFITSdk != null) {
                Log.i(_tag, "demo_function_connect");

                // Demo - get connect information from local storage
                if (mMacAddress == null || mPairingCode == null || mPairingTime == null) {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor pe = sp.edit();
                    mMacAddress = sp.getString("macAddress", "");
                    mPairingCode = sp.getString("pairCode", "");
                    mPairingTime = sp.getString("pairTime", "");
                    mProductID = sp.getString("productID", "");
                    pe.apply();
                }

                // Demo - doConnectDevice API
                _goFITSdk.doConnectDevice(mMacAddress, mPairingCode, mPairingTime, mProductID, new GoFITSdk.GenericCallback() {
                    @Override
                    public void onSuccess() {
                        Log.i(_tag, "doConnectDevice() : onSuccess()");
                        showToast("Connect complete");

                        Preference pPref = (Preference) findPreference("demo_connect_status");
                        // Demo - isBLEConnect API
                        boolean isConnect = _goFITSdk.isBLEConnect();
                        String summary = isConnect ? "Connected" : "Disconnected";
                        pPref.setSummary(summary);

                        pPref = (Preference) findPreference("demo_function_connect");
                        pPref.setSummary(mMacAddress);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        Log.e(_tag, "doConnectDevice() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                        showToast("doConnectDevice() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                    }
                });
            }
            else {
                showToast("SDK Instance invalid, needs `SDK init`");
            }
        }

        else if (preference.getKey().equals("demo_function_setting")) {
            if (_goFITSdk != null) {
                Log.i(_tag, "demo_function_setting");

                // Demo - doSetSetting API
                SetCareSetting mCareSettings = demoGenSettingObject();

                _goFITSdk.doSetSettings(mCareSettings, new GoFITSdk.SettingsCallback() {
                    @Override
                    public void onCompletion() {
                        Log.i(_tag, "doSetSettings() : onCompletion()");
                        showToast("Setting OK");
                        Preference pPref = (Preference) findPreference("demo_function_setting");
                        String summary = "Setting OK";
                        pPref.setSummary(summary);
                    }

                    @Override
                    public void onProgress(String message) {
                        Log.i(_tag, "doSetSettings() : onProgress() : message = " + message);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        Log.e(_tag, "doSetSettings() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                        showToast("doSetSettings() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                    }
                });
            }
            else {
                showToast("SDK Instance invalid, needs `SDK init`");
            }
        }

        else if (preference.getKey().equals("demo_function_sync")) {
            if (_goFITSdk != null) {
                Log.i(_tag, "demo_function_sync");

                // Demo - doSyncFitnessData API
                _goFITSdk.doSyncFitnessData(new GoFITSdk.SyncCallback() {
                    @Override
                    public void onCompletion() {
                        Log.i(_tag, "doSyncFitnessData() : onCompletion()");
                        showToast("Sync complete!\nDetail fitness data show in `Logcat`");
                    }

                    @Override
                    public void onProgress(String message, int progress) {
                        Log.i(_tag, "doSyncFitnessData() : onProgress() : message = " + message + ", progress = " + progress);
                        Preference pPref = (Preference) findPreference("demo_function_sync");
                        String summary = String.format("%d", progress);
                        pPref.setSummary(summary);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        Log.e(_tag, "doSyncFitnessData() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                        showToast("doSyncFitnessData() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                    }

                    @Override
                    public void onGetFitnessData(ArrayList<TableStepRecord> stepRecords, ArrayList<TableSleepRecord> sleepRecords, ArrayList<TablePulseRecord> hrRecords) {
                        for (TableStepRecord step : stepRecords) {
                            Log.i(_tag, "doSyncFitnessData() : onGetFitnessData() : step = " + step.toJSONString());
                        }

                        for (TableSleepRecord sleep : sleepRecords) {
                            Log.i(_tag, "doSyncFitnessData() : onGetFitnessData() : sleep = " + sleep.toJSONString());
                        }

                        for (TablePulseRecord hr : hrRecords) {
                            Log.i(_tag, "doSyncFitnessData() : onGetFitnessData() : hr = " + hr.toJSONString());
                        }
                    }
                });
            }
            else {
                showToast("SDK Instance invalid, needs `SDK init`");
            }
        }

        else if (preference.getKey().equals("demo_function_clear")) {
            if (_goFITSdk != null) {
                Log.i(_tag, "demo_function_clear");

                // Demo - doClearDeviceData API
                _goFITSdk.doClearDeviceData(new GoFITSdk.GenericCallback() {
                    @Override
                    public void onSuccess() {
                        Log.i(_tag, "doClearDeviceData() : onSuccess()");
                        showToast("Clear Data OK");
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        Log.e(_tag, "doClearDeviceData() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                        showToast("doClearDeviceData() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                    }
                });
            }
            else {
                showToast("SDK Instance invalid, needs `SDK init`");
            }
        }

        else if (preference.getKey().equals("demo_function_init")) {
            if (_goFITSdk != null) {
                Log.i(_tag, "demo_function_init");

                // Demo - doInitialDevice API
                _goFITSdk.doInitialDevice(new GoFITSdk.GenericCallback() {
                    @Override
                    public void onSuccess() {
                        Log.i(_tag, "doInitialDevice() : onSuccess()");
                        showToast("Initialize OK");
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        Log.e(_tag, "doInitialDevice() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                        showToast("doInitialDevice() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                    }
                });
            }
            else {
                showToast("SDK Instance invalid, needs `SDK init`");
            }
        }

        else if (preference.getKey().equals("demo_function_dfu")) {
            if (_goFITSdk != null) {
                Log.i(_tag, "demo_function_dfu");

                // Demo - doDFU API
                _goFITSdk.doDFU(new GoFITSdk.DFUCallback() {
                    @Override
                    public void onCompletion() {
                        Log.i(_tag, "doDFU() : onCompletion()");
                        showToast("DFU OK");
                        Preference pPref = (Preference) findPreference("demo_function_dfu");
                        String summary = "100 %";
                        pPref.setSummary(summary);
                    }

                    @Override
                    public void onProgress(int progress) {
                        Log.i(_tag, "doDFU() : onProgress() : progress = " + progress);
                        Preference pPref = (Preference) findPreference("demo_function_dfu");
                        String summary = progress + " %";
                        pPref.setSummary(summary);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        Log.e(_tag, "doDFU() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                        showToast("doDFU() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                    }
                });
            }
            else {
                showToast("SDK Instance invalid, needs `SDK init`");
            }
        }

        else if (preference.getKey().equals("demo_function_disconnect")) {
            if (_goFITSdk != null) {
                Log.i(_tag, "demo_function_disconnect");
                showToast("Device Disconnect");

                // Demo - doDisconnectDevice API
                _goFITSdk.doDisconnectDevice();

                Preference pPref = (Preference) findPreference("demo_connect_status");
                // Demo - isBLEConnect API
                boolean isConnect = _goFITSdk.isBLEConnect();

                String summary = isConnect ? "Connected" : "Disconnected";
                pPref.setSummary(summary);

                pPref = (Preference) findPreference("demo_battery");
                pPref.setSummary("");

                pPref = (Preference) findPreference("demo_mac");
                pPref.setSummary("");

                pPref = (Preference) findPreference("demo_sn");
                pPref.setSummary("");

                pPref = (Preference) findPreference("demo_fw_version");
                pPref.setSummary("");

                pPref = (Preference) findPreference("demo_function_scan");
                pPref.setSummary("");

                pPref = (Preference) findPreference("demo_function_new_pairing");
                pPref.setSummary("");

                pPref = (Preference) findPreference("demo_function_setting");
                pPref.setSummary("");

                pPref = (Preference) findPreference("demo_function_connect");
                pPref.setSummary("");

                pPref = (Preference) findPreference("demo_function_sync");
                pPref.setSummary("");

                pPref = (Preference) findPreference("demo_function_dfu");
                pPref.setSummary("");
            }
            else {
                showToast("SDK Instance invalid, needs `SDK init`");
            }
        }

        else if (preference.getKey().equals("demo_battery")) {
            if (_goFITSdk != null) {
                Log.i(_tag, "demo_battery");

                // Demo - getDeviceBatteryValue API
                _goFITSdk.getDeviceBatteryValue(new GoFITSdk.GetDeviceInfoCallback() {
                    @Override
                    public void onSuccess(String info) {
                        Log.i(_tag, "getDeviceBatteryValue() : onSuccess() : info = " + info);
                        showToast("Get Battery OK");

                        Preference pPref = (Preference) findPreference("demo_battery");
                        pPref.setSummary(info);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        Log.e(_tag, "getDeviceBatteryValue() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                        showToast("getDeviceBatteryValue() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                    }
                });
            }
            else {
                showToast("SDK Instance invalid, needs `SDK init`");
            }
        }

        else if (preference.getKey().equals("demo_mac")) {
            if (_goFITSdk != null) {
                Log.i(_tag, "demo_mac");

                // Demo - getDeviceMAC API
                _goFITSdk.getDeviceMAC(new GoFITSdk.GetDeviceInfoCallback() {
                    @Override
                    public void onSuccess(String info) {
                        Log.i(_tag, "getDeviceMAC() : onSuccess() : info = " + info);
                        showToast("Get MAC address OK");

                        Preference pPref = (Preference) findPreference("demo_mac");
                        pPref.setSummary(info);
                        mMacAddress = info;
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        Log.e(_tag, "getDeviceMAC() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                        showToast("getDeviceMAC() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                    }
                });
            }
            else {
                showToast("SDK Instance invalid, needs `SDK init`");
            }
        }

        else if (preference.getKey().equals("demo_sn")) {
            if (_goFITSdk != null) {
                Log.i(_tag, "demo_sn");

                // Demo - getDeviceSN API
                _goFITSdk.getDeviceSN(new GoFITSdk.GetDeviceInfoCallback() {
                    @Override
                    public void onSuccess(String info) {
                        Log.i(_tag, "getDeviceSN() : onSuccess() : info = " + info);
                        showToast("Get Device SN OK");

                        Preference pPref = (Preference) findPreference("demo_sn");
                        pPref.setSummary(info);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        Log.e(_tag, "getDeviceSN() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                        showToast("getDeviceSN() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                    }
                });
            }
            else {
                showToast("SDK Instance invalid, needs `SDK init`");
            }
        }

        else if (preference.getKey().equals("demo_fw_version")) {
            if (_goFITSdk != null) {
                Log.i(_tag, "demo_fw_version");

                // Demo - getDeviceFWVersion API
                _goFITSdk.getDeviceFWVersion(new GoFITSdk.GetDeviceInfoCallback() {
                    @Override
                    public void onSuccess(String info) {
                        Log.i(_tag, "getDeviceFWVersion() : onSuccess() : info = " + info);
                        showToast("Get FW Version OK");

                        Preference pPref = (Preference) findPreference("demo_fw_version");
                        pPref.setSummary(info);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        Log.i(_tag, "getDeviceFWVersion() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                        showToast("getDeviceFWVersion() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                    }
                });
            }
            else {
                showToast("SDK Instance invalid, needs `SDK init`");
            }
        }

        return true;
    }

    private Handler mConfirmPairingCodeHandler = new Handler();
    private final Runnable mConfirmPairingCodeRunnable = new Runnable() {
        public void run() {
            mConfirmPairingCodeHandler.removeCallbacks(mConfirmPairingCodeRunnable);

            // Demo - confirmPairingCode API
            if (_goFITSdk != null) {
                mProductID = mSelectDevice.getProductID();
                _goFITSdk.doConfirmPairingCode(mPairingCode, mPairingTime, mProductID, new GoFITSdk.GenericCallback() {
                    @Override
                    public void onSuccess() {
                        Log.i(_tag, "doConfirmPairingCode() : onSuccess() : Pairing Complete!");
                        showToast("Pairing complete");

                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor pe = sp.edit();
                        pe.putString("productID", mProductID);
                        pe.commit();

                        Preference pPref = (Preference) findPreference("demo_function_new_pairing");
                        String summary = "Confirm Paring Code : " + mPairingCode + "(" + mPairingTime + ")";
                        pPref.setSummary(summary);

                        pPref = (Preference) findPreference("demo_connect_status");
                        // Demo - isBLEConnect API
                        boolean isConnect = _goFITSdk.isBLEConnect();
                        summary = isConnect ? "Connected" : "Disconnected";
                        pPref.setSummary(summary);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        Log.e(_tag, "doConfirmPairingCode() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                        showToast("doConfirmPairingCode() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                    }
                });
            }
            else {
                showToast("SDK Instance invalid, needs `SDK init`");
            }
        }
    };

    void showToast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}
