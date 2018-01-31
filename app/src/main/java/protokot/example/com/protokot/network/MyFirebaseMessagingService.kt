package protokot.example.com.protokot.network

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import protokot.example.com.protokot.R
import protokot.example.com.protokot.screens.splash.SplashActivity
import java.util.*

/**
 * Created by Cardiweb on 19/12/2017.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage?) {
        Log.d("FA", "Notification received")

        if (message?.notification != null) {
            val notif = NotificationCompat.Builder(this, "")
            notif.setSmallIcon(R.drawable.ic_account)
            notif.mContentTitle = message.notification.title
            notif.mContentText = message.notification.body
            notif.setContentIntent(PendingIntent.getActivity(this, 0, Intent(this, SplashActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT))

            val mngr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mngr.notify(Calendar.getInstance().timeInMillis.toInt(), notif.build())
        }
    }
}