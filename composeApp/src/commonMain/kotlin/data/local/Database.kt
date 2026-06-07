package data.local

import DatabaseDriverFactory
import data.local.entities.VideoEntity
import org.nla.phototovideoai.AppDatabase

class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val videosQueries = database.videosQueries

    fun getAllVideos(): List<VideoEntity> {
        return videosQueries.selectAll(mapper = ::mapVideoEntity).executeAsList()
    }

    fun getVideoById(id: String): VideoEntity? {
        return videosQueries.selectById(
            id = id,
            mapper = ::mapVideoEntity
        ).executeAsOneOrNull()
    }


    fun insertOrUpdateVideo(video: VideoEntity) {
        videosQueries.insertOrReplace(
            id = video.id,
            created_at = video.createdAt
        )
    }

    fun deleteVideoById(id: String) {
        videosQueries.deleteById(id)
    }

    private fun mapVideoEntity(
        id: String,
        createdAt: String,
    ): VideoEntity {
        return VideoEntity(
            id = id,
            createdAt = createdAt
        )
    }
}