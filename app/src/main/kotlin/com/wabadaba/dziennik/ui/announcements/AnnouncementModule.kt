package com.wabadaba.dziennik.ui.announcements

import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.base.Schedulers
import dagger.Module
import dagger.Provides

@Module
class AnnouncementModule {
    @Provides
    fun provideAnnouncementPresenter(schedulers: Schedulers, entityRepository: EntityRepository) =
            AnnouncementPresenter(schedulers, entityRepository)
}