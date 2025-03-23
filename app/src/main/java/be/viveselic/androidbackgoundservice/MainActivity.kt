package be.viveselic.androidbackgoundservice

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ContentInfoCompat.Flags
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    val updateText = 1

    val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
        // Updating UI here is fine
            when (msg.what) {
                updateText -> findViewById<TextView>(R.id.textView).text = "Nice to meet you"
            }
        }
    }

    lateinit var downloadBinder: MyService.DownloadBinder
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service:
        IBinder
        ) {
            downloadBinder = service as MyService.DownloadBinder
            downloadBinder.startDownload()
            downloadBinder.getProgress()
        }
        override fun onServiceDisconnected(name: ComponentName) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(POST_NOTIFICATIONS), 1)
            }
        }

        findViewById<Button>(R.id.changeTextButton).setOnClickListener {
            thread {
                val msg = Message()
                msg.what = updateText
                handler.sendMessage(msg) // send the Message object
            }
        }
        findViewById<Button>(R.id.startServiceButton).setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            startService(intent)
        }
        findViewById<Button>(R.id.stopServiceButton).setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            stopService(intent)
        }

        findViewById<Button>(R.id.bindButton).setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        findViewById<Button>(R.id.unbindButton).setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            unbindService(connection) // unbind Service
        }

        findViewById<Button>(R.id.intentServiceButton).setOnClickListener {
            MyIntentService.startActionFoo(this, "param1", "param2")
        }

    }


}