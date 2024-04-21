package ui.network

import com.upwork.googlesheetreader.network.model.spreadsheet.SpreadSheetResponse
import com.upwork.googlesheetreader.network.model.spreadsheetDetails.SpreadSheetDetails
import com.upwork.googlesheetreader.ui.postData.PlayerData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineStart
import kotlinx.serialization.json.Json
import org.upwork.googlesheetkmp.BuildKonfig

val API_KEY = BuildKonfig.GOOGLE_API_KEY
const val BASE_URL = "https://sheets.googleapis.com"
const val BASE_URL_GoogleSheet = "https://script.google.com"

const val SHEET_ID = "1SMrpeJC2isCTJotRYXBDNENNbDVzCcazonOOwUQ-Vf0"
const val SHEET_ID_GoogleSheet =
    "AKfycbyNq9KMdhh2hebpl7IERNOAvmEovRzMYsMYIi237L5H7MOag0NbnXP_Kd4Ry4gihPNL"

class KtorComponent {

    private val httpClient = HttpClient {

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    print("HTTP Client  " + message)
                }
            }
            logger = Logger.SIMPLE
            level = LogLevel.BODY
            filter { request ->
                request.url.host.contains("ktor.io")
            }
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
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

    suspend fun postDataToSpreadSheet(playerData: PlayerData): HttpResponse {
        with(playerData) {
            val response: HttpResponse = httpClient.post(
                "${BASE_URL_GoogleSheet}/macros/s/${SHEET_ID_GoogleSheet}/exec"
            ) {
                url {
                    parameters.append("action", "add")
                    parameters.append("spreadsheetName", spreadSheetName)
                    parameters.append("playerFirstName", firstName.trim())
                    parameters.append("playerSecondName", secondName.trim())
                    parameters.append("age", age.trim())
                    parameters.append("position", position.trim())
                    parameters.append("isShoot", "FALSE")

                }
            }
            print(response.toString())
            return response
        }
    }

    suspend fun editPlayerShootStatus(playerData: PlayerData): HttpResponse {
        with(playerData) {
            val response: HttpResponse = httpClient.post(
                "${BASE_URL_GoogleSheet}/macros/s/${SHEET_ID_GoogleSheet}/exec"
            ) {
                url {
                    parameters.append("action", "edit")
                    parameters.append("spreadsheetName", spreadSheetName)
                    parameters.append("playerFirstName", firstName)
                    parameters.append("isShoot", isCaptured)
                }
            }
            print(response.toString())
            return response
        }
    }


}