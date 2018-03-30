package com.wabadaba.dziennik.ui.grades

import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.base.Schedulers
import dagger.Module
import dagger.Provides

@Module
class GradeFragmentModule {
    @Provides
    fun provideGradeFragmentPresenter(schedulers: Schedulers, entityRepository: EntityRepository) = GradeFragmentPresenter(schedulers, entityRepository)
}