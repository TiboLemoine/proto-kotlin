package protokot.example.com.protokot.screens.main.booklist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.view.View
import protokot.example.com.protokot.R
import protokot.example.com.protokot.screens.base.AbstractFragment
import kotlinx.android.synthetic.main.fragment_book_list.*
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.google.android.gms.analytics.HitBuilders
import com.google.firebase.analytics.FirebaseAnalytics
import protokot.example.com.protokot.KotApp
import protokot.example.com.protokot.data.LibraryBook
import protokot.example.com.protokot.data.SharedConstants
import protokot.example.com.protokot.network.ILibraryRetrofit
import protokot.example.com.protokot.network.LibraryService
import protokot.example.com.protokot.screens.bookdetail.BookDetailActivity
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Fragment handling book list screen
 */
class BookListFragment : AbstractFragment(), BookListListener, TabLayout.OnTabSelectedListener {

    /**
     * Selected filter
     */
    private var filterValue = LIST_FILTER

    /**
     * Adapter for schedule list
     */
    private lateinit var adapter: BookListAdapter

    /**
     * Book list subscription
     */
    private var subscription : Subscription? = null
    private var catSubscription : Subscription? = null

    companion object {
        const val LIST_FILTER = 0
        const val GRID_FILTER = 1

        fun newInstance(): BookListFragment {
            return BookListFragment()
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = BookListAdapter(ArrayList(), this)

        listButton.setOnClickListener { v ->
            filterValue = LIST_FILTER
            updateButtonFilter()
        }

        gridButton.setOnClickListener { v ->
            filterValue = GRID_FILTER
            updateButtonFilter()
        }

        categoryTab.addOnTabSelectedListener(this)

        swipeRefresh.setOnRefreshListener { getBookList() }

        updateButtonFilter()
        getCategories()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscription?.let { it.unsubscribe() }
        catSubscription?.let { it.unsubscribe() }
    }

    override fun getContentView() = R.layout.fragment_book_list

    /**
     * List interface methods
     */
    override fun bookClicked(position: Int) {
        val book = adapter.items[position]
        val shared = context.getSharedPreferences(SharedConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)

        val firebase = FirebaseAnalytics.getInstance(context);
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, book.title)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BookDetail")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "Navigation")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Cell")
        bundle.putString("author", book.authors)
        firebase.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        Log.d("FA","Sending firebase event")

        val application = activity.application as KotApp
        application.getDefaultTracker().send(HitBuilders.EventBuilder()
                .setCategory("Navigation")
                .setAction("Cell")
                .setLabel(book.title)
                .set("author", book.authors)
                .setCustomDimension(1, shared.getString(SharedConstants.EMAIL_KEY, ""))
                .build())

