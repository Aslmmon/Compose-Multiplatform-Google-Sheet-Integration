package com.upwork.googlesheetreader.network.model.spreadsheetDetails

data class SpreadSheetDetails(
    val majorDimension: String,
    val range: String,
    val values: List<List<String>>
)