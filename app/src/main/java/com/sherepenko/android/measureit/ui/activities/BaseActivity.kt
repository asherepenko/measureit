package com.sherepenko.android.measureit.ui.activities

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics

abstract class BaseActivity(@LayoutRes private val contentLayoutRes: Int) : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentLayoutRes)
    }

    override fun onResume() {
        super.onResume()
        setCurrentScreen()
    }

    private fun setCurrentScreen() {
        FirebaseAnalytics.getInstance(this@BaseActivity)
            .logEvent(
                FirebaseAnalytics.Event.SCREEN_VIEW,
                bundleOf(
                    FirebaseAnalytics.Param.SCREEN_NAME to javaClass.simpleName,
                    FirebaseAnalytics.Param.SCREEN_CLASS to javaClass.canonicalName
                )
            )
    }
}
