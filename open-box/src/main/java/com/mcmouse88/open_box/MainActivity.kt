package com.mcmouse88.open_box

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI

/**
 * Чтобы подключить верхний action bar, при использовании navigation component, нужно у класса
 * [NavigationUI] вызвать статический метод [setupActionBarWithNavController()]. Для того, чтобы
 * в action bar отображались адекватные названия (например название фрагметна), нужно поменять
 * label по умолчанию в графе навигации, и также лучше использовать для этого строковые ресурсы.
 * Также в этот label (в nav_graph) можно передавать параметры, чтоб в зависимости от параметров
 * в action bar были разные надписи. Важно, аргумент в nav_graph должен называться также как и ключ
 * при передаче аргумента через [Bundle]. Чтобы заработала кнопка назад на action bar нужно
 * в активити переопределить метод [onSupportNavigateUp].
 */
class MainActivity : AppCompatActivity() {

    private val navController: NavController by lazy {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navHost.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()

    }
}