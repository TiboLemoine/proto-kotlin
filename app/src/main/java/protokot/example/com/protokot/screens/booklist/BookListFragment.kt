package protokot.example.com.protokot.screens.booklist

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import protokot.example.com.protokot.R
import protokot.example.com.protokot.screens.base.AbstractFragment
import kotlinx.android.synthetic.main.fragment_book_list.*
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import protokot.example.com.protokot.data.LibraryBook
import protokot.example.com.protokot.network.ILibraryRetrofit
import protokot.example.com.protokot.network.LibraryService
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Fragment handling book list screen
 */
class BookListFragment : AbstractFragment() {

    /**
     * Selected filter
     */
    private var filterValue = LIST_FILTER

    /**
     * Adapter for schedule list
     */
    private var adapter: BookListAdapter? = null

    companion object {
        const val LIST_FILTER = 0
        const val GRID_FILTER = 1

        fun newInstance(): BookListFragment {
            return BookListFragment()
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = BookListAdapter(ArrayList())

        listButton.setOnClickListener { v ->
            filterValue = LIST_FILTER
            updateButtonFilter()
        }

        gridButton.setOnClickListener { v ->
            filterValue = GRID_FILTER
            updateButtonFilter()
        }

        swipeRefresh.setOnRefreshListener { getBookList() }

        updateButtonFilter()
        getBookList()
    }

    override fun getContentView() = R.layout.fragment_book_list

    private fun getBookList() {
        swipeRefresh.isRefreshing = true

        val service = LibraryService("http://10.0.2.2:3000/", ILibraryRetrofit::class.java)
        service.serviceInstance.getBooks()
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
                    adapter?.items = list
                    adapter?.notifyDataSetChanged()
                }, { e -> e.printStackTrace() })
    }

    private fun updateButtonFilter() {
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
        return BookSummary(book.title!!, book.author ?: getString(R.string.book_author_unknown), book.imagePath, false, getString(R.string.book_borrow))
    }
}