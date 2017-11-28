package protokot.example.com.protokot.screens.main.booklist

/**
 * Interface contract for book list
 */
interface BookListListener {
    
    fun bookClicked(position : Int)

    fun bookActionClicked(position: Int)
}