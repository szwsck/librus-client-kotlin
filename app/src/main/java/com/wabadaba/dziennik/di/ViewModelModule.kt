package com.wabadaba.dziennik.di

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.ui.MainViewModel
import com.wabadaba.dziennik.ui.announcements.AnnouncementsViewModel
import com.wabadaba.dziennik.ui.attendance.AttendancesViewModel
import com.wabadaba.dziennik.ui.events.EventsViewModel
import com.wabadaba.dziennik.ui.grades.GradesViewModel
import com.wabadaba.dziennik.ui.timetable.TimetableViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Provider
import javax.inject.Singleton


@Module
class ViewModelModule {

    @Provides
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun provideMainViewModel(appliaction: Application): ViewModel = MainViewModel(appliaction)

    @Provides
    @IntoMap
    @ViewModelKey(GradesViewModel::class)
    fun provideGradesViewModel(entityRepo: EntityRepository): ViewModel = GradesViewModel(entityRepo)

    @Provides
    @IntoMap
    @ViewModelKey(TimetableViewModel::class)
    fun provideTimetableViewModel(entityRepo: EntityRepository): ViewModel = TimetableViewModel(entityRepo)

    @Provides
    @IntoMap
    @ViewModelKey(AttendancesViewModel::class)
    fun provideAttendancesViewModel(entityRepo: EntityRepository): ViewModel = AttendancesViewModel(entityRepo)

    @Provides
    @IntoMap
    @ViewModelKey(EventsViewModel::class)
    fun provideEventsViewModel(entityRepo: EntityRepository): ViewModel = EventsViewModel(entityRepo)

    @Provides
    @IntoMap
    @ViewModelKey(AnnouncementsViewModel::class)
    fun provideAnnouncementsViewModel(entityRepo: EntityRepository) : ViewModel = AnnouncementsViewModel(entityRepo)

    @Provides
    @Singleton
    fun provideViewModelFactory(creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>) =
            ViewModelFactory(creators)
}