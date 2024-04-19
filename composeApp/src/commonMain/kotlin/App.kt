import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.upwork.googlesheetreader.ui.details.SpreadSheetDetails
import com.upwork.googlesheetreader.ui.home.SpreadSheetList
import com.upwork.googlesheetreader.ui.postData.PostDataScreen
import com.upwork.googlesheetreader.ui.splash.SplashScreen
import com.upwork.googlesheetreader.ui.theme.GoogleSheetReaderTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.ViewModelGoogleSheet



@Composable
@Preview
fun App() {
    val viewModel = ViewModelGoogleSheet()
    GoogleSheetReaderTheme {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color =  MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "splash") {
                composable("spreadsheet") {
                    SpreadSheetList(
                        modifier = Modifier,
                        onNavigateToDetails = {
                            navController.navigate("spreadsheetDetails")
                        }, navigateToPostScreen = {
                            navController.navigate("postScreen")
                        }, viewModel = viewModel
                    )

                }
                composable("spreadsheetDetails") {
                    SpreadSheetDetails(modifier = Modifier, navigateBack = {
                        navController.navigateUp()
                    }, viewModel = viewModel)
                }
                composable("postScreen") {
                    PostDataScreen(modifier = Modifier,viewModel= viewModel)
                }

                composable("splash") {
                    SplashScreen(modifier = Modifier, navigateToHome = {
                        navController.navigate("spreadsheet") {
                            navController.popBackStack()
                        }
                    })
                }

            }
        }
    }
}