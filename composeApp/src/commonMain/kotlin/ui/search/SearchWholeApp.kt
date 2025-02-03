package ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Colors
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.upwork.googlesheetreader.ui.postData.PlayerData
import com.upwork.googlesheetreader.ui.postData.components.LoaderIndicator
import com.upwork.googlesheetreader.ui.postData.components.MinimalDialog
import kotlinx.coroutines.delay
import ui.utils.Logger
import ui.utils.PlayerDataRow

@Composable
fun SearchScreen(
    modifier: Modifier,
    viewModel: SearchViewModel,
) {

    val searchState by viewModel.searchState.collectAsState()
    val filteredPlayers by viewModel.filteredPlayers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val addPlayer by viewModel.addPlayer.collectAsState()
    val openAlertDialog = remember { mutableStateOf(false) }
    val hasDataBeenFetched = remember { mutableStateOf(false) }

    var playerData: PlayerData


    LaunchedEffect(!hasDataBeenFetched.value) {
        delay(3000)
        viewModel.getAllPlayersFromAllSheets()
        hasDataBeenFetched.value = true

    }
    Logger.e("hasbeenFetched", hasDataBeenFetched.toString())


    Column(
        modifier = Modifier.background(color = Color.White)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = {
                viewModel.updateSearchQuery(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            placeholder = { Text("Search by name") }
        )

        when (searchState) {
            is SearchViewModel.SearchState.Loading -> {
                LoaderIndicator()
            }

            is SearchViewModel.SearchState.Success -> {
//                val result = (searchState as SearchViewModel.SearchState.Success).allPlayers

                LazyColumn {
                    itemsIndexed(filteredPlayers) { index, item ->
                        playerData = PlayerData(
                            firstName = item.get(0),
                            secondName = item.get(1),
                            spreadSheetName = item.get(3),
                            isCaptured = item.get(4)
                        )
                      //  viewModel.updatePlayer(item)
                        PlayerDataRow(
                            modifier = Modifier.padding(5.dp),
                            playerData = playerData,
                            onStatusChange = { updatedPlayer, newStatus -> },
                            openDialogChangeStatus = { player ->
                                viewModel.updatePlayer(player)
                                openAlertDialog.value = true


                            },
                            qrCodeData = arrayOf(
                                item.get(0),
                                item.get(1),
                                item.get(2),
                                item.get(3)
                            )
                        )
                        Divider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 0.5.dp,
                            color = Color.LightGray
                        )
                    }
                }



            }


            is SearchViewModel.SearchState.Error -> {
                Text("Error")

            }

        }


        when (openAlertDialog.value) {

            true -> {

                MinimalDialog(onDismissRequest = {
                    openAlertDialog.value = false
                }, addPlayer)

            }

            false -> {}
        }

    }



}


