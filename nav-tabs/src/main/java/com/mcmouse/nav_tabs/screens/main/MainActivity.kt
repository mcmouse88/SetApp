package com.mcmouse.nav_tabs.screens.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.mcmouse.nav_tabs.R
import com.mcmouse.nav_tabs.Repositories
import com.mcmouse.nav_tabs.databinding.ActivityMainBinding
import com.mcmouse.nav_tabs.screens.main.tabs.TabsFragment
import com.mcmouse.nav_tabs.utils.viewModelCreator
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModelCreator { MainActivityViewModel(Repositories.accountsRepository) }

    private var navController: NavController? = null

    private val topLevelDestination = setOf(getTabsDestination(), getSignInDestination())

    private val fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            if (f is TabsFragment || f is NavHostFragment) return
            onNavControllerActivated(f.findNavController())
        }
    }

    private val destinationListener = NavController.OnDestinationChangedListener {_, destination, arguments ->
        supportActionBar?.title = prepareTitle(destination.label, arguments)
        supportActionBar?.setDisplayHomeAsUpEnabled(!isStartDestination(destination))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Repositories.init(applicationContext)
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        setSupportActionBar(binding.toolbar)

        val navController = getRootNavController()
        prepareRootNavController(isSignIn(), navController)
        onNavControllerActivated(navController)

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, true)

        viewModel.username.observe(this) {
            binding.tvToolbarUserName.text = it
        }
    }

    override fun onDestroy() {
        navController = null
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentListener)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (isStartDestination(navController?.currentDestination)) super.onBackPressed()
        else navController?.popBackStack()
    }

    override fun onSupportNavigateUp(): Boolean =
        (navController?.navigateUp() ?: false) || super.onSupportNavigateUp()

    private fun prepareRootNavController(isSignedIn: Boolean, navController: NavController) {
        val graph = navController.navInflater.inflate(getMainNavigationGraphId())
        graph.setStartDestination(
            if (isSignedIn) getTabsDestination() else getSignInDestination()
        )
        navController.graph = graph
    }

    private fun onNavControllerActivated(navController: NavController) {
        if (this.navController == navController) return
        this.navController?.removeOnDestinationChangedListener(destinationListener)
        navController.addOnDestinationChangedListener(destinationListener)
        this.navController = navController
    }

    private fun getRootNavController(): NavController {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        return navHost.navController
    }

    private fun isStartDestination(destination: NavDestination?): Boolean {
        if (destination == null) return false
        val graph = destination.parent ?: return false
        val startDestination = topLevelDestination + graph.startDestinationId
        return startDestination.contains(destination.id)
    }

    private fun prepareTitle(label: CharSequence?, arguments: Bundle?): String {
        if (label == null) return ""
        val title = StringBuffer()
        val fillInPattern = Pattern.compile("\\{(.+?)\\}")
        val matcher = fillInPattern.matcher(label)
        while (matcher.find()) {
            val argName = matcher.group(1)
            if (arguments != null && arguments.containsKey(argName)) {
                matcher.appendReplacement(title, "")
                title.append(arguments[argName].toString())
            } else {
                throw IllegalArgumentException("Could not find $argName in $arguments to fill label $label")
            }
        }
        matcher.appendTail(title)
        return title.toString()
    }

    /**
     * Здесь будет происходить проверка залогинен пользоваетль или нет, через свойство extras
     * получим bundle, который по графу навигации передает [SplashFragment], из которого получим
     * аргумент типа Boolean. После проверки пользователя, через метод [getMainNavigationGraphId]
     * будет создаваться граф навигации, идентификатор которого и будет возвращать этот метод.
     * Методы [getTabsDestination] и [getSignInDestination] возвращают идентификатор
     * фрагмента Таба если пользоваетль залогинен или авторизации, если же нет.
     */
    private fun isSignIn(): Boolean {
        val bundle = intent.extras ?: throw IllegalArgumentException("No required arguments")
        val args = MainActivityArgs.fromBundle(bundle)
        return args.isSignedIn
    }

    private fun getMainNavigationGraphId(): Int = R.navigation.main_graph

    private fun getTabsDestination(): Int = R.id.tabsFragment

    private fun getSignInDestination(): Int = R.id.signInFragment
}