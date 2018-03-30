package com.wabadaba.dziennik.ui.attendance

import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.base.Schedulers
import dagger.Module
import dagger.Provides

@Module
class AttendanceModule {
    @Provides
    fun providePresenter(schedulers: Schedulers, entityRepository: EntityRepository) =
            AttendancePresenter(schedulers, entityRepository)
}