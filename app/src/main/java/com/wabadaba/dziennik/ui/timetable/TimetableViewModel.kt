package com.wabadaba.dziennik.ui.timetable

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.vo.Lesson
import io.reactivex.android.schedulers.AndroidSchedulers

class TimetableViewModel(entityRepo: EntityRepository) : ViewModel() {

    val lessons = MutableLiveData<List<Lesson>>()

    init {
        entityRepo.lessons
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { lessons.value = it }
    }
}