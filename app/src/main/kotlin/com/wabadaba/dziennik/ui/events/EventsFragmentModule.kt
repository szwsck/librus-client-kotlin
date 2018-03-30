package com.wabadaba.dziennik.ui.events

import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.base.Schedulers
import dagger.Module
import dagger.Provides

@Module
class EventsFragmentModule {
    @Provides
    fun provideEventsFragmentPresenter(schedulers: Schedulers, entityRepository: EntityRepository) = EventsFragmentPresenter(schedulers, entityRepository)
}