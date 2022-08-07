package com.mcmouse88.permissions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mcmouse88.permissions.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    /**
     * для того, чтобы запращивать permissions через Activity Result Api, нужно создать два
     * лаунчера. Так как первый запрашивает только одно разрешение, то нужно передать
     * в качестве парметра [RequestPermission], в таком случае в параметр лямбду попадет згначение
     * типа Boolean. Для запроса массива permissions(как во втором случае), нужно использовать
     * [RequestMultiplePermissions], который будет в лямбду передавать Map, где ключ само
     * разрешение, а значение Boolean.
     */
    private val permissionCameraAccessLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::gotPermissionResultForCamera
    )

    private val permissionLocationAndRecordAudioLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        ::gotPermissionResultForLocationAndRecordAudio
    )

    /**
     * для того чтобы запросить разрешение желательно сначала проверить не было ли оно дано ранее.
     * Это можно сделать через статический метод [checkSelfPermission] класса [ContextCompat].
     * Данный метод принимает два параметра [Context] и само разрешение в виде константы типа
     * String, расположенные в классе Manifest пакета android. Чтобы запросить разрешение у
     * пользователя на предоставление permission нужно вызвать метод [requestPermissions] у класса
     * [ActivityCompat] (чтобы не выполнять проверки на версию андроид). Данный метод в качестве
     * параметров принимает Активити, массив permissions, которые мы хотим запросить, и requestCode
     * типа Int, по которому потом будем получать результат ответа в методе
     * [onRequestPermissionsResult].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonFutureOne.setOnClickListener {
            permissionCameraAccessLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.buttonFeatureTwo.setOnClickListener {
            permissionLocationAndRecordAudioLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO)
            )
        }
    }

    private fun onCameraPermissionGranted() {
        Toast.makeText(this, getString(R.string.camera_permission_granted), Toast.LENGTH_SHORT).show()
    }

    private fun askForAccessPermission() {
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        if (packageManager.resolveActivity(appSettingsIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            Toast.makeText(this, getString(R.string.permissions_denied_forever), Toast.LENGTH_SHORT).show()
        } else {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.permission_denied))
                .setMessage(getString(R.string.open_app_settings))
                .setPositiveButton(getString(R.string.open)) { _, _ ->
                    startActivity(appSettingsIntent)
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                }
                .create()
                .show()
        }
    }

    private fun gotPermissionResultForCamera(granted: Boolean) {
        if (granted) {
            onCameraPermissionGranted()
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            } else {
                askForAccessPermission()
            }
        }
    }

    private fun gotPermissionResultForLocationAndRecordAudio(grantResults: Map<String, Boolean>) {
        if (grantResults.entries.all { it.value }) {
            Toast.makeText(this, getString(R.string.rec_audio_and_location_granted), Toast.LENGTH_SHORT).show()
        }
    }

    private companion object {
        private const val REQUEST_CODE_FOR_CAMERA_ACCESS = 111
        private const val REQUEST_CODE_FOR_RECORD_AUDIO_ACCESS = 222
    }
}