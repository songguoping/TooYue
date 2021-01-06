package com.coderfeng.tooyue.home

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.plusAssign
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.coderfeng.tooyue.R
import com.coderfeng.tooyue.common.navigation.KeepStateNavigator
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var navHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_home_fragment) as NavHostFragment
        val navController = findNavController(R.id.nav_home_fragment)
        val navigator = KeepStateNavigator(this, navHostFragment.childFragmentManager, R.id.nav_home_fragment)
        navController.navigatorProvider.addNavigator("keep_state_fragment",navigator)
        navController.setGraph(R.navigation.nav_graph)

        navigation.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp() =
        Navigation.findNavController(this, R.id.nav_home_fragment).navigateUp()
}