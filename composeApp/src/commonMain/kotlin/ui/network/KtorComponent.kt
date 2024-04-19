package ui.network

import com.upwork.googlesheetreader.network.model.spreadsheet.SpreadSheetResponse
import com.upwork.googlesheetreader.network.model.spreadsheetDetails.SpreadSheetDetails
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.upwork.googlesheetkmp.BuildKonfig

val API_KEY = BuildKonfig.GOOGLE_API_KEY
const val BASE_URL = "https://sheets.googleapis.com"
const val SHEET_ID = "1SMrpeJC2isCTJotRYXBDNENNbDVzCcazonOOwUQ-Vf0"

class KtorComponent {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getSpreadsheetData(): SpreadSheetResponse {
        //var data =
        val spreadSheetResponse: SpreadSheetResponse =
            httpClient.get("${BASE_URL}/v4/spreadsheets/${SHEET_ID}?fields=sheets(properties(title))&key=${API_KEY}")
                .body()
        return spreadSheetResponse


    }

    suspend fun getSpreadsheetDataDetails(nameSheet: String): SpreadSheetDetails {
        val spreadSheetDetails: SpreadSheetDetails =
            httpClient.get("${BASE_URL}/v4/spreadsheets/${SHEET_ID}/values/${nameSheet}!A2:F150?key=${API_KEY}")
                .body()
        return spreadSheetDetails


    }

//    @GET(value = "/v4/spreadsheets/1SMrpeJC2isCTJotRYXBDNENNbDVzCcazonOOwUQ-Vf0/values/{name-sheet}!A2:F150?key=${API_KEY}")
//    suspend fun getDetailsOfSpreadSheet(
//        @Path("name-sheet") nameSheet: String
//    ): SpreadSheetDetails


//    @GET(value = "")
//    suspend fun getSpreadSheetData(
//    ): SpreadSheetResponse

}