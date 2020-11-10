package com.sherepenko.android.measureit.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent

@KoinApiExtension
abstract class BaseFragment(
    @LayoutRes private val contentLayoutRes: Int
) : Fragment(), KoinComponent {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(contentLayoutRes, container, false)

    override fun onResume() {
        super.onResume()
        setCurrentScreen()
    }

    private fun setCurrentScreen() {
        Firebase.analytics.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to javaClass.simpleName,
                FirebaseAnalytics.Param.SCREEN_CLASS to javaClass.canonicalName
            )
        )
    }
}
