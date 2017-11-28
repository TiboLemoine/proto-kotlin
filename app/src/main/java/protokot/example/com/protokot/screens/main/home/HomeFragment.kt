package protokot.example.com.protokot.screens.main.home

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import protokot.example.com.protokot.screens.base.AbstractFragment
import protokot.example.com.protokot.R
import protokot.example.com.protokot.data.DaySchedule
import protokot.example.com.protokot.network.ILibraryRetrofit
import protokot.example.com.protokot.network.LibraryService
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlinx.android.synthetic.main.fragment_home.*
import rx.Subscription

/**
 * Fragment handling home screen
 */
class HomeFragment : AbstractFragment() {

    private val hf: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    /**
     * Adapter for schedule list
     */
    private var adapter: ScheduleAdapter? = null

    /**
     * Listener for button
     */
    var listener: View.OnClickListener? = null

    /**
     * Book list subscription
     */
    private var subscription : Subscription? = null

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ScheduleAdapter(ArrayList())
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(context)

        if (listener != null) booksButton.setOnClickListener(listener)

        showLoader()
        val service = LibraryService("http://10.0.2.2:3000/", ILibraryRetrofit::class.java)
        subscription = service.serviceInstance.getSchedules()
                .subscribeOn(Schedulers.io())
                .delay(1, TimeUnit.SECONDS)
                .map { l -> l.sortedWith(compareBy({ it.dayNumber })) }
                .flatMap { l -> Observable.from(l) }
                .filter { day -> day.title != null }
                .map { day -> convertNetworkObjectToPojo(day) }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { _ -> hideLoader(true) }
                .subscribe({ day -> adapter?.addSchedule(day) }, { e -> e.printStackTrace() }, { hideLoader(false) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscription?.let { subscription!!.unsubscribe() }
    }

    override fun getContentView() = R.layout.fragment_home

    /**
     * Convert a network DaySchedule object into a Schedule object to give to the list
     */
    private fun convertNetworkObjectToPojo(daySchedule: DaySchedule): Schedule {
        var scheduleString = getString(R.string.home_schedule_unknown)
        if (daySchedule.openingTime != null && daySchedule.closingTime != null) {
            val openingTime = Calendar.getInstance()
            openingTime.set(Calendar.HOUR_OF_DAY, daySchedule.openingTime.hour)
            openingTime.set(Calendar.MINUTE, daySchedule.openingTime.minute)
            val closingTime = Calendar.getInstance()
            closingTime.set(Calendar.HOUR_OF_DAY, daySchedule.closingTime.hour)
            closingTime.set(Calendar.MINUTE, daySchedule.closingTime.minute)

            scheduleString = getString(R.string.home_schedule_format, hf.format(openingTime.time), hf.format(closingTime.time))
        }

        return Schedule(daySchedule.title!!, scheduleString)
    }

    /**
     * Hide the content and show the loader
     */
    private fun showLoader() {
        contentLayout.visibility = View.GONE
        errorText.visibility = View.GONE
        loaderLayout.visibility = View.VISIBLE
    }

    /**
     * Hide the loader and show the content if there is no error, and the error otherwise
     */
    private fun hideLoader(withError: Boolean) {
        loaderLayout.visibility = View.GONE
        if (withError) {
            errorText.visibility = View.VISIBLE
        } else {
            contentLayout.visibility = View.VISIBLE
        }
    }
}