package de.tum.`in`.tumcampusapp.component.ui.cafeteria

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.details.CafeteriaMenusAdapter
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.details.OpenHoursHelper
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.model.CafeteriaWithMenus
import de.tum.`in`.tumcampusapp.component.ui.overview.CardInteractionListener
import de.tum.`in`.tumcampusapp.component.ui.overview.card.CardViewHolder
import org.joda.time.format.DateTimeFormat
import java.util.*

class CafeteriaMenuViewHolder(
    itemView: View,
    interactionListener: CardInteractionListener
) : CardViewHolder(itemView, interactionListener) {

    private lateinit var adapter: CafeteriaMenusAdapter
    private val cafeteriaNameTextView = itemView.findViewById<TextView>(R.id.cafeteriaNameTextView)
    private val menuDateTextView = itemView.findViewById<TextView>(R.id.menuDateTextView)
    private val openingHoursTextView = itemView.findViewById<TextView>(R.id.openingHoursTextView)
    private val menusRecyclerView = itemView.findViewById<RecyclerView>(R.id.menusRecyclerView)

    fun bind(cafeteria: CafeteriaWithMenus) = with(itemView) {
        cafeteriaNameTextView.text = cafeteria.name
        menuDateTextView.text = DateTimeFormat.mediumDate().print(cafeteria.nextMenuDate)

        val openHoursHelper = OpenHoursHelper(context)
        var openingHours = openHoursHelper.getHoursByIdAsString(cafeteria.id, cafeteria.nextMenuDate)
        if (openingHours.isEmpty()) {
            openingHoursTextView.visibility = View.GONE
        } else {
            // update textview every 10s (this interval is chosen to make our clock closer to realtime)
            var timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    openingHours = openHoursHelper.getHoursByIdAsString(cafeteria.id, cafeteria.nextMenuDate)
                    openingHoursTextView.visibility = View.VISIBLE
                    openingHoursTextView.text = openingHours
                }
            }, 0, 10000)
        }

        if (this@CafeteriaMenuViewHolder::adapter.isInitialized.not()) {
            menusRecyclerView.layoutManager = LinearLayoutManager(context)
            menusRecyclerView.itemAnimator = DefaultItemAnimator()

            adapter = CafeteriaMenusAdapter(context, false) { performClick() }
            menusRecyclerView.adapter = adapter
        }

        adapter.update(cafeteria.menus)
    }
}
