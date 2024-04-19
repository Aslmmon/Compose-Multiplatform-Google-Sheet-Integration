package com.upwork.googlesheetreader.network.model.spreadsheet

import kotlinx.serialization.Serializable

@Serializable
data class SpreadSheetResponse(
    val sheets: List<Sheet>
)