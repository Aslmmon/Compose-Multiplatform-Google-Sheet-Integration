package com.upwork.googlesheetreader.ui.postData.components

import PlatformQrCode
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.upwork.googlesheetreader.network.model.spreadsheet.Sheet
import com.upwork.googlesheetreader.ui.postData.PlayerData
import io.github.alexzhirkevich.qrose.rememberQrCodePainter


@Composable
fun SimpleOutlinedTextFieldSample(
    modifier: Modifier,
    label: String,
    textWritten: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = text,
        isError = isError,
        onValueChange = {
            text = it
            textWritten.invoke(text)
            isError = text.isEmpty()

        },
        label = { Text(label) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuBoxItem(
    modifier: Modifier,
    sheetList: Pair<List<Sheet>, Int>,
    selectedTextFunc: (String) -> Unit
) {
    //  val context = LocalContext.current
//    val coffeeDrinks = arrayOf("Americano", "Cappuccino", "Espresso", "Latte", "Mocha")
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember {
        mutableStateOf(
            sheetList.first[sheetList.second].properties?.title ?: ""
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = selectedText,
                onValueChange = {
                    selectedTextFunc.invoke(selectedText)

                },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                sheetList.first.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.properties?.title ?: "") },
                        onClick = {
                            selectedText = item.properties?.title ?: ""
                            selectedTextFunc.invoke(selectedText)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LoaderIndicator(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = modifier.size(50.dp),
            strokeWidth = 10.dp,
            strokeCap = StrokeCap.Square,
            color = androidx.compose.material3.MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun MinimalDialog(onDismissRequest: () -> Unit, playerData: PlayerData) {

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 15.dp, bottom = 20.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                QRcodePlayer(
                    modifier = Modifier.size(400.dp),
                    data =
                        arrayOf(
                            playerData.firstName,
                            playerData.secondName,
                            playerData.age,
                            playerData.position
                        )
                )
                Text(
                    text = playerData.firstName + " " + playerData.secondName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                if (playerData.age.isNotEmpty()) Text(
                    text = "Jersey : " + playerData.age,
                    fontWeight = FontWeight.Bold
                )
                if (playerData.position.isNotEmpty()) Text(
                    text = "Position : " + playerData.position,
                    fontWeight = FontWeight.Bold
                )
                if (playerData.isCaptured.isNotEmpty()) Text(
                    text = "is photographed : " + playerData.isCaptured,
                    fontWeight = FontWeight.Bold
                )
                if (playerData.other.isNotEmpty()) Text(
                    text = "other : " + playerData.other,
                    fontWeight = FontWeight.Bold
                )

            }


        }
    }
}

@Composable
fun QRcodePlayer(modifier: Modifier, vararg data: String) {
    Image(
        modifier = modifier,
        painter = rememberQrCodePainter(
            data.toString()

        ),
        contentDescription = "QR code referring to the playerData"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}
