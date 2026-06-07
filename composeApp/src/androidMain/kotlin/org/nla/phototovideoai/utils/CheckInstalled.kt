package org.nla.phototovideoai.utils

import org.nla.phototovideoai.BuildConfig

object CheckInstalled {
    fun isInstalledFromRuStore(): Boolean {
        return if (BuildConfig.DEBUG) {
            true
        } else {
            BuildConfig.IS_RUSTORE
        }
    }
}