        startActivity(BookDetailActivity.newIntent(context, book.id))
    }

    override fun bookActionClicked(position: Int) {
        val book = adapter.items[position]
        val shared = context.getSharedPreferences(SharedConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)

        val firebase = FirebaseAnalytics.getInstance(context);
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, book.buttonTitle)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BookAction")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "Navigation")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button")
        firebase.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        Log.d("FA","Sending firebase event")

        val application = activity.application as KotApp
        application.getDefaultTracker().send(HitBuilders.EventBuilder()
                .setCategory("Navigation")
                .setAction("Button")
                .setLabel(book.buttonTitle)
                .setCustomDimension(1, shared.getString(SharedConstants.EMAIL_KEY, ""))
                .build())

        Toast.makeText(context, "Action button clicked", Toast.LENGTH_SHORT).show()
    }

    /**
     * Network methods
     */
    private fun getCategories() {
        swipeRefresh.isRefreshing = true

        val service = LibraryService("http://10.0.2.2:3000/", ILibraryRetrofit::class.java)
        catSubscription = service.serviceInstance.getCategories()
                .subscribeOn(Schedulers.io())
                .map { list ->
                    val completeList = ArrayList(list)
                    completeList.add(0, getString(R.string.book_cat_all))

                    return@map completeList
                }
                .flatMap { list -> Observable.from(list) }
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .subscribe({ cat -> categoryTab.addTab(categoryTab.newTab().setText(cat)) }, { e -> e.printStackTrace() }, {getBookList()})
    }

    private fun getBookList() {
        swipeRefresh.isRefreshing = true

        subscription?.let { it.unsubscribe() }

        val service = LibraryService("http://10.0.2.2:3000/", ILibraryRetrofit::class.java)
        subscription = service.serviceInstance.getBooks()
                .subscribeOn(Schedulers.io())
                .delay(1, TimeUnit.SECONDS)
                .map { l -> l.sortedWith(compareBy({ it.title })) }
                .flatMap { l -> Observable.from(l) }
                .filter { book -> book.title != null }
                .map { book -> convertNetworkObjectToPojo(book) }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate { swipeRefresh.isRefreshing = false }
                .subscribe({ list ->
                    adapter.items = list
                    adapter.notifyDataSetChanged()
                }, { e -> e.printStackTrace() })
    }

    private fun getBookListForCat(cat : String) {
        swipeRefresh.isRefreshing = true

        adapter.items = ArrayList()
        adapter.notifyDataSetChanged()

        subscription?.let { it.unsubscribe() }

        val service = LibraryService("http://10.0.2.2:3000/", ILibraryRetrofit::class.java)
        subscription = service.serviceInstance.getBooksForCat(cat)
                .subscribeOn(Schedulers.io())
                .delay(1, TimeUnit.SECONDS)
                .map { l -> l.sortedWith(compareBy({ it.title })) }
                .flatMap { l -> Observable.from(l) }
                .filter { book -> book.title != null }
                .map { book -> convertNetworkObjectToPojo(book) }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate { swipeRefresh.isRefreshing = false }
                .subscribe({ list ->
                    adapter.items = list
                    adapter.notifyDataSetChanged()
                }, { e -> e.printStackTrace() })
    }

    /**
     * TabLayout callback
     */
    override fun onTabReselected(tab: TabLayout.Tab?) {
        //Nothing to do here
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        //Nothing to do here
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        val shared = context.getSharedPreferences(SharedConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        val firebase = FirebaseAnalytics.getInstance(context);
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, tab?.text.toString())
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "TabSelection")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "Navigation")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Tab")
        firebase.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        Log.d("FA","Sending firebase event")

        val application = activity.application as KotApp
        application.getDefaultTracker().send(HitBuilders.EventBuilder()
                .setCategory("Navigation")
                .setAction("Tab")
                .setLabel(tab?.text.toString())
                .setCustomDimension(1, shared.getString(SharedConstants.EMAIL_KEY, ""))
                .build())

        if (tab?.text.toString() == (getString(R.string.book_cat_all))) {
            getBookList()
        } else {
            getBookListForCat(tab?.text.toString())
        }
    }

    /**
     * Other activity methods
     */
    private fun updateButtonFilter() {
        val shared = context.getSharedPreferences(SharedConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        val firebase = FirebaseAnalytics.getInstance(context);
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, if (filterValue == LIST_FILTER) "List" else "Grid")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "FilterSelection")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "Navigation")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button")
        firebase.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        Log.d("FA","Sending firebase event")

        val application = activity.application as KotApp
        application.getDefaultTracker().send(HitBuilders.EventBuilder()
                .setCategory("Navigation")
                .setAction("Button")
                .setLabel(if (filterValue == LIST_FILTER) "List" else "Grid")
                .setCustomDimension(1, shared.getString(SharedConstants.EMAIL_KEY, ""))
                .build())

        if (filterValue == LIST_FILTER) {
            adapter?.isListView = true
            list.layoutManager = LinearLayoutManager(context)
            list.adapter = adapter

            listButton.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent))
            gridButton.setColorFilter(ContextCompat.getColor(context, android.R.color.black))
        } else {
            adapter?.isListView = false
            list.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            list.adapter = adapter

            gridButton.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent))
            listButton.setColorFilter(ContextCompat.getColor(context, android.R.color.black))
        }
    }

    private fun convertNetworkObjectToPojo(book: LibraryBook): BookSummary {
        val rand = Random()
        val isBorrowed : Boolean = rand.nextBoolean()
        return BookSummary(book.id, book.title!!, book.author ?: getString(R.string.book_author_unknown), book.imagePath, isBorrowed, if (isBorrowed) getString(R.string.book_return) else getString(R.string.book_borrow))
    }
}