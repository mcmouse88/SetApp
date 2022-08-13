package com.mcmouse.nav_tabs.screens.main.tabs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.mcmouse.nav_tabs.R
import com.mcmouse.nav_tabs.databinding.FragmentTabsBinding

class TabsFragment : Fragment(R.layout.fragment_tabs) {

    private var _binding: FragmentTabsBinding? = null
    private val binding: FragmentTabsBinding
        get() = _binding ?: throw NullPointerException("FragmentTabsBinding is null")

    /**
     * Чтобы подключить navigation component к bottomNavigationView нам нужно получить
     * [NavController], который относится к хосту фрагмента, который находится во fragment
     * container, сделаем это через [childFragmentManager] так как находимся уже внутри фрагмента
     * (если бы контейнер был внутри активити, тогда [parentFragmentManager]), получаем текущий
     * фрагмент по идентификатору, и явно приводим его к [NavHostFragment], ну и после чего
     * получаем из него navController. Чтобы соединить контенер с [bottomNavigationView]
     * используется статический метод [setupWithNavController] класса [NavigationUI], в который
     * передается [bottomNavigationView] и navController в качестве параметров.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTabsBinding.bind(view)

        val navHost = childFragmentManager.findFragmentById(R.id.tabs_container) as NavHostFragment
        val navController = navHost.navController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}