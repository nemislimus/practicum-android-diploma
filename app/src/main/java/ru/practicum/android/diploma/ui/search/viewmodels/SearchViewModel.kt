package ru.practicum.android.diploma.ui.search.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.domain.models.Resource
import ru.practicum.android.diploma.domain.models.VacancyShort
import ru.practicum.android.diploma.domain.search.api.SearchInteractor
import ru.practicum.android.diploma.ui.utils.XxxLiveData
import ru.practicum.android.diploma.util.debounce

class SearchViewModel(
    private val searchInteractor: SearchInteractor
) : ViewModel() {

    private var currentPage: Int = 0
    private var maxPages: Int = Int.MAX_VALUE
    private val vacanciesList: MutableList<VacancyShort> = mutableListOf()
    private var isNextPageLoading = false

    private val _searchState: XxxLiveData<SearchState> = XxxLiveData()
    internal val searchState: LiveData<SearchState> get() = _searchState

    private val _searchDebounce: (String) -> Unit =
        debounce(true, viewModelScope, SEARCH_DEBOUNCE_DELAY) { searchText ->
            searchVacancies(searchText)
        }

    private var lastSearchRequest: String = ""

    fun searchDebounce(searchQuery: String) {
        if (searchQuery == lastSearchRequest) {
            return
        }
        clearPagingHistory()
        lastSearchRequest = searchQuery

        _searchDebounce(searchQuery)
    }

    private fun clearPagingHistory() {
        vacanciesList.clear()
        maxPages = Integer.MAX_VALUE
        currentPage = 0
    }

    private fun searchVacancies(searchQuery: String) {
        if (searchQuery.isNotEmpty() && currentPage < maxPages) {
            renderLoadingState()
            viewModelScope.launch {
                searchInteractor.searchVacancy(searchQuery, currentPage).collect { result ->
                    withContext(Dispatchers.Main) {
                        when (result) {
                            is Resource.ConnectionError -> {
                                if (currentPage == 0) {
                                    renderState(SearchState.ConnectionError(true), true)
                                } else {
                                    _searchState.setSingleEventValue(SearchState.ConnectionError(false))
                                }
                            }

                            is Resource.NotFoundError -> renderState(SearchState.NotFoundError, true)
                            is Resource.ServerError -> renderState(SearchState.NotFoundError, true)
                            is Resource.Success -> {
                                with(result.data) {
                                    isNextPageLoading = false
                                    maxPages = pages
                                    if (found > 0) {
                                        vacanciesList.addAll(items)
                                        renderState(SearchState.Content(vacanciesList, currentPage == 0), true)
                                        renderState(SearchState.VacanciesCount(found))
                                        ++currentPage
                                    } else if (currentPage == 0) {
                                        renderState(SearchState.NotFoundError, true)
                                    } else {
                                        // nothing to do
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun setNoScrollOnViewCreated() {
        _searchState.setStartValue(SearchState.Content(vacanciesList, false))
    }

    private fun renderLoadingState() {
        if (currentPage == 0) {
            renderState(SearchState.IsLoading, true)
        } else {
            _searchState.setSingleEventValue(SearchState.IsLoadingNextPage)
        }
    }

    private fun renderState(newState: SearchState, clearOtherStates: Boolean = false) {
        if (clearOtherStates) {
            _searchState.clear()
        }
        _searchState.setValue(newState)
    }

    fun onLastItemReached() {
        if (!isNextPageLoading) {
            isNextPageLoading = true
            searchVacancies(lastSearchRequest)
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
