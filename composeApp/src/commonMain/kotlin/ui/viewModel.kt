package ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upwork.googlesheetreader.network.model.spreadsheet.Sheet
import com.upwork.googlesheetreader.network.model.spreadsheetDetails.SpreadSheetDetails
import com.upwork.googlesheetreader.ui.postData.PlayerData
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.network.KtorComponent
import ui.utils.Logger


class ViewModelGoogleSheet : ViewModel() {
    //    private var retrofitMoviesNetworkApi = RetrofitMoviesNetworkApi
    private val ktorComponent = KtorComponent()

    val text = MutableStateFlow("")
    val dbText = text.debounce(300)
        .distinctUntilChanged()
        .flatMapLatest {
            filterSpreadSheetDetails(it)
        }


    private var _homeUiState: MutableStateFlow<HomeUiState?> =
        MutableStateFlow(null)
    val homeUiState: MutableStateFlow<HomeUiState?> get() = _homeUiState


    private var _EditState: MutableStateFlow<EditUIState?> =
        MutableStateFlow(null)
    val EditState: MutableStateFlow<EditUIState?> get() = _EditState

    private val _data = mutableStateOf("")
    val data: State<String> = _data

    private val _sheetList = mutableStateOf(Pair(emptyList<Sheet>(), 0))
    val sheetList: MutableState<Pair<List<Sheet>, Int>> = _sheetList


    private val _addPlayer = MutableStateFlow(PlayerData())
    val addPlayer: StateFlow<PlayerData> = _addPlayer.asStateFlow()


    fun updatePlayer(item: PlayerData) {
        _addPlayer.update {
            PlayerData(
                firstName = item.firstName,
                secondName = item.secondName,
                spreadSheetName = item.spreadSheetName,
                age = item.age,
                isCaptured = item.isCaptured, other = ""
            )
        }
    }

    fun setData(newData: String) {
        _data.value = newData
    }

    fun setSheetList(newData: List<Sheet>, index: Int) {
        _sheetList.value = Pair(newData, index)
    }

    var dataList: SpreadSheetDetails = SpreadSheetDetails()

    fun getSpreadsheet() {
        viewModelScope.launch {
            _homeUiState.update {
                HomeUiState.Loading()

            }
            try {
                val data = ktorComponent.getSpreadsheetData()
                _homeUiState.update {
                    HomeUiState.Success(data.sheets)

                }
            } catch (e: Exception) {
                _homeUiState.update {
                    HomeUiState.Error(e.message.toString())
                }
            }
        }


    }

    fun getSpreadsheetDetails(sheetName: String) {


        viewModelScope.launch {
            _homeUiState.update {
                HomeUiState.Loading()
            }
            try {
                dataList = ktorComponent.getSpreadsheetDataDetails(sheetName)


                _homeUiState.update {
                    HomeUiState.Details(dataList.values)

                }
            } catch (e: Exception) {
                _homeUiState.update {
                    HomeUiState.Error(e.message.toString())
                }
            }
        }


    }

    fun filterSpreadSheetDetails(playerName: String): Flow<String> {
        print("total players ${dataList.values}")

        val filterdList = dataList.values.filter {
            val completeName = it.get(0) + it.get(1)
            completeName.contains(playerName, ignoreCase = true)
        }
        print("filterd list  $filterdList")
        print(filterdList.toString())

        viewModelScope.launch {
            try {
                _homeUiState.update {
                    HomeUiState.Details(filterdList)
                }
            } catch (e: Exception) {
                _homeUiState.update {
                    HomeUiState.Error(e.message.toString())
                }
            }
        }
        return flow { }


    }


    suspend fun postDataToSpreadSheet(playerData: PlayerData) {

        _homeUiState.update {
            HomeUiState.Loading()
        }
        viewModelScope.launch {

            try {
                val response = ktorComponent.postDataToSpreadSheet(
                    playerData
                )
                if (response.status == HttpStatusCode.Found) {
                    _homeUiState.update {
                        HomeUiState.SuccessSubmitPost()
                    }
                } else {
                    _homeUiState.update {
                        HomeUiState.Error(response.status.value.toString() + " " + response.status.description)
                    }
                    Logger.e(
                        "  Error",
                        response.status.value.toString() + " " + response.status.description
                    )

                }

            } catch (e: Exception) {
                _homeUiState.update {
                    HomeUiState.Error(e.message.toString())
                }
            }
        }


    }

    suspend fun editIsShootStatus(playerData: PlayerData) {

        _EditState.update {
            EditUIState.Loading()
        }
        viewModelScope.launch {

            try {
                Logger.e("playerDetails Request", playerData.toString())
                val response = ktorComponent.editPlayerShootStatus(
                    playerData
                )

                if (response.status == HttpStatusCode.MethodNotAllowed) {
                    _EditState.update {
                        EditUIState.SuccessSubmitPost()
                    }
                    getSpreadsheetDetails(playerData.spreadSheetName)

                } else {
                    _EditState.update {
                        EditUIState.Error(
                            response.body<String>()
                                .toString() + "\n error message " + response.status.value + " " + response.status.description
                        )
                    }
                }

            } catch (e: Exception) {
                _EditState.update {
                    EditUIState.Error(e.message.toString())
                }
            }
        }


    }


    sealed class HomeUiState {
        data class Success(val data: List<Sheet>) : HomeUiState()
        data class Details(val data: List<List<String>>) : HomeUiState()
        class SuccessSubmitPost : HomeUiState()
        data class Error(val message: String) : HomeUiState()
        class Loading : HomeUiState()

    }

    sealed class EditUIState {
        class SuccessSubmitPost : EditUIState()
        data class Error(val message: String) : EditUIState()
        class Loading : EditUIState()

    }


}