package com.upwork.googlesheetreader.network.model.spreadsheetDetails

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpreadSheetDetails(
    @SerialName("majorDimension")
    val majorDimension: String="",
    @SerialName("range")
    val range: String="",
    @SerialName("values")
    val values: List<List<String>> = mutableListOf()
)