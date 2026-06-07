package utils.device.uid

import data.prefs.SharedPrefs
import getPlatformDeviceUid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DeviceUid(sharedPrefs: SharedPrefs) {
    private val _uid = MutableStateFlow(sharedPrefs.getDeviceUid())
    val uid = _uid.asStateFlow()

    init {
        if (sharedPrefs.getDeviceUid() == null) {
            getPlatformDeviceUid().uid.let {
                sharedPrefs.putDeviceUid(it)
                _uid.value = it
            }
        }
    }
}