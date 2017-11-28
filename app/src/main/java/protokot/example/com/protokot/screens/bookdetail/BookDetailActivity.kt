package protokot.example.com.protokot.screens.bookdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import protokot.example.com.protokot.R
import kotlinx.android.synthetic.main.activity_book_detail.*
import protokot.example.com.protokot.data.LibraryBook
import protokot.example.com.protokot.network.ILibraryRetrofit
import protokot.example.com.protokot.network.LibraryService
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

/**
 * Activity handling book detail screen
 */
class BookDetailActivity : AppCompatActivity() {

    /**
     * Book list subscription
     */
    private var subscription : Subscription? = null

    companion object {
        const val BOOK_ID_KEY = "book_id_key"

        fun newIntent(context: Context, bookId: String): Intent {
            val intent = Intent(context, BookDetailActivity::class.java)
            intent.putExtra(BOOK_ID_KEY, bookId)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        val bookId = intent?.getStringExtra(BOOK_ID_KEY)
        if (bookId != null) {
            getBook(bookId)
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription?.let { subscription!!.unsubscribe() }
    }

    private fun getBook(bookId: String) {
        val service = LibraryService("http://10.0.2.2:3000/", ILibraryRetrofit::class.java)
        subscription = service.serviceInstance.getBookFromId(bookId)
                .subscribeOn(Schedulers.io())
                .map { book -> convertNetworkObjectToPojo(book) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ book ->
                    collapsingToolbar.title = book.title
                    book.image.let {
                        Glide.with(baseContext)
                                .load(book.image)
                                .into(headerImage)
                    }
                    description.text = book.summary
                    publishDate.text = book.parutionDate
                    authors.text = book.authors
                    categories.text = getStringFromList(book.categories)
                }, { e -> e.printStackTrace() })
    }

    private fun convertNetworkObjectToPojo(book: LibraryBook): BookDetail {
        val bookDetail = BookDetail(book.title!!, book.author ?: getString(R.string.book_author_unknown), book.imagePath)
        bookDetail.borrowed = false
        bookDetail.summary = book.description ?: "Aucun résumé disponible"
        bookDetail.categories = book.categories ?: ArrayList()

        book.publicationDate.let {
            val serverFormater = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val appFormater = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = serverFormater.parse(it)
            bookDetail.parutionDate = appFormater.format(date)
        }

        return bookDetail
    }

    private fun getStringFromList(list: List<String>): String {
        val builder = StringBuilder()
        list.forEach { s ->
            builder.append(s)
            if (list.indexOf(s) != list.size - 1) {
                builder.append("\n")
            }
        }

        return builder.toString()
    }
}