package com.mcmouse88.sharedpreferences

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mcmouse88.sharedpreferences.databinding.ActivityEmptyBinding

const val APP_PREFERENCES = "APP_PREFERENCES"
const val PREF_SOME_TEXT_VALUE = "PREF_SOME_TEXT_VALUE"

/**
 * Чтобы получить [Preferences] используется два метода [getPreferences()] для получения
 * внутри конкретной активити и [getSharedPreferences()] для получения [Preferences] всего
 * приложения. Чтобы получить данные определенного типа (используются только примитивные типы данных
 * и String) из [SharedPreferences] используется метод [getString()], (getInt, getBoolean и т.д.),
 * где указывается ключ(тип String), под которым данные были сохранены и значение по умолчанию,
 * на случай если [SharedPreferences] еще не хранит в себе никаких значений. Чтобы положить
 * значения в [SharedPreferences] у него вызывается метод [edit()] (создается объект интерфейса
 * [Editor]), у которого вызываются методы [putString()] (putInt(), putBoolean() и т.д., при этом
 * можно несколько разных объектов сразу положить в [SharedPreferences] в этой транзакции), и
 * в конце вызывается метод [apply()], который завершает транзакцию. Также можно создать
 * слушаетль изменений в [SharedPreferences], благодаря которому можно обработать ситуация,
 * связанные с обновлением данных в [SharedPreferences] (например поменять интерфейс и т.д.).
 * Самы preferences хранятся в памяти телефона в формате XML в каталоге data -> пакет приложения ->
 * shared_prefs.
 */
class EmptyActivity : AppCompatActivity() {

    private var _binding: ActivityEmptyBinding? = null
    private val binding: ActivityEmptyBinding
        get() = _binding ?: throw NullPointerException("ActivityEmptyBinding is null")

    private lateinit var preferences: SharedPreferences

    private val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { pref, key ->
        if (key == PREF_SOME_TEXT_VALUE) {
            binding.tvCurrentVale.text = pref.getString(key, "")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEmptyBinding.inflate(layoutInflater).also { setContentView(it.root) }

        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

        val currentValuePref = preferences.getString(PREF_SOME_TEXT_VALUE, "")
        binding.etValue.setText(currentValuePref)
        binding.tvCurrentVale.text = currentValuePref
        binding.btSave.setOnClickListener {
            val value = binding.etValue.text.toString()
            preferences.edit()
                .putString(PREF_SOME_TEXT_VALUE, value)
                .apply()
            Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show()
        }

        preferences.registerOnSharedPreferenceChangeListener(prefListener)
    }

    override fun onDestroy() {
        _binding = null
        preferences.unregisterOnSharedPreferenceChangeListener(prefListener)
        super.onDestroy()
    }
}