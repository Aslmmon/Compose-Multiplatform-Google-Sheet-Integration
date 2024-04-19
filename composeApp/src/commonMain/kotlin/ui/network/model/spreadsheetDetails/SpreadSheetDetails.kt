package com.upwork.googlesheetreader.network.model.spreadsheetDetails

import kotlinx.serialization.Serializable

@Serializable
data class SpreadSheetDetails(
    val majorDimension: String,
    val range: String,
    val values: List<List<String>>
)