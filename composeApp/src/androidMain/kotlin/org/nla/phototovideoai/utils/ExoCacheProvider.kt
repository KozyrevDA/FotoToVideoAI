package org.nla.phototovideoai.utils

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@UnstableApi
object ExoCacheProvider {
    private val caches = mutableMapOf<String, SimpleCache>()

    fun getCache(context: Context, cacheName: String): SimpleCache {
        return caches[cacheName] ?: synchronized(this) {
            caches[cacheName] ?: SimpleCache(
                File(context.cacheDir, cacheName),
                LeastRecentlyUsedCacheEvictor(500L * 1024 * 1024), // 500MB
                StandaloneDatabaseProvider(context)
            ).also { caches[cacheName] = it }
        }
    }
}