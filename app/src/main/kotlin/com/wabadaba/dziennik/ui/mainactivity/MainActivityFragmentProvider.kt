package com.wabadaba.dziennik.ui.mainactivity

import com.wabadaba.dziennik.ui.announcements.AnnouncementModule
import com.wabadaba.dziennik.ui.announcements.AnnouncementsFragment
import com.wabadaba.dziennik.ui.attendance.AttendanceModule
import com.wabadaba.dziennik.ui.attendance.AttendancesFragment
import com.wabadaba.dziennik.ui.events.EventsFragment
import com.wabadaba.dziennik.ui.events.EventsFragmentModule
import com.wabadaba.dziennik.ui.grades.GradeFragmentModule
import com.wabadaba.dziennik.ui.grades.GradesFragment
import com.wabadaba.dziennik.ui.timetable.TimetableFragment
import com.wabadaba.dziennik.ui.timetable.TimetableModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityFragmentProvider {
    @ContributesAndroidInjector(modules = [GradeFragmentModule::class])
    abstract fun bindGradesFragment() : GradesFragment

    @ContributesAndroidInjector(modules = [EventsFragmentModule::class])
    abstract fun bindEventsFragment() : EventsFragment

    @ContributesAndroidInjector(modules = [AttendanceModule::class])
    abstract fun bindAttendanceFragment() : AttendancesFragment

    @ContributesAndroidInjector(modules = [AnnouncementModule::class])
    abstract fun bindAnnouncementsFragment() : AnnouncementsFragment

    @ContributesAndroidInjector(modules = [TimetableModule::class])
    abstract fun bindTimetableFragment() : TimetableFragment
}
