package protokot.example.com.protokot.screens.main.booklist

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import protokot.example.com.protokot.R

/**
 * Adapter class for book list
 */
class BookListAdapter(var items : List<BookSummary>) : RecyclerView.Adapter<BookListAdapter.BookViewHolder>() {

    companion object {
        const val TYPE_BORROW = 0
        const val TYPE_RETURN = 1
    }

    var isListView = true

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BookViewHolder {
        return if (isListView) {
            BookListAdapter.BookViewHolder(LayoutInflater.from(ContextThemeWrapper(parent?.context, if (viewType == TYPE_BORROW) R.style.PositiveButton else R.style.NegativeButton)).inflate(R.layout.cell_book_list, parent, false))
        } else {
            BookListAdapter.BookViewHolder(LayoutInflater.from(ContextThemeWrapper(parent?.context, if (viewType == TYPE_BORROW) R.style.PositiveButton else R.style.NegativeButton)).inflate(R.layout.cell_book_grid, parent, false))
        }
    }

    override fun onBindViewHolder(holder: BookListAdapter.BookViewHolder?, position: Int) {
        holder?.bind(items[position])
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return if (items[position].borrowed) TYPE_RETURN else TYPE_BORROW
    }

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(book : BookSummary) {
            itemView.findViewById<TextView>(R.id.book_title)?.text = book.title
            itemView.findViewById<TextView>(R.id.book_author)?.text = book.authors
            if (book.image != null) {
                val image = itemView.findViewById<ImageView>(R.id.book_image)

                Glide.with(image.context)
                        .load(book.image)
                        .placeholder(ContextCompat.getDrawable(image.context, R.drawable.book_placeholder))
                        .into(image)
            }

            val button : Button ? = itemView.findViewById<Button>(R.id.action_button)
            button?.text = book.buttonTitle
        }
    }
}