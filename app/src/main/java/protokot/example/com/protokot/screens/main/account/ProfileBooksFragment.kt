package protokot.example.com.protokot.screens.main.account

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.fragment_book_list.*
import protokot.example.com.protokot.R
import protokot.example.com.protokot.screens.base.AbstractFragment
import protokot.example.com.protokot.screens.main.booklist.BookListAdapter
import protokot.example.com.protokot.screens.main.booklist.BookListFragment
import protokot.example.com.protokot.screens.main.booklist.BookListListener
import protokot.example.com.protokot.screens.main.booklist.BookSummary
import java.util.ArrayList
import kotlinx.android.synthetic.main.fragment_user_books.*
/**
 * Created by Cardiweb on 24/01/2018.
 */
class ProfileBooksFragment : AbstractFragment(), BookListListener {

    override fun bookActionClicked(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun bookClicked(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContentView() = R.layout.fragment_user_books

    companion object {
        fun newInstance(): ProfileBooksFragment {
            return ProfileBooksFragment()
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userBookList.isNestedScrollingEnabled = false

        val adapter = BookListAdapter(ArrayList(), this)
        val list : ArrayList<BookSummary> = ArrayList()
        list.add(BookSummary("2", "Harry Potter and the Cursed Child", "Jack Thorne", "https://upload.wikimedia.org/wikipedia/en/f/fe/Harry_Potter_and_the_Cursed_Child_Special_Rehearsal_Edition_Book_Cover.jpg", true, "Retourner"))
        list.add(BookSummary("3", "A Tale of Two Cities", "Charles Dickens", "ttp://artsandculturereviews.files.wordpress.com/2014/07/top-10-best-seller-books-1-a-tale-of-two-cities-by-charles-dickens.jpg", true, "Retourner"))
        adapter.items = list
        adapter.notifyDataSetChanged()
        userBookList.layoutManager = LinearLayoutManager(context)
        userBookList.adapter = adapter
    }
}