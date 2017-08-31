package com.wabadaba.dziennik.ui

import android.support.v4.app.Fragment
import com.chibatching.kotpref.KotprefModel
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.ui.attendance.AttendancesFragment
import com.wabadaba.dziennik.ui.grades.GradesFragment
import com.wabadaba.dziennik.ui.timetable.TimetableFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlin.reflect.KClass

class FragmentRepository {

    object Prefs : KotprefModel() {
        var defaultFragmentId by intPref(R.id.fragment_timetable)
    }

    private val currentSubject: BehaviorSubject<FragmentInfo> = BehaviorSubject.create()
    val current: Observable<FragmentInfo> = currentSubject

    val fragmentInfoList = listOf(
            FragmentInfo(R.id.fragment_timetable, TimetableFragment::class, R.string.timetable, R.drawable.ic_event_black_24dp),
            FragmentInfo(R.id.fragment_grades, GradesFragment::class, R.string.grades, R.drawable.ic_assignment_black_24dp),
            FragmentInfo(R.id.fragment_attendances, AttendancesFragment::class, R.string.attendances, R.drawable.ic_person_outline_black_24dp))

    init {
        currentSubject.onNext(getFragmentInfoForId(Prefs.defaultFragmentId))
    }

    private fun getFragmentInfoForId(id: Int) = fragmentInfoList.singleOrNull { it.id == id }
            ?: throw IllegalArgumentException("0 or multiple fragmentInfoList with id $id")

    fun setDefaultFragment(id: Int) {
        Prefs.defaultFragmentId = id
    }

    fun setCurrentFragment(id: Int) = currentSubject.onNext(getFragmentInfoForId(id))

}

data class FragmentInfo(
        val id: Int,
        val kClass: KClass<out Fragment>,
        val title: Int,
        val icon: Int
)