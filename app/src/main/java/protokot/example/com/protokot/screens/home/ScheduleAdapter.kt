package protokot.example.com.protokot.screens.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import protokot.example.com.protokot.R

/**
 * Adapter class for library schedule list
 */
class ScheduleAdapter(private var items : ArrayList<Schedule>) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = ScheduleViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.cell_schedule, parent, false))

    override fun onBindViewHolder(holder: ScheduleViewHolder?, position: Int) {
            holder?.bind(items[position])
    }

    override fun getItemCount() = items.size

    public fun addSchedule(schedule: Schedule) {
        if (items == null) {
            items = ArrayList()
        }

        items.add(schedule)
    }

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(schedule : Schedule) {
            itemView.findViewById<TextView>(R.id.schedule_day).text = schedule.day
            itemView.findViewById<TextView>(R.id.schedule_value).text = schedule.schedule
        }
    }
}