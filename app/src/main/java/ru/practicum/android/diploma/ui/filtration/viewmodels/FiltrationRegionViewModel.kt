package ru.practicum.android.diploma.ui.filtration.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.repository.AreasInteractor
import ru.practicum.android.diploma.domain.repository.SetSearchFilterInteractor
import ru.practicum.android.diploma.ui.utils.XxxLiveData

open class FiltrationRegionViewModel(
    val regionsGetter: AreasInteractor,
    private val filterSetter: SetSearchFilterInteractor,
    private val parentId: String?
) : ViewModel() {
    val _LiveData = XxxLiveData<FiltrationRegionData>()
    var parentArea: Area? = null
    val liveData: LiveData<FiltrationRegionData> get() = _LiveData

    init {
        viewModelScope.launch {
            parentId?.let {
                parentArea = regionsGetter.getAreaById(parentId)
            }
            getRegions()
        }
    }

    open fun getRegions(search: String? = null) {
        viewModelScope.launch {
            val regions = if (parentId.isNullOrBlank()) {
                regionsGetter.getAllRegions(search)
            } else {
                regionsGetter.getRegionsInCountry(parentId, search)
            }

            if (regions.isEmpty()) {
                _LiveData.postValue(FiltrationRegionData.NotFound)
            } else {
                _LiveData.postValue(FiltrationRegionData.Regions(regions))
            }
        }
    }

    fun saveRegion(region: Area) {
        viewModelScope.launch {
            filterSetter.saveAreaTempValue(region)
            _LiveData.setSingleEventValue(FiltrationRegionData.GoBack(region))
        }
    }
}
