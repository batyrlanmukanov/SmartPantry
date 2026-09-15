package kz.kbtu.smartpantry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kz.kbtu.smartpantry.core.domain.ObserveProfileUseCase
import kz.kbtu.smartpantry.core.domain.ObserveSessionUseCase
import kz.kbtu.smartpantry.core.domain.SeedDemoInventoryUseCase

@HiltViewModel
class MainViewModel @Inject constructor(
    observeSessionUseCase: ObserveSessionUseCase,
    observeProfileUseCase: ObserveProfileUseCase,
    private val seedDemoInventoryUseCase: SeedDemoInventoryUseCase,
) : ViewModel() {

    val session = observeSessionUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val profile = observeProfileUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    private var seededEmail: String? = null

    fun ensureDemoData(ownerEmail: String) {
        if (!BuildConfig.PRELOAD_SAMPLE_DATA) return
        if (seededEmail == ownerEmail) return

        seededEmail = ownerEmail
        viewModelScope.launch {
            seedDemoInventoryUseCase(ownerEmail)
        }
    }
}
