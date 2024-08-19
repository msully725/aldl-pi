package com.sullivan_digital_consulting.aldl_android

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var deviceListAdapter: ArrayAdapter<String>

    private val bluetoothPermissions = arrayOf(
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Request Bluetooth permissions if on Android 12 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasBluetoothPermissions()) {
                ActivityCompat.requestPermissions(this, bluetoothPermissions, REQUEST_CODE_BLUETOOTH)
            }
        }

        val btnScan: Button = findViewById(R.id.btnScan)
        val lvDevices: ListView = findViewById(R.id.lvDevices)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        deviceListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        lvDevices.adapter = deviceListAdapter

        btnScan.setOnClickListener {
            startBluetoothScan()
        }
    }

    private fun hasBluetoothPermissions(): Boolean {
        return bluetoothPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startBluetoothScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                deviceListAdapter.clear()
                val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(receiver, filter)
                bluetoothAdapter.startDiscovery()
            } else {
                // Request the necessary permissions if they are not granted
                ActivityCompat.requestPermissions(this, bluetoothPermissions, REQUEST_CODE_BLUETOOTH)
            }
        } else {
            // For Android versions below S, no need to check BLUETOOTH_SCAN permission
            deviceListAdapter.clear()
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            registerReceiver(receiver, filter)
            bluetoothAdapter.startDiscovery()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        val deviceName = device?.name
                        val deviceAddress = device?.address // MAC address
                        if (deviceName != null && deviceAddress != null) {
                            deviceListAdapter.add("$deviceName\n$deviceAddress")
                            deviceListAdapter.notifyDataSetChanged()
                        }
                    } else {
                        // Request the necessary permissions if they are not granted
                        ActivityCompat.requestPermissions(this@MainActivity, bluetoothPermissions, REQUEST_CODE_BLUETOOTH)
                    }
                } else {
                    // For Android versions below S, access the device name and address without permission check
                    val deviceName = device?.name
                    val deviceAddress = device?.address // MAC address
                    if (deviceName != null && deviceAddress != null) {
                        deviceListAdapter.add("$deviceName\n$deviceAddress")
                        deviceListAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_BLUETOOTH) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions granted, proceed with Bluetooth operations
            } else {
                // Permissions denied, handle accordingly
                showPermissionsDeniedDialog()
            }
        }
    }

    private fun showPermissionsDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permissions Required")
            .setMessage("Bluetooth permissions are required to scan for and connect to devices. Without these permissions, this functionality won't be available.")
            .setPositiveButton("Settings") { _, _ ->
                // Open app settings so the user can enable permissions
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    companion object {
        const val REQUEST_CODE_BLUETOOTH = 1
    }
}