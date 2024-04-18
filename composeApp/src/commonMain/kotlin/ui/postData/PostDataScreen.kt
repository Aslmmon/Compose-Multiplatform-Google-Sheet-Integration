package com.upwork.googlesheetreader.ui.postData

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Colors
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.upwork.googlesheetreader.ui.postData.components.ExposedDropdownMenuBoxItem
import com.upwork.googlesheetreader.ui.postData.components.LoaderIndicator
import com.upwork.googlesheetreader.ui.postData.components.SimpleOutlinedTextFieldSample
import ui.ViewModelGoogleSheet
import ui.ViewModelGoogleSheet.HomeUiState


@Composable
fun PostDataScreen(modifier: Modifier, viewModel: ViewModelGoogleSheet) {

    val homeUiState by viewModel.homeUiState.collectAsState()
    var isSubmitClicked by remember { mutableStateOf(false) }
    val playerData by remember {
        mutableStateOf(
            PlayerData(
                spreadSheetName = viewModel.sheetList.value.first.get(
                    viewModel.sheetList.value.second
                ).properties?.title ?: ""
            )
        )
    }

    LaunchedEffect(isSubmitClicked) {
        if (playerData.age.isEmpty() ||
            playerData.firstName.isEmpty() ||
            playerData.secondName.isEmpty() ||
            playerData.position.isEmpty()
        ) {
            isSubmitClicked = false
        } else {
            if (isSubmitClicked) {
                viewModel.postDataToSpreadSheet(playerData)
                isSubmitClicked = false
            }
        }
    }




    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {


        ExposedDropdownMenuBoxItem(modifier = modifier, viewModel.sheetList.value) { selectedText ->
            playerData.spreadSheetName = selectedText

        }

        SimpleOutlinedTextFieldSample(modifier, "First Name") { firstName ->
            playerData.firstName = firstName
        }
        SimpleOutlinedTextFieldSample(modifier, "Second Name") { secondName ->
            playerData.secondName = secondName

        }
        SimpleOutlinedTextFieldSample(modifier, "Age") { age ->
            playerData.age = age

        }
        SimpleOutlinedTextFieldSample(modifier, "Position") { pos ->
            playerData.position = pos
        }
        when (homeUiState) {
            is HomeUiState.SuccessSubmitPost -> {
                Text(text = "Data Submittied Succesffuly")
            }

            is HomeUiState.Loading -> {
                LoaderIndicator(modifier)

            }

            else -> {}
        }


        Button(modifier = modifier
            .fillMaxWidth()
            .height(45.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
            ),
            shape = RoundedCornerShape(8.dp), onClick = {
                isSubmitClicked = true
            }) {
            Text(text = "Submit data")
        }

    }
}


data class PlayerData(
    var spreadSheetName: String = "",
    var firstName: String = "",
    var secondName: String = "",
    var age: String = "",
    var position: String = "",
    var isCaptured: String = "",
    var other: String = ""

)