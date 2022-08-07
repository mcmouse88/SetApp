package com.mcmouse88.permissions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mcmouse88.permissions.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

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

            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED)
            ) {
                onCameraPermissionGranted()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CODE_FOR_CAMERA_ACCESS
                )
            }
        }

        binding.buttonFeatureTwo.setOnClickListener {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO),
                REQUEST_CODE_FOR_RECORD_AUDIO_ACCESS
            )
        }
    }

    /**
     * Метод для обработки результатов ответа пользователя при подтверждении или отклонении
     * permission. Первый аргумент requestCode, который мы получаем при запрашивании разрешения,
     * второй массив permissions, который мы запрашивали, а третий результаты ответа пользователя
     * представленные в виде массива, содержащие в себе Int с константами (PERMISSION_GRANTED,
     * PERMISSION_DENIED и т.д.), по идексам соотвествующие массиву второго параметра. У отклонения
     * разрешения также есть два типа, отклонение навсегда, и отклонение с возможностью повторно
     * запросить разрешение. Для того, чтобы проверить не заблокировал ли пользователь навсегда
     * разрешение на доступ к чему-либо используестя метод [shouldShowRequestPermissionRationale].
     * Если метод возвращает true, то значит можно запросить еще раз предоставление разрешения.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_FOR_CAMERA_ACCESS -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onCameraPermissionGranted()
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                    } else {
                        askForAccessPermission()
                    }
                }
            }
            REQUEST_CODE_FOR_RECORD_AUDIO_ACCESS -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Toast.makeText(this, getString(R.string.rec_audio_and_location_granted), Toast.LENGTH_SHORT).show()
                }
            }
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

    private companion object {
        private const val REQUEST_CODE_FOR_CAMERA_ACCESS = 111
        private const val REQUEST_CODE_FOR_RECORD_AUDIO_ACCESS = 222
    }
}