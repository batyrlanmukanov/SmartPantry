package kz.kbtu.smartpantry.feature.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kz.kbtu.smartpantry.core.domain.ObserveWeeklyStatsUseCase

const val ANALYTICS_ROUTE = "analytics"

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    observeWeeklyStatsUseCase: ObserveWeeklyStatsUseCase,
) : ViewModel() {

    val stats = observeWeeklyStatsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )
}
