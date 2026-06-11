package com.example.ads

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AdManager {
    // Simulated Remote Config keys
    private val _isAdsEnabled = MutableStateFlow(true)
    val isAdsEnabled: StateFlow<Boolean> = _isAdsEnabled.asStateFlow()

    private val _rewardedPoints = MutableStateFlow(0)
    val rewardedPoints: StateFlow<Int> = _rewardedPoints.asStateFlow()

    // Control displaying a full-screen interstitial ad or rewarded ad via state
    private val _currentActiveAd = MutableStateFlow<AdProgress?>(null)
    val currentActiveAd: StateFlow<AdProgress?> = _currentActiveAd.asStateFlow()

    sealed class AdProgress {
        object Interstitial : AdProgress()
        object Rewarded : AdProgress()
    }

    fun init(context: Context) {
        // Here, we would initialize actual SDKs (like AdMob MobileAds.initialize())
        // but for safety we mock successful configuration load
        _isAdsEnabled.value = true
    }

    fun setAdsEnabled(enabled: Boolean) {
        _isAdsEnabled.value = enabled
    }

    fun showInterstitial(onAdDismissed: () -> Unit) {
        if (!_isAdsEnabled.value) {
            onAdDismissed()
            return
        }
        _currentActiveAd.value = AdProgress.Interstitial
        // We'll let the UI host watch this flow and display a custom popup
    }

    fun showRewarded(onRewardEarned: () -> Unit) {
        if (!_isAdsEnabled.value) {
            onRewardEarned()
            return
        }
        _currentActiveAd.value = AdProgress.Rewarded
    }

    fun dismissAd(rewardEarned: Boolean = false) {
        val current = _currentActiveAd.value
        _currentActiveAd.value = null
        if (current is AdProgress.Rewarded && rewardEarned) {
            _rewardedPoints.value += 100
        }
    }
}
