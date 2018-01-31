package protokot.example.com.protokot

import android.app.Application
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker


/**
 * Created by Cardiweb on 21/12/2017.
 */
class KotApp : Application() {

    private lateinit var sAnalytics: GoogleAnalytics
    private var sTracker: Tracker? = null

    override fun onCreate() {
        super.onCreate()

        sAnalytics = GoogleAnalytics.getInstance(this)
    }

    /**
     * Gets the default [Tracker] for this [Application].
     * @return tracker
     */
    @Synchronized
    fun getDefaultTracker(): Tracker {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker)
        }

        return sTracker!!
    }
}