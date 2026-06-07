package ui.screens.onboarding

import androidx.lifecycle.ViewModel
import data.prefs.SharedPrefs
import data.repository.AppRepository

class OnboardingViewModel(
    private val appRepository: AppRepository,
    private val sharedPrefs: SharedPrefs,
) : ViewModel() {
    fun setPastOnboarding() {
        sharedPrefs.setNotFirstOpenApp(true)
        sharedPrefs.setPastOnboarding(true)
    }

    suspend fun getShowStartPaywall(): Boolean {
        return appRepository.getShowStartPaywall()
    }

    suspend fun getShowTrialGeneration(): Boolean {
        if (sharedPrefs.isTrialUsed()) return false
        return appRepository.getShowTrialGeneration()
    }
}