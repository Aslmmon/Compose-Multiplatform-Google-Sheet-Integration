package com.upwork.googlesheetreader.network.model.spreadsheet

import kotlinx.serialization.Serializable

@Serializable
data class Sheet(
    val properties: Properties?=null
)