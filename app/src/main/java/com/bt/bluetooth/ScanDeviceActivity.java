package com.bt.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ScanDeviceActivity extends AppCompatActivity {
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private static final int MY_PERMISSION_RESPONSE = 99;
    private static final int REQUEST_ENABLE_BT = 88;
    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private DeviceAdapter mAdapter;

    private Button mButtonScanDevice;
    private ListView mDeviceListView;
    private ProgressBar mProgressBarLoading;

    // Device scan callback.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                updateDeviceScanned(device);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_device);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            toast(getResources().getString(R.string.msg_bluetooth_not_support));
            finish();
            return;
        }

        // Prompt for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_RESPONSE);
        }

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            toast(getResources().getString(R.string.msg_bluetooth_not_support));
            finish();
        }

        mHandler = new Handler();
        mAdapter = new DeviceAdapter(getApplicationContext());
        mDeviceListView = findViewById(R.id.list_view_device);
        mButtonScanDevice = findViewById(R.id.button_scan);
        mProgressBarLoading = findViewById(R.id.progress_bar);
        mDeviceListView.setAdapter(mAdapter);
        mButtonScanDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }


        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void scan() {
        ViewUtils.disable(mButtonScanDevice);
        ViewUtils.visible(mProgressBarLoading);
        mAdapter.clear();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.cancelDiscovery();
                ViewUtils.eneble(mButtonScanDevice);
                ViewUtils.gone(mProgressBarLoading);
                if (mAdapter.getCount() == 0) {
                    toast(getResources().getString(R.string.msg_not_found_device));
                }
            }
        }, SCAN_PERIOD);

        mBluetoothAdapter.startDiscovery();

    }

    private void updateDeviceScanned(BluetoothDevice bluetoothDevice) {
        mAdapter.addDevice(bluetoothDevice);
    }
}
