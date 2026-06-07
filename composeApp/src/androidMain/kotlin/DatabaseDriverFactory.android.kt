import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.nla.phototovideoai.AppDatabase
import org.nla.phototovideoai.app.AndroidApp

class AndroidDatabaseDriverFactory(private val context: Context) : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(AppDatabase.Schema, context, "AppDatabase.db")
    }
}

actual fun getDatabaseDriverFactory(): DatabaseDriverFactory =
    AndroidDatabaseDriverFactory(AndroidApp.INSTANCE.applicationContext)