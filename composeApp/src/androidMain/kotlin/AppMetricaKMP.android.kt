import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig
import io.appmetrica.analytics.push.AppMetricaPush
import org.nla.phototovideoai.BuildConfig
import org.nla.phototovideoai.app.AndroidApp

class AndroidAppMetricaKMP : AppMetricaKMP {
    override fun initialize() {
        with(AndroidApp.INSTANCE) {
            val config = AppMetricaConfig
                .newConfigBuilder("717e9080-de19-4949-b9e0-1f2d0b0c0cb3")
                .withLocationTracking(true)
                .build()

            AppMetrica.activate(this, config)
            AppMetrica.enableActivityAutoTracking(this)
            AppMetricaPush.activate(this)
        }
    }

    override fun reportEvent(eventName: String) {
        if (!BuildConfig.DEBUG)
            AppMetrica.reportEvent(eventName)
    }

    override fun reportEvent(eventName: String, parameters: Map<String, Any>) {
        if (!BuildConfig.DEBUG)
            AppMetrica.reportEvent(eventName, parameters)
    }
}

actual fun getAppMetricaKMP(): AppMetricaKMP? = AndroidAppMetricaKMP()