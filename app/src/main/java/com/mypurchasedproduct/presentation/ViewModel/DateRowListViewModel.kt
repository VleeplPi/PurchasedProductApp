package com.mypurchasedproduct.presentation.ViewModel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypurchasedproduct.presentation.item.DayItem
import com.mypurchasedproduct.presentation.state.DateBoxUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.Instant
import org.joda.time.format.DateTimeFormat
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class DateRowListViewModel @Inject constructor() : ViewModel() {

    private val TAG = this.javaClass.simpleName

    private val now: DateTime = Instant.now().toDateTime()


    private val _state = MutableStateFlow(
        DateBoxUIState(
            false,
            DayItem(
                now.dayOfWeek().asShortText,
                now.dayOfMonth,
                now.monthOfYear,
                now.year,
                now.monthOfYear().asShortText
            )
        )
    )
    val state = _state.asStateFlow()

    private val _listDates = MutableStateFlow<List<DayItem>>(listOf())
    val listDates =_listDates.asStateFlow()
    init{
        // TODO: MOVE TO CalendarListViewModel
        viewModelScope.launch{
            Log.wtf(TAG, "INIT")
            _state.update { state ->
                Log.d(TAG, "LOADING START")
                state.copy(isLoading = true)
            }
            val maximumDayInMonth = now.dayOfMonth().withMaximumValue().dayOfMonth
            val listDays:MutableList<DayItem> = mutableListOf()
            for(i in 1..maximumDayInMonth){
                val day: DateTime = now.dayOfMonth().setCopy(i)
                listDays.add(i-1,DayItem(dayWeekName = day.dayOfWeek().asShortText, dayOfMonth = day.dayOfMonth, month=day.monthOfYear,year=day.year, day.monthOfYear().asShortText) )

            }
            _listDates.update{ listDates ->
                listDates.plus(listDays)
            }
            _state.update { state ->
                Log.d(TAG, "LOADING END")
                state.copy(isLoading = false)
            }

        }
    }

    fun createListDaysMonth(dayItem: DayItem){
        val month = if (dayItem.month / 10 > 0) dayItem.month.toString() else "0${dayItem.month}"
        val datetime = DateTime.parse("${dayItem.year}-${month}")



        viewModelScope.launch{
            Log.wtf(TAG, "INIT")
            _state.update { state ->
                Log.d(TAG, "LOADING START")
                state.copy(isLoading = true)
            }

            val maximumDayInMonth = datetime.dayOfMonth().withMaximumValue().dayOfMonth
            val listDays:MutableList<DayItem> = mutableListOf()
            for(i in 1..maximumDayInMonth){
                val day: DateTime = datetime.dayOfMonth().setCopy(i)
                listDays.add(i-1,DayItem(dayWeekName = day.dayOfWeek().asShortText, dayOfMonth = day.dayOfMonth, month=day.monthOfYear,year=day.year, day.monthOfYear().asShortText) )

            }
            _listDates.update{ listDays}
            _state.update { state ->
                Log.d(TAG, "LOADING END")
                state.copy(isLoading = false)
            }

        }
    }

    fun getSelectedDayTimestamp(): Long{
        val selectedDay = state.value.selectedDate
        return Instant.parse("${selectedDay.year}-${selectedDay.month}-${selectedDay.dayOfMonth}").millis
    }

    fun getYearMonthSelectedDate(): YearMonth {
        Log.d(TAG, "GET YEAR MONTH DATE")
        val todayInstant = Instant.ofEpochMilli(getSelectedDayTimestamp())
        val dtf = DateTimeFormat.forPattern("yyyy-MM")
        return YearMonth.parse(dtf.print(todayInstant))
    }

    fun onSelectDay(day:DayItem){
        createListDaysMonth(day)
        viewModelScope.launch {
            Log.v(TAG, "onSelectDay")
            _state.update { state ->
                state.copy(
                    selectedDate = day
                )
            }
        }
    }
}