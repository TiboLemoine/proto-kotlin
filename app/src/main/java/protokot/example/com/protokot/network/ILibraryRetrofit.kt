package protokot.example.com.protokot.network

import protokot.example.com.protokot.data.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import rx.Observable

/**
 * Interface for library services
 */
interface ILibraryRetrofit {

    @POST("ws/user/login")
    fun logUserIn(@Body request : LogInRequest) : Observable<LogInResponse>

    @POST("ws/user")
    fun createAccount(@Body request : CreateUserRequest) : Observable<LogInResponse>

    @GET("ws/schedules")
    fun getSchedules() : Observable<List<DaySchedule>>

    @GET("ws/books/categories")
    fun getCategories() : Observable<List<String>>

    @GET("ws/books")
    fun getBooks() : Observable<List<LibraryBook>>

    @GET("ws/book/category/{categoryId}/")
    fun getBooksForCat(@Path("categoryId") categoryId : String) : Observable<List<LibraryBook>>

    @GET("ws/book/{bookId}/")
    fun getBookFromId(@Path("bookId") bookId : String) : Observable<LibraryBook>
}