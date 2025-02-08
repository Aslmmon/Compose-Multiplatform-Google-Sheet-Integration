package ui.network

import com.upwork.googlesheetreader.network.model.spreadsheet.SpreadSheetResponse
import com.upwork.googlesheetreader.network.model.spreadsheetDetails.SpreadSheetDetails
import com.upwork.googlesheetreader.ui.postData.PlayerData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.upwork.googlesheetkmp.BuildKonfig
import ui.search.data.PlayerDataResponseItem
import io.ktor.client.statement.bodyAsText
import io.ktor.http.append
import io.ktor.http.headers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString


/**
 * link for edit App Script : https://script.google.com/home/projects/1eRRxTca7cZHxkS0HQJUpLgzlm3ZpcTyUXbU5_irHpg4BpBdp7hBQ_E9u/edit
 */
val API_KEY = BuildKonfig.GOOGLE_API_KEY
const val BASE_URL = "https://sheets.googleapis.com"
const val BASE_URL_GoogleSheet = "https://script.google.com"

//https://script.google.com/macros/s/AKfycbzn5KmkOicmN8K3Sde_-i-PyRq4hBAEWXC8Joz0Bt4XwXk5toX7yNGnpGt6VVgLGn-y/exec
const val SHEET_ID = "1SMrpeJC2isCTJotRYXBDNENNbDVzCcazonOOwUQ-Vf0"

//const val DeploymentGoogleSheetAppScriptID = "AKfycbyNq9KMdhh2hebpl7IERNOAvmEovRzMYsMYIi237L5H7MOag0NbnXP_Kd4Ry4gihPNL"
const val DeploymentGoogleSheetAppScriptID =
    "AKfycbxMq0CxPin0BnKCFtagxN0QNUA_s7eHO_Ze40pe4XwPWj3eLeKMycjfE2jcr3SPeKDx"

class KtorComponent {

    private val httpClient = HttpClient {

        install(Logging) {
            logger = Logger.SIMPLE // Or Logger.DEFAULT
            level = LogLevel.ALL  //
            // Or other LogLevel
        }

        install(HttpRedirect) {
            checkHttpMethod = false
        }
        install(ContentNegotiation) {

            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json)
            }

        }


    }


    suspend fun searchPlayer(searchString: String): HttpResponse? {
        val requestBody = SearchRequest("search", searchString)
        val maxRedirects = 10 // Prevent infinite loops
        var currentUrl =
            "https://script.google.com/macros/s/AKfycbyO6siSZve0G57IVvjPZceqEgpsp1geM-7tYO6jbPhoT4Kw2HUBMVpoAlxFiylQFSqI/exec" // Replace with your initial URL
        var response: HttpResponse? = null

        try {


            for (i in 0 until maxRedirects) {
                response = httpClient.get(
                    currentUrl
                ) {
                    contentType(ContentType.Application.Json)
                    setBody(requestBody)

                }
                // httpClient.close()
                if (response.status == HttpStatusCode.OK) {
                    val body = response.bodyAsText()
                    println("Final Response Body: $body")
                    break
                } else if (response.status == HttpStatusCode.Found) {
                    val location = response.headers[HttpHeaders.Location]
                    println("Redirecting to: $location")
                    currentUrl = location!!
                    // Update the URL for the next request
                    httpClient.get(currentUrl)
                } else {
                    println("Unexpected Status: ${response.status}")
                    break
                }

            }
            if (response?.status != HttpStatusCode.OK) {
                println("Failed to get 200 after $maxRedirects redirects")
            }

        } catch (e: Exception) {
            println("Error: ${e.message}")
        } finally {
            httpClient.close()

        }
        ui.utils.Logger.d("search Response", response.toString())
        return response
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSpreadsheetDataWithDetails(): Flow<Pair<SpreadSheetResponse, SpreadSheetDetails>> =
        flow {
            val spreadSheetResponse = getSpreadsheetData()
            emit(spreadSheetResponse)
        }.flatMapConcat { response ->
            response.sheets.take(10).asFlow().map { sheet ->
                delay(50)
                val spreadSheetDetails = getSpreadsheetDataDetails(sheet.properties?.title ?: "")
                Pair(response, spreadSheetDetails)
            }

        }


    suspend fun postDataToSpreadSheet(playerData: PlayerData): HttpResponse {
        with(playerData) {
            val postData = PostData(
                action = "add",
                spreadsheetName = spreadSheetName,
                playerFirstName = firstName.trim(),
                playerSecondName = secondName.trim(),
                age = age.trim(),
                position = position.trim(),
                isShoot = "FALSE"
            )
            val response: HttpResponse = httpClient.post(
                "${BASE_URL_GoogleSheet}/macros/s/${DeploymentGoogleSheetAppScriptID}/exec"
            ) {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(postData))
            }
            ui.utils.Logger.e("playerDetails Request", postData.toString())
            print(response.toString())
            return response
        }
    }

    suspend fun editPlayerShootStatus(playerData: PlayerData): HttpResponse {
        with(playerData) {
            val editPostData = EditPostData(
                action = "edit",
                spreadsheetName = spreadSheetName,
                playerFirstName = firstName.trim(), playerSecondName = secondName.trim(),
                isShoot = if (isCaptured== "TRUE") "FALSE" else "TRUE"
            )

            val response: HttpResponse = httpClient.post(
                "${BASE_URL_GoogleSheet}/macros/s/${DeploymentGoogleSheetAppScriptID}/exec"
            ) {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(editPostData))
            }
            ui.utils.Logger.e("playerDetails Request", Json.encodeToString(editPostData))
            ui.utils.Logger.e("playerDetails headers", response.headers.toString())

            return response
        }
    }


}

@kotlinx.serialization.Serializable
data class SearchRequest(val action: String, val searchString: String)


@Serializable
data class PostData(
    val action: String,
    val spreadsheetName: String,
    val playerFirstName: String,
    val playerSecondName: String,
    val age: String,
    val position: String,
    val isShoot: String
)

@Serializable
data class EditPostData(
    val action: String, val spreadsheetName: String,
    val playerFirstName: String,
    val playerSecondName: String,
    val isShoot: String
)
