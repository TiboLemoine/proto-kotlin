package protokot.example.com.protokot.screens.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.gms.analytics.HitBuilders
import com.google.firebase.analytics.FirebaseAnalytics
import protokot.example.com.protokot.KotApp
import protokot.example.com.protokot.R
import protokot.example.com.protokot.data.SharedConstants
import protokot.example.com.protokot.screens.main.account.AccountFragment
import protokot.example.com.protokot.screens.main.booklist.BookListFragment
import protokot.example.com.protokot.screens.main.home.HomeFragment


/**
 * Activity handling main content
 */
class MainActivity : AppCompatActivity() {

    private var drawerLayout: DrawerLayout? = null
    private var toolbar: Toolbar? = null
    private var navigationView: NavigationView? = null

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigation_view)

        val shared = baseContext.getSharedPreferences(SharedConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        val firebase = FirebaseAnalytics.getInstance(this)
        firebase.setUserProperty("username", shared.getString(SharedConstants.EMAIL_KEY, ""))

        val application = application as KotApp
        application.getDefaultTracker().set("site", shared.getString(SharedConstants.EMAIL_KEY, ""))
        application.getDefaultTracker().set("site_session", shared.getString(SharedConstants.EMAIL_KEY, ""))


        setSupportActionBar(toolbar)
        initNavigationDrawer()
        val fragment = HomeFragment.newInstance()
        fragment.listener = View.OnClickListener { v ->
            val firebase = FirebaseAnalytics.getInstance(this);
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "BookListButton")
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BookListButton")
            bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "Navigation")
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button")
            firebase.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

            application.getDefaultTracker().send(HitBuilders.EventBuilder()
                    .setCategory("Navigation")
                    .setAction("Button")
                    .setLabel("BookListButton")
                    .setCustomDimension(1, shared.getString(SharedConstants.EMAIL_KEY, ""))
                    .build())

            changeFragment(BookListFragment.newInstance())
        }
        changeFragment(fragment)
    }

    private fun initNavigationDrawer() {
        val shared = baseContext.getSharedPreferences(SharedConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        navigationView?.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            val frag = getSupportFragmentManager().findFragmentById(R.id.container)

            val firebase = FirebaseAnalytics.getInstance(this);
            val application = application as KotApp

            when (item.itemId) {
                R.id.home -> {
                    if (frag !is HomeFragment) {
                        val bundle = Bundle()
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Home")
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Home")
                        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "Navigation")
                        firebase.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Menu")
                        Log.d("FA", "Sending firebase event")

                        application.getDefaultTracker().send(HitBuilders.EventBuilder()
                                .setCategory("Navigation")
                                .setAction("Menu")
                                .setLabel("Home")
                                .setCustomDimension(1, shared.getString(SharedConstants.EMAIL_KEY, ""))
                                .build())

                        val fragment = HomeFragment.newInstance()
                        fragment.listener = View.OnClickListener { v -> changeFragment(BookListFragment.newInstance()) }
                        changeFragment(fragment)
                    }
                    drawerLayout?.closeDrawers()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.settings -> {
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Settings")
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Settings")
                    bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "Navigation")
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Menu")
                    firebase.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
                    Log.d("FA", "Sending firebase event")

                    application.getDefaultTracker().send(HitBuilders.EventBuilder()
                            .setCategory("Navigation")
                            .setAction("Menu")
                            .setLabel("Settings")
                            .setCustomDimension(1, shared.getString(SharedConstants.EMAIL_KEY, ""))
                            .build())

                    changeFragment(AccountFragment.newInstance())
                    drawerLayout?.closeDrawers()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.books -> {
                    if (frag !is BookListFragment) {
                        val bundle = Bundle()
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "BookList")
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BookList")
                        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "Navigation")
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Menu")
                        firebase.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
                        Log.d("FA", "Sending firebase event")

                        application.getDefaultTracker().send(HitBuilders.EventBuilder()
                                .setCategory("Navigation")
                                .setAction("Menu")
                                .setLabel("BookList")
                                .setCustomDimension(1, shared.getString(SharedConstants.EMAIL_KEY, ""))
                                .build())


                        changeFragment(BookListFragment.newInstance())
                    }
                    drawerLayout?.closeDrawers()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })

        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)

        drawerLayout?.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    private fun changeFragment(fragment: Fragment?) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.commit()
        }
    }
}
