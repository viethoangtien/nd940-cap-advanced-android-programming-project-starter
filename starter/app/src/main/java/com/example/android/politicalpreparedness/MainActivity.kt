package com.example.android.politicalpreparedness

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("turn on device location request code: $requestCode")
        supportFragmentManager.primaryNavigationFragment?.getChildFragmentManager()?.fragments?.let {
            for (fragment in it) {
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}
