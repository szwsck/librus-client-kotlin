package com.wabadaba.dziennik.ui.attendance

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.vo.Attendance
import io.reactivex.android.schedulers.AndroidSchedulers

class AttendancesViewModel(entityRepo: EntityRepository) : ViewModel() {

    val attendances = MutableLiveData<List<Attendance>>()

    init {
        entityRepo.attendances
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    attendances.value = it
                }
    }
}