import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import org.nla.phototovideoai.R
import org.nla.phototovideoai.app.AndroidApp
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

actual fun saveVideoToGallery(name: String, videoBytes: ByteArray) {
    val context = AndroidApp.INSTANCE
    val resolver = context.contentResolver
    val newHash = videoBytes.md5()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (context.checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO)
            != PackageManager.PERMISSION_GRANTED
        ) return
    } else {
        if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) return
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val existingUri = resolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Video.Media._ID),
            "${MediaStore.Video.Media.DISPLAY_NAME} = ? AND ${MediaStore.Video.Media.RELATIVE_PATH} = ?",
            arrayOf(name, "Movies/live photos/"),
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
            } else null
        }

        if (existingUri != null) {
            resolver.openInputStream(existingUri)?.use { stream ->
                val existingBytes = stream.readBytes()
                if (existingBytes.md5() == newHash) return
            }

            resolver.openOutputStream(existingUri, "wt")?.use { out ->
                out.write(videoBytes)
            }
            return
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, name)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(
                MediaStore.Video.Media.RELATIVE_PATH,
                "Movies/live photos/"
            )
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        val insertUri =
            resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
        insertUri?.let { uri ->
            resolver.openOutputStream(uri, "w")?.use { out ->
                out.write(videoBytes)
            }

            contentValues.clear()
            contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }
    } else {
        val moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val appDir = File(moviesDir, context.getString(R.string.app_name)).apply { mkdirs() }
        val file = File(appDir, name)

        if (file.exists()) {
            val existingBytes = file.readBytes()
            if (existingBytes.md5() == newHash) return
        }

        FileOutputStream(file).use { out ->
            out.write(videoBytes)
        }

        ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, name)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.DATA, file.absolutePath)
        }.let { resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, it) }
    }
}

actual fun getVideos(): Map<String, String> {
    val context = AndroidApp.INSTANCE

    fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    if (!hasPermission()) {
        return emptyMap()
    }

    val videosDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
        context.getString(R.string.app_name)
    )

    if (videosDir.exists() && videosDir.isDirectory) {
        val files = videosDir.listFiles()
            ?.filter { it.isFile && it.extension.lowercase() in listOf("mp4", "mov", "mkv") }
            ?: emptyList()

        files.forEach { file ->
            MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null, null)
        }
    }

    fun getAllVideos(context: Context): List<Pair<String, Uri>> {
        val resolver = context.contentResolver
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val projection: Array<String>
        val selection: String?
        val selectionArgs: Array<String>?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.RELATIVE_PATH
            )
            selection = "${MediaStore.Video.Media.RELATIVE_PATH} = ?"
            selectionArgs = arrayOf("Movies/live photos/")
        } else {
            projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA
            )
            selection = "${MediaStore.Video.Media.DATA} LIKE ?"
            selectionArgs = arrayOf("%/Movies/live photos/%")
        }

        val cursor = resolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Video.Media.DISPLAY_NAME} DESC"
        ) ?: return emptyList()

        val list = mutableListOf<Pair<String, Uri>>()
        val seenUris = mutableSetOf<String>()

        cursor.use { c ->
            val idColumn = c.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = c.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)

            while (c.moveToNext()) {
                val id = c.getLong(idColumn)
                val name = c.getString(nameColumn)

                val contentUri = Uri.withAppendedPath(uri, id.toString())

                if (seenUris.add(contentUri.toString())) {
                    list.add(name to contentUri)
                }
            }
        }

        return list
    }

    val uris = getAllVideos(context)
    val map = mutableMapOf<String, String>()
    for ((name, uri) in uris) {
        map[name] = uri.toString()
    }

    return map
}

actual fun getVideoUriByName(name: String): String? {
    val context = AndroidApp.INSTANCE
    val resolver = context.contentResolver

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (context.checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO)
            != PackageManager.PERMISSION_GRANTED
        ) return null
    } else {
        if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) return null
    }

    val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME)
    val selection: String
    val selectionArgs: Array<String>

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        selection =
            "${MediaStore.Video.Media.DISPLAY_NAME} = ? AND ${MediaStore.Video.Media.RELATIVE_PATH} = ?"
        selectionArgs = arrayOf("$name.mp4", "Movies/live photos/")
    } else {
        selection = "${MediaStore.Video.Media.DISPLAY_NAME} = ?"
        selectionArgs = arrayOf("$name.mp4")
    }

    resolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
            return ContentUris.withAppendedId(uri, id).toString()
        }
    }
    return null
}

private fun ByteArray.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return md.digest(this).joinToString("") { "%02x".format(it) }
}