package ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upwork.googlesheetreader.ui.postData.PlayerData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.network.KtorComponent

class SearchViewModel : ViewModel() {


    private val ktorComponent = KtorComponent()


    private val _searchState = MutableStateFlow<SearchState>(SearchState.Loading)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()


    private val _filteredPlayers = MutableStateFlow<List<List<String>>>(emptyList())
    val filteredPlayers: StateFlow<List<List<String>>> = _filteredPlayers.asStateFlow()

    var allPlayers: ArrayList<List<String>> = arrayListOf()


    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()


    private val _addPlayer = MutableStateFlow(PlayerData())
    val addPlayer: StateFlow<PlayerData> = _addPlayer.asStateFlow()


    init {
        _searchQuery
            .debounce(500L) // Debounce for 500 milliseconds
            .distinctUntilChanged() // Only emit if the query has changed
            .onEach { query ->
                filterPlayers(query)
            }
            .launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }


    fun updatePlayer(item: PlayerData) {
        _addPlayer.update {
            PlayerData(
                firstName = item.firstName,
                secondName = item.secondName,
                spreadSheetName = item.spreadSheetName,
                isCaptured = item.isCaptured, other = ""
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        clearSearchQuery()
    }

    private fun clearSearchQuery() {
        _searchQuery.update { "" }
    }
    suspend fun getAllPlayersFromAllSheets() {
        allPlayers.clear()
        _searchState.update {
            SearchState.Loading
        }
        viewModelScope.launch {
            try {
                ktorComponent.getSpreadsheetDataWithDetails().collect() { (_, details) ->
                    details.values.forEachIndexed { index, strings ->
                        allPlayers.add(strings)
                    }
                }

                _searchState.update {
                    SearchState.Success(allPlayers)
                }
                filterPlayers(_searchQuery.value)

            } catch (e: Exception) {
                _searchState.update {
                    SearchState.Error(e.message.toString())
                }
            }
        }


    }

    private fun filterPlayers(query: String) {
        val filteredList = allPlayers.filter { item ->
            val firstName = item.getOrNull(0) ?: ""
            val secondName = item.getOrNull(1) ?: ""
            firstName.contains(query, ignoreCase = true) ||
                    secondName.contains(query, ignoreCase = true)
        }
        _filteredPlayers.update { filteredList }
    }


    sealed class SearchState {
        data class Success(
            val allPlayers: ArrayList<List<String>>
        ) : SearchState()

        data object Loading : SearchState()

        data class Error(val message: String) : SearchState()
    }
}