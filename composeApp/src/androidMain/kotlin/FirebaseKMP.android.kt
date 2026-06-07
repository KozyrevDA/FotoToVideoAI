import android.content.Context
import android.os.Bundle
import co.touchlab.crashkios.crashlytics.enableCrashlytics
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import org.nla.phototovideoai.BuildConfig
import org.nla.phototovideoai.app.AndroidApp

class AndroidFirebaseKMP(private val appContext: Context) : FirebaseKMP {
    val crashlytics get() = Firebase.crashlytics

    override fun initialize() {
        with(appContext) {
            FirebaseApp.initializeApp(this)
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
            enableCrashlytics()
        }
    }

    override fun reportEvent(eventName: String) {
        kotlin.runCatching {
            Firebase.analytics.logEvent(eventName, null)
        }
    }

    override fun reportEvent(eventName: String, parameters: Map<String, Any>) {
        kotlin.runCatching {
            Firebase.analytics.logEvent(
                eventName,
                Bundle().apply {
                    parameters.forEach { (key, value) ->
                        when (value) {
                            is String -> putString(key, value)
                            is Int -> putInt(key, value)
                            is Long -> putLong(key, value)
                            is Double -> putDouble(key, value)
                            is Float -> putFloat(key, value)
                            is Boolean -> putBoolean(key, value)
                            is List<*> -> putString(
                                key,
                                value.joinToString(", ")
                            )

                            else -> putString(key, value.toString())
                        }
                    }
                }
            )
        }
    }

    override fun recordException(throwable: Throwable) {
        kotlin.runCatching {
            Firebase.crashlytics.recordException(throwable)
        }
    }
}

/*Чтобы включить режим отладки Analytics на устройстве Android, выполните следующие команды:
  adb shell setprop debug.firebase.analytics.app com.nla.personalfitnesstrainer
  Это поведение сохраняется до тех пор, пока вы явно не отключите режим отладки, выполнив следующую команду:
  adb shell setprop debug.firebase.analytics.app .none  */

actual fun getFirebaseKMP(): FirebaseKMP = AndroidFirebaseKMP(AndroidApp.INSTANCE)