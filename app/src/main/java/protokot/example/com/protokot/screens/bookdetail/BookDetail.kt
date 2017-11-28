package protokot.example.com.protokot.screens.bookdetail

/**
 * Object representing a book as showed in the detail screen
 */
class BookDetail(val title : String, val authors : String, val image : String?) {
    var borrowed : Boolean = false
    var summary : String = ""
    var parutionDate : String = ""
    var categories : List<String> = ArrayList()

}