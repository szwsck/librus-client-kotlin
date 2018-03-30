package com.wabadaba.dziennik.ui.timetable

import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.base.Schedulers
import dagger.Module
import dagger.Provides

@Module
class TimetableModule {
    @Provides
    fun providePresenter(schedulers: Schedulers, entityRepository: EntityRepository) = TimetablePresenter(schedulers, entityRepository)
}