package com.upwork.googlesheetreader.ui.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DoneOutline
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upwork.googlesheetreader.ui.postData.PlayerData
import com.upwork.googlesheetreader.ui.postData.components.LoaderIndicator
import com.upwork.googlesheetreader.ui.postData.components.MinimalDialog
import com.upwork.googlesheetreader.ui.postData.components.QRcodePlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ui.ViewModelGoogleSheet
import ui.ViewModelGoogleSheet.*

@Composable
fun SpreadSheetDetails(
    modifier: Modifier,
    navigateBack: () -> Unit,
    viewModel: ViewModelGoogleSheet
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val editUIState by viewModel.EditState.collectAsState()

    val openAlertDialog = remember { mutableStateOf(false) }
    val isShootStatusChanged = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val playerData by remember {
        mutableStateOf(
            PlayerData()
        )
    }

    LaunchedEffect(Unit) {
        viewModel.getSpreadsheetDetails(viewModel.data.value)
        playerData.spreadSheetName = viewModel.data.value
    }




    when (homeUiState) {
        is HomeUiState.Loading -> {
            LoaderIndicator(modifier)
        }

        is HomeUiState.Details -> {
            val response = (homeUiState as HomeUiState.Details).data
            if (response.isEmpty()) {
                Text(text = "empty Data")
            } else

                Column(modifier = modifier.padding(vertical = 15.dp, horizontal = 5.dp)) {
                    Text(
                        text = viewModel.data.value,
                        modifier = modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    Box {
                        LazyColumn(
                            modifier = modifier,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(response) { item ->
                                Row(
                                    modifier = modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            updatePlayerData(playerData, item,item[4])
                                            openAlertDialog.value = true
                                        }
                                        .padding(5.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(modifier = modifier.weight(1f)) {

                                        Text(
                                            modifier = modifier
                                                .weight(0.5f)
                                                .padding(vertical = 10.dp, horizontal = 5.dp),
                                            text = try {

                                                item[0] + " " + item[1]
                                            } catch (e: Exception) {
                                                "exception"
                                            },
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Serif
                                        )

                                        if (item.size > 4) {
                                            if (item[4].contains("FALSE", true)) {
                                                Icon(
                                                    Icons.Rounded.ErrorOutline,
                                                    contentDescription = "",
                                                    tint = Color.Red,
                                                    modifier = modifier
                                                        .size(30.dp)
                                                        .weight(0.5f).clickable {
                                                            updatePlayerData(playerData, item,"TRUE")
                                                            coroutineScope.launch {
                                                                viewModel.editIsShootStatus(
                                                                    playerData
                                                                )
                                                            }
                                                        }
                                                )
                                            } else {
                                                Icon(
                                                    Icons.Rounded.DoneOutline,
                                                    contentDescription = "",
                                                    tint = Color.Green,
                                                    modifier = modifier
                                                        .size(30.dp)
                                                        .weight(0.5f).clickable {
                                                            updatePlayerData(playerData, item,"FALSE")
                                                            coroutineScope.launch {
                                                                viewModel.editIsShootStatus(
                                                                    playerData
                                                                )
                                                            }

                                                        }
                                                )
                                            }
                                        }
                                    }
                                    QRcodePlayer(
                                        modifier = modifier.size(50.dp), data =
                                        arrayOf(item[0], item[1], item[2], item[3])

                                    )

                                }
                                Divider(Modifier.height(1.dp))
                            }
                        }

                        when(editUIState){
                            is EditUIState.Loading->{
                                LoaderIndicator(modifier)
                            }
                            is EditUIState.SuccessSubmitPost->{

                            }
                            else->{}
                        }
                    }
                }
        }

        else -> {}
    }

    when (openAlertDialog.value) {
        true -> MinimalDialog(onDismissRequest = {
            openAlertDialog.value = false
        }, playerData)

        false -> {}
    }

}

private fun updatePlayerData(
    playerData: PlayerData,
    item: List<String>,isCapturedValue:String
) {
    with(playerData) {
        try {
            firstName = item[0]
            secondName = item[1]
            age = item[2]
            position = item[3]
            isCaptured = isCapturedValue
            other = item[5]
        } catch (e: Exception) {
        }
    }
}

