package com.wabadaba.dziennik.ui.announcements

import com.wabadaba.dziennik.base.BaseView

interface AnnouncementView : BaseView {
    fun showAnnouncements(announcementData: AnnouncementData)
}