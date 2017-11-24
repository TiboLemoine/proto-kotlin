package protokot.example.com.protokot.network

import protokot.example.com.protokot.data.DaySchedule
import protokot.example.com.protokot.data.LibraryBook
import retrofit2.http.GET
import rx.Observable

/**
 * Interface for library services
 */
interface ILibraryRetrofit {

    @GET("ws/schedules")
    fun getSchedules() : Observable<List<DaySchedule>>

    @GET("ws/books")
    fun getBooks() : Observable<List<LibraryBook>>
}