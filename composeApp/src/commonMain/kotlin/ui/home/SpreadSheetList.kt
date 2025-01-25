package com.upwork.googlesheetreader.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upwork.googlesheetreader.ui.postData.components.LoaderIndicator
import ui.ViewModelGoogleSheet
import ui.ViewModelGoogleSheet.HomeUiState


@Composable
fun SpreadSheetList(
    modifier: Modifier,
    viewModel: ViewModelGoogleSheet,
    onNavigateToDetails: () -> Unit,
    navigateToPostScreen: () -> Unit,
    navigateToSearchScreen: () -> Unit
) {
    val homeUiState by viewModel.homeUiState.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.getSpreadsheet()

    }
    when (homeUiState) {
        is HomeUiState.Loading -> {
            LoaderIndicator(modifier)
        }

        is HomeUiState.Success -> {
            val response = (homeUiState as HomeUiState.Success).data
            Scaffold(
                floatingActionButton = {
                    Box(modifier = Modifier.padding(16.dp)) {

                        FloatingActionButton(
                            onClick = {
                                viewModel.setSheetList(response, 0)
                                navigateToPostScreen.invoke()
                            },
                            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "add",
                                tint = Color.White
                            )
                        }

                        // Top FAB (offset to be above the bottom FAB)
                        FloatingActionButton(

                            onClick = {
                                navigateToSearchScreen.invoke()
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(y = (-72).dp),
                            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
// Adjust the offset as needed
                        ) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = Color.White
                            )
                        }
                    }

                },
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = modifier.padding(vertical = 15.dp, horizontal = 5.dp)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = "Sheets Available",
                            modifier = modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        LazyColumn(
                            modifier = modifier.padding(bottom = 40.dp, top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            itemsIndexed(response) { index, item ->
                                Row(
                                    modifier = modifier
                                        .fillMaxWidth()
                                        .padding(end = 20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        modifier = modifier
                                            .weight(1f)
                                            .padding(vertical = 10.dp, horizontal = 5.dp)
                                            .clickable {
                                                viewModel.setData(item.properties?.title ?: "")
                                                onNavigateToDetails.invoke()
                                            },
                                        text = item.properties?.title ?: "",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = FontFamily.Serif
                                    )

                                    Icon(
                                        Icons.Rounded.AddCircleOutline,
                                        contentDescription = "",
                                        modifier = modifier.size(30.dp).clickable {
                                            viewModel.setSheetList(response, index)
                                            navigateToPostScreen.invoke()
                                        }
                                    )
                                }
                                Divider(Modifier.height(1.dp))
                            }
                        }
                    }

                }
            }

        }

        else -> {

        }
    }


}