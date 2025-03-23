package be.viveselic.androidbackgoundservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.view.ContentInfoCompat.Flags

class MyService : Service() {

    private val mBinder = DownloadBinder()
    class DownloadBinder : Binder() {
        fun startDownload() {
            Log.d("MyService", "startDownload executed")
        }
        fun getProgress(): Int {
            Log.d("MyService", "getProgress executed")
            return 0
        }
    }

    override fun onBind(intent: Intent): IBinder {
        //Return the communication channel to the service.
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MyService", "onCreate executed")

        //add code for foreground service
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("my_service", "Foreground Service Notification", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, "my_service")
            .setContentTitle("This is content title")
            .setContentText("This is content text")
            .setSmallIcon(R.drawable.baseline_cloud_sync_24)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.vives_logo))
            .setContentIntent(pi)
            .build()
        startForeground(1, notification)

    }
    override fun onStartCommand(intent: Intent, flags: Int, startId:
    Int): Int {
        Log.d("MyService", "onStartCommand executed")
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyService", "onDestroy executed")
    }

}