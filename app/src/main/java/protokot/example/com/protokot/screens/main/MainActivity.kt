package protokot.example.com.protokot.screens.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.widget.TextView
import android.widget.Toast
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBarDrawerToggle
import android.view.View
import protokot.example.com.protokot.R
import protokot.example.com.protokot.screens.main.booklist.BookListFragment
import protokot.example.com.protokot.screens.main.home.HomeFragment

/**
 * Activity handling main content
 */
class MainActivity : AppCompatActivity() {

    private var drawerLayout: DrawerLayout? = null
    private var toolbar: Toolbar? = null
    private var navigationView: NavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigation_view)

        setSupportActionBar(toolbar)
        initNavigationDrawer()
        val fragment = HomeFragment.newInstance()
        fragment.listener = View.OnClickListener { v -> changeFragment(BookListFragment.newInstance()) }
        changeFragment(fragment)
    }

    private fun initNavigationDrawer() {
        navigationView?.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            val frag = getSupportFragmentManager().findFragmentById(R.id.container)

            when (item.itemId) {
                R.id.home -> {
                    if (frag !is HomeFragment) {
                        changeFragment(HomeFragment.newInstance())
                    }
                    drawerLayout?.closeDrawers()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.settings -> {
                    Toast.makeText(applicationContext, "Settings", Toast.LENGTH_SHORT).show()
                    drawerLayout?.closeDrawers()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.books -> {
                    if (frag !is BookListFragment) {
                        changeFragment(BookListFragment.newInstance())
                    }
                    drawerLayout?.closeDrawers()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })

        val header = navigationView?.getHeaderView(0)
        val headerText = header?.findViewById<TextView>(R.id.header_text)
        headerText?.text = "Mon header"

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
