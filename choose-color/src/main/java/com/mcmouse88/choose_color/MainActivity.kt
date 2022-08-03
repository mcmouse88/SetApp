package com.mcmouse88.choose_color

import android.os.Bundle
import com.mcmouse88.choose_color.views.currentcolor.CurrentColorFragment
import com.mcmouse88.foundation.sideeffect.SideEffectPluginsManager
import com.mcmouse88.foundation.sideeffect.dialogs.plugin.DialogsPlugin
import com.mcmouse88.foundation.sideeffect.intents.plugin.IntentsPlugin
import com.mcmouse88.foundation.sideeffect.navigator.plugin.NavigatorPlugin
import com.mcmouse88.foundation.sideeffect.navigator.plugin.StackFragmentNavigator
import com.mcmouse88.foundation.sideeffect.permissions.plugin.PermissionsPlugin
import com.mcmouse88.foundation.sideeffect.resourses.plugin.ResourcesPlugin
import com.mcmouse88.foundation.sideeffect.toasts.plugin.ToastsPlugin
import com.mcmouse88.foundation.views.activity.BaseActivity

class MainActivity : BaseActivity() {

    /**
     * В данном методе мы регистрируем все имеющиеся у нас side effect. После чего мы можем
     * передавать в конструкторы viewModel интерфейсы, отвечающие за глобальные side effect.
     */
    override fun registerPlugins(manager: SideEffectPluginsManager) = with(manager) {
        val navigator = createNavigator()
        register(ToastsPlugin())
        register(ResourcesPlugin())
        register(NavigatorPlugin(navigator))
        register(PermissionsPlugin())
        register(DialogsPlugin())
        register(IntentsPlugin())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun createNavigator() = StackFragmentNavigator(
        containerId = R.id.fragment_container,
        defaultTitle = getString(R.string.app_name),
        animations = StackFragmentNavigator.Animations(
            enterAnim = R.anim.enter,
            exitAnim = R.anim.exit,
            popEnterAnim = R.anim.pop_enter,
            popExitAnim = R.anim.pop_exit
        ),
        initialScreen = { CurrentColorFragment.Screen() }
    )
}