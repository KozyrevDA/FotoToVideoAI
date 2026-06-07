package data.prefs

import SharedPreferences
import androidx.compose.ui.graphics.ImageBitmap
import data.model.AuthMethod
import data.model.AuthTokens
import data.model.QueueGen
import getSharedPreferences
import kotlinx.coroutines.flow.Flow

const val NOT_FIRST_OPEN_APP = "not_first_open_app"
private const val PAST_ONBOARDING = "past_onboarding"
private const val AUTH_METHOD = "auth_method"
private const val DEVICE_UID = "device_uid"
private const val LAST_COUNT_COINS = "last_count_coins"
private const val LAST_IS_PRO_SUB = "last_is_pro_sub"
const val IS_PAST_AUTH = "is_past_auth"
private const val REMOTE_CONFIG_SHOW_ONBOARDING = "rc_show_onboarding"
private const val REMOTE_CONFIG_SHOW_START_PAYWALL = "rc_show_start_paywall"
private const val REMOTE_CONFIG_SHOW_TRIAL_GENERATION = "rc_show_trial_generation"
private const val TRIAL_USED = "trial_used"
private const val REMOTE_CONFIG_MONTH_SUB_COINS = "rc_month_sub_coins"
private const val QUEUE_UIDS = "queue_uids"
const val ACCESS_TOKEN = "access_token"
const val REFRESH_TOKEN = "refresh_token"
const val IMAGE_PICK_PHOTO_2 = "image_pick_filter"
const val IMAGE_PICK_PHOTO_1 = "image_pick_avatar"
const val LAST_FILTER_UID = "last_filter_uid"
const val LAST_PROMPT = "last_promt"

class SharedPrefs(private val prefs: SharedPreferences = getSharedPreferences()) {
    fun setNotFirstOpenApp(value: Boolean) {
        prefs.putBool(NOT_FIRST_OPEN_APP, value)
    }

    fun isNotFirstOpenApp(): Boolean {
        return prefs.getBool(NOT_FIRST_OPEN_APP)
    }

    fun setPastOnboarding(value: Boolean) {
        prefs.putBool(PAST_ONBOARDING, value)
    }

    fun isPastOnboarding(): Boolean {
        return prefs.getBool(PAST_ONBOARDING)
    }

    fun savePhoto(bitmap: ImageBitmap?, name: String): Boolean {
        return prefs.savePhoto(bitmap, name)
    }

    fun getPhoto(name: String): Flow<ImageBitmap?> {
        return prefs.getPhoto(name)
    }

    fun putAuthMethod(authMethod: AuthMethod?) {
        if (authMethod == null) {
            prefs.putString(AUTH_METHOD, "")
        } else {
            prefs.putString(AUTH_METHOD, authMethod.name)
        }
    }

    fun getAuthMethod(): AuthMethod? {
        val authMethodString = prefs.getString(AUTH_METHOD)
        if (authMethodString.isNullOrBlank()) return null

        return try {
            AuthMethod.valueOf(authMethodString)
        } catch (e: Exception) {
            null
        }
    }

    fun putDeviceUid(uid: String) {
        prefs.putString(DEVICE_UID, uid)
    }

    fun getDeviceUid(): String? {
        return prefs.getString(DEVICE_UID)
    }

    fun putAuthTokens(accessToken: String, refreshToken: String) {
        prefs.putString(ACCESS_TOKEN, accessToken)
        prefs.putString(REFRESH_TOKEN, refreshToken)
    }

    fun getAuthTokens(): AuthTokens? {
        val accessToken = prefs.getString(ACCESS_TOKEN)
        val refreshToken = prefs.getString(REFRESH_TOKEN)

        if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
            return null
        }

        return AuthTokens(accessToken, refreshToken)
    }

    fun putLastCountCoins(value: Int?) {
        value?.let { prefs.putInt(LAST_COUNT_COINS, it) }
    }

    fun getLastCountCoins(): Int {
        return prefs.getInt(LAST_COUNT_COINS)
    }

    fun putLastIsProSub(value: Boolean) {
        prefs.putBool(LAST_IS_PRO_SUB, value)
    }

    fun getLastIsProSub(): Boolean {
        return prefs.getBool(LAST_IS_PRO_SUB)
    }

    fun putUidLastFilter(uid: String) {
        prefs.putString(LAST_FILTER_UID, uid)
    }

    fun getUidLastFilter(): String? {
        return prefs.getString(LAST_FILTER_UID)
    }

    fun putLastPrompt(prompt: String) {
        prefs.putString(LAST_PROMPT, prompt)
    }

    fun getLastPrompt(): String? {
        return prefs.getString(LAST_PROMPT)
    }

    fun putPastAuth(value: Boolean) {
        prefs.putBool(IS_PAST_AUTH, value)
    }

    fun isPastAuth(): Boolean {
        return prefs.getBool(IS_PAST_AUTH)
    }

    fun putShowOnboarding(value: Boolean) {
        prefs.putBool(REMOTE_CONFIG_SHOW_ONBOARDING, value)
    }

    fun isShowOnboarding(): Boolean {
        return prefs.getBool(key = REMOTE_CONFIG_SHOW_ONBOARDING, defValue = true)
    }

    fun putShowStartPaywall(value: Boolean) {
        prefs.putBool(REMOTE_CONFIG_SHOW_START_PAYWALL, value)
    }

    fun isShowStartPaywall(): Boolean {
        return prefs.getBool(key = REMOTE_CONFIG_SHOW_START_PAYWALL, defValue = true)
    }

    fun putMonthSubCoins(value: Int?) {
        value?.let { prefs.putInt(REMOTE_CONFIG_MONTH_SUB_COINS, it) }
    }

    fun getMonthSubCoins(): Int? {
        val value = prefs.getInt(REMOTE_CONFIG_MONTH_SUB_COINS)
        return if (value == 0) null else value
    }

    fun putQueueUids(list: List<QueueGen>) {
        prefs.putString(QUEUE_UIDS, list.joinToString("|") { it.uid })
    }

    fun getQueueUids(): Set<String> {
        return prefs.getString(QUEUE_UIDS)
            ?.split("|")
            ?.filter { it.isNotBlank() }
            ?.toSet()
            ?: emptySet()
    }

    fun putShowTrialGeneration(value: Boolean) {
        prefs.putBool(REMOTE_CONFIG_SHOW_TRIAL_GENERATION, value)
    }

    fun isShowTrialGeneration(): Boolean {
        return prefs.getBool(key = REMOTE_CONFIG_SHOW_TRIAL_GENERATION, defValue = false)
    }

    fun putTrialUsed(value: Boolean) {
        prefs.putBool(TRIAL_USED, value)
    }

    fun isTrialUsed(): Boolean {
        return prefs.getBool(TRIAL_USED)
    }
}