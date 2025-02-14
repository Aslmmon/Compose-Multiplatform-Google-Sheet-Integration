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
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.ChangeCircle
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upwork.googlesheetreader.ui.postData.PlayerData
import com.upwork.googlesheetreader.ui.postData.components.AlertDialogExample
import com.upwork.googlesheetreader.ui.postData.components.LoaderIndicator
import com.upwork.googlesheetreader.ui.postData.components.MinimalDialog
import kotlinx.coroutines.launch
import ui.ViewModelGoogleSheet
import ui.ViewModelGoogleSheet.EditUIState
import ui.ViewModelGoogleSheet.HomeUiState
import ui.utils.Logger
import ui.utils.QRcodePlayer

@Composable
fun SpreadSheetDetails(
    modifier: Modifier,
    navigateBack: () -> Unit,
    viewModel: ViewModelGoogleSheet
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val editUIState by viewModel.EditState.collectAsState()
    val openAlertDialog = remember { mutableStateOf(false) }
    val openDialogChangeStatus = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val text by viewModel.text.collectAsState("")
    val addPlayer by viewModel.addPlayer.collectAsState()



    LaunchedEffect(Unit) {
        viewModel.getSpreadsheetDetails(viewModel.data.value)
        viewModel.text.value = ""
    }




    when (homeUiState) {
        is HomeUiState.Loading -> {
            LoaderIndicator(modifier)
        }

        is HomeUiState.Details -> {
            val response = (homeUiState as HomeUiState.Details).data
            Column(modifier = modifier.padding(vertical = 15.dp, horizontal = 5.dp)) {

                Text(
                    text = viewModel.data.value,
                    modifier = modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
                Row(verticalAlignment = Alignment.CenterVertically) {


                    IconButton(onClick = {navigateBack.invoke() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                    OutlinedTextField(
                        modifier = modifier.fillMaxWidth().padding(horizontal = 10.dp),
                        value = text,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black
                        ),
                        onValueChange = {
                            viewModel.text.value = (it)
                        },
                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        label = { Text("Search here ", color = Color.Black) }
                    )
                }
                Box {

                    PlayerList(
                        response,
                        viewModel = viewModel,
                        modifier = Modifier,
                        openAlertDialog=openAlertDialog,
                        openDialogChangeStatus=openDialogChangeStatus,
                    )

                    when (editUIState) {
                        is EditUIState.Loading -> {
                            LoaderIndicator(modifier)
                        }

                        is EditUIState.SuccessSubmitPost -> {

                        }
                        is  EditUIState.Error ->{
                            val error = (editUIState as EditUIState.Error).message
                            Logger.e("PlayerDetails Response", error.toString())
                        }

                        else -> {}
                    }
                }
            }
        }

        else -> {}
    }

    when (openAlertDialog.value) {
        true -> {
            Logger.e("playerDetails", addPlayer.toString())
            MinimalDialog(onDismissRequest = {
                openAlertDialog.value = false
            }, addPlayer)

        }

        false -> {}
    }
    when (openDialogChangeStatus.value) {
        true -> AlertDialogExample(
            onDismissRequest = {
                openDialogChangeStatus.value = false
            },
            onConfirmation = {
                coroutineScope.launch {
                    viewModel.editIsShootStatus(
                        addPlayer
                    )
                }
                openDialogChangeStatus.value = false

            },
            dialogText = "Are you sure want to change Shooting Status of Roster ",
            dialogTitle = "",
            icon = Icons.Rounded.ChangeCircle
        )

        false -> {}
    }

}

@Composable
fun PlayerList(
    response: List<List<String>>,
    modifier: Modifier = Modifier,
    viewModel: ViewModelGoogleSheet,
    openAlertDialog: MutableState<Boolean>,
    openDialogChangeStatus: MutableState<Boolean>,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(
            items = response,
//            key = { item -> item } // Use a unique key for each item
        ) { item ->
            // Extract data from the list only once
            val player =
                remember(item) { // Use remember to avoid recalculating on each recomposition
                    try {
                        PlayerData(
                            firstName = item[0],
                            secondName = item[1],
                            age = item[2],
                            spreadSheetName = item[3],
                            isCaptured = item[4]
                        )
                    } catch (e: IndexOutOfBoundsException) {
                        // Handle the case where the list doesn't have enough elements
                        PlayerData(
                            firstName = "N/A",
                            secondName = "N/A",
                            age = "N/A",
                            spreadSheetName = "N/A",
                            isCaptured = "N/A"
                        )
                    }
                }

            PlayerListItem(
                player = player,
                modifier = modifier,
                viewModel = viewModel,
                openAlertDialog = openAlertDialog,
                openDialogChangeStatus = openDialogChangeStatus,
                item = item,
            )
            Divider(Modifier.height(1.dp))
        }
    }
}


@Composable
fun PlayerListItem(
    player: PlayerData,
    modifier: Modifier,
    viewModel: ViewModelGoogleSheet,
    openAlertDialog: MutableState<Boolean>,
    openDialogChangeStatus: MutableState<Boolean>,
    item: List<String>,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                viewModel.updatePlayer(player)
                openAlertDialog.value = true
            }
            .padding(5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = modifier
                    .weight(0.5f)
                    .padding(vertical = 10.dp, horizontal = 5.dp),
                text = "${player.firstName} ${player.secondName}", // Use string templates for cleaner code
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif
            )
            if (item.size > 4) {
                val icon = if (player.isCaptured.equals("FALSE", ignoreCase = true)) {
                    Icons.Rounded.Cancel
                } else {
                    Icons.Rounded.CheckCircle
                }
                val tint = if (player.isCaptured.equals("FALSE", ignoreCase = true)) {
                    Color.Red
                } else {
                    Color.DarkGray
                }
                Icon(
                    imageVector = icon,
                    contentDescription = if (player.isCaptured.equals(
                            "FALSE",
                            ignoreCase = true
                        )
                    ) "Not Captured" else "Captured",
                    tint = tint,
                    modifier = modifier
                        .size(30.dp)
                        .weight(0.5f)
                        .clickable {
                            viewModel.updatePlayer(player)
                            openDialogChangeStatus.value = true
                        }
                )
            }
        }
        QRcodePlayer(
            modifier = modifier.size(50.dp),
            data = arrayOf(item[0], item[1], item[2], item[3])
        )
    }
}


