package com.wabadaba.dziennik.ui.events

import com.wabadaba.dziennik.base.BaseView
import com.wabadaba.dziennik.vo.Event

interface EventsFragmentView : BaseView {
    fun showEventData(event : EventData)
}