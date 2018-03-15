package com.wabadaba.dziennik.ui.mainactivity

import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.api.UserRepository
import com.wabadaba.dziennik.base.Schedulers
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {
    // You could use constructor injection, but I prefer this way
    @Provides
    fun providePresenter(schedulers: Schedulers, userRepository: UserRepository, entityRepository : EntityRepository)  = MainActivityPresenter(schedulers, userRepository, entityRepository)
}