package com.mcmouse88.box_quiz

import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.mcmouse88.box_quiz.contract.*
import com.mcmouse88.box_quiz.databinding.ActivityMainBinding
import com.mcmouse88.box_quiz.fragments.BoxSelectionFragment
import com.mcmouse88.box_quiz.fragments.OptionsFragment

/**
 * В классе [MainActivity] будет написана реализация интерфейса [Navigator]
 */
class MainActivity : AppCompatActivity(), Navigator {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding ?: throw NullPointerException("ActivityMainBinding is null")

    private val navController by lazy {
        val navHost = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment
        navHost.navController
    }

    private var currentFragment: Fragment? = null

    private val fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fragmentManager: FragmentManager,
            fragment: Fragment,
            view: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fragmentManager, fragment, view, savedInstanceState)
            if (fragment is NavHostFragment) return
            currentFragment = fragment
            updateUI()
        }
    }

    /**
     * В методе [onCreate] мы проверяем запущено ли приложение впервые, если да, то запускаем
     * транзакцию через [supportFragmentManager]. для реализации интерфейсов [HasCustomAction],
     * мы вызываем у [supportFragmentManager] метод [registerFragmentLifecycleCallbacks], куда
     * передаем в качестве параметров объект абсрактного класса [FragmentLifecycleCallbacks],
     * созданный нами при помощи анонимного класса, и в методе [onDestroy] отписываемся от него
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        setSupportActionBar(binding.toolbar)

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentListener)
        currentFragment = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        updateUI()
        return true
    }

    override fun onSupportNavigateUp() = navController.navigateUp() || super.onSupportNavigateUp()

    override fun showBoxSelectionScreen(options: Options) {
        launchDestination(R.id.boxSelectionFragment, BoxSelectionFragment.createArgs(options))
    }

    override fun showOptionsScreen(options: Options) {
        launchDestination(R.id.optionsFragment, OptionsFragment.createArgs(options))
    }

    override fun showAboutScreen() {
        launchDestination(R.id.aboutFragment)
    }

    override fun showBoxScreen() {
        launchDestination(R.id.boxFragment)
    }

    /**
     * для перехода назад просто будем вызывать метод, реализации системной кнопки назад
     * (если в стеке ничего нет, то при вызове этого метода приложение закроется)
     */
    override fun goBack() {
        onBackPressed()
    }

    /**
     * для перехода на экран меню, вызовем метод popBackStack, куда передадим параметр
     * [destination], и false, чтобы не включать его в удаление, таким образом вызов этого
     * метода с параметрами удалит из стека всю навигацию, оставив в нем только экран меню
     */
    override fun goToMenu() {
        navController.popBackStack(R.id.menuFragment, false)
    }

    /**
     * метод [setFragmentResult] предназначен для того, чтобы отправлять результаты работы
     * одного фрагмента в другой фрагмент, у него два параметра, это ключ и сам результат.
     * Вместо [result.javaClass.name] можно использовать любую константу типа String. Чтобы не
     * создавать для каждого класса свою константу, в качестве ключа будет использоваться
     * имя класса. Вместо длинной процедуры создания объекта [Bundle] можно использовать
     * функцию [bundleOf]
     */
    override fun <T : Parcelable> publishResult(result: T) {
        supportFragmentManager.setFragmentResult(
            result.javaClass.name,
            bundleOf(KEY_RESULT to result)
        )
    }

    /**
     * Через эту функцию фрагмент будет принимать результат от другого фрагмента, в качетсве
     * параметра передается LifecycleOwner для того, чтобы фрагмент принимал данные в нужный
     * момент, а не в момент когда он в методе onStop, onPause и т.д.
     */
    override fun <T : Parcelable> listenResult(
        clazz: Class<T>,
        owner: LifecycleOwner,
        listener: ResultListener<T>
    ) {
        supportFragmentManager.setFragmentResultListener(clazz.name, owner) { key, bundle ->
            listener.invoke(
                bundle.getParcelable(KEY_RESULT)
                    ?: throw NullPointerException("bundle in method listenResult with MainActivity is null")
            )
        }
    }

    private fun launchDestination(destinationId: Int, args: Bundle? = null) {
        navController.navigate(
            destinationId,
            args,
            navOptions {
                anim {
                    R.anim.slide_in
                    R.anim.fade_out
                    R.anim.fade_in
                    R.anim.slide_out
                }
            }
        )
    }

    private fun updateUI() {
        val fragment = currentFragment
        binding.toolbar.title = if (fragment is HasCustomTitle) {
            getString(fragment.getTitleRes())
        } else {
            getString(R.string.app_name)
        }

        if (navController.currentDestination?.id == navController.graph.startDestinationId) {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        if (fragment is HasCustomAction) {
            createCustomToolBarAction(fragment.getCustomAction())
        } else {
            binding.toolbar.menu.clear()
        }
    }

    private fun createCustomToolBarAction(action: CustomAction) {
        binding.toolbar.menu.clear()

        val iconDrawable = DrawableCompat.wrap(
            ContextCompat.getDrawable(
                this,
                action.iconRes
            )
                ?: throw NullPointerException("DrawableCompat in createCustomToolBarAction in MainActivity is null")
        )
        iconDrawable.setTint(Color.WHITE)

        val menuItem = binding.toolbar.menu.add(action.textRes)
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        menuItem.icon = iconDrawable
        menuItem.setOnMenuItemClickListener {
            action.onCustomAction.run()
            return@setOnMenuItemClickListener true
        }
    }

    companion object {
        private const val KEY_RESULT = "key_result"
    }
}