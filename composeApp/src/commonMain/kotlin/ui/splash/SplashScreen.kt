package com.upwork.googlesheetreader.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import googlesheetkmp.composeapp.generated.resources.Res
import googlesheetkmp.composeapp.generated.resources.compose_multiplatform
import googlesheetkmp.composeapp.generated.resources.logo_splash
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SplashScreen(modifier: Modifier, navigateToHome: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000L)
        navigateToHome.invoke()
    }
   // HideSystemBars()
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Image(painter = painterResource(Res.drawable.logo_splash),
            contentDescription = "",
            modifier=modifier.fillMaxSize(),
            contentScale = ContentScale.Crop)
    }
}



//@Composable
//fun HideSystemBars() {
//    val context = LocalContext.current
//
//    DisposableEffect(Unit) {
//        val window = context.findActivity()?.window ?: return@DisposableEffect onDispose {}
//        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
//        insetsController.apply {
//            hide(WindowInsetsCompat.Type.statusBars())
//            hide(WindowInsetsCompat.Type.navigationBars())
//            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        }
//
//        onDispose {
//            insetsController.apply {
//                show(WindowInsetsCompat.Type.statusBars())
//                show(WindowInsetsCompat.Type.navigationBars())
//                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
//            }
//        }
//    }
//}
//fun Context.findActivity(): Activity? {
//    var context = this
//    while (context is ContextWrapper) {
//        if (context is Activity) return context
//        context = context.baseContext
//    }
//    return null
//}