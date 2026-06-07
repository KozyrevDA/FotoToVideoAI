interface AppMetricaKMP {
    fun initialize()
    fun reportEvent(eventName: String)
    fun reportEvent(eventName: String, parameters: Map<String, Any>)
}

expect fun getAppMetricaKMP(): AppMetricaKMP?