package com.thiago.dogs.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.thiago.dogs.R
import com.thiago.dogs.util.PERMISSION_SEND_SMS

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)  // Set the Toolbar as the ActionBar

        // Ensure the NavHostFragment is correctly retrieved
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as? NavHostFragment
            ?: throw IllegalStateException("NavHostFragment not found")
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)  // Setup ActionBar with NavController
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)

    }

    fun checkSmsPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                AlertDialog.Builder(this)
                    .setTitle("Send SMS permission")
                    .setMessage("This app requires access to send an SMS.")
                    .setPositiveButton("Ask me") { dialog, which ->
                        requestSmsPermission()
                    }
                    .setNegativeButton("No") { dialog, which ->
                        notifyDetailFragment(false)
                    }
                    .show()
            } else {
                requestSmsPermission()
            }
        } else {
            notifyDetailFragment(true)
        }
    }

    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), PERMISSION_SEND_SMS)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            PERMISSION_SEND_SMS -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    notifyDetailFragment(true)
                } else {
                    notifyDetailFragment(false)
                }
            }
        }
    }

    private fun notifyDetailFragment(permissionGranted: Boolean) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val activeFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
        if (activeFragment is DetailFragment) {
            activeFragment.onPermissionResult(permissionGranted)
        }
    }
}
