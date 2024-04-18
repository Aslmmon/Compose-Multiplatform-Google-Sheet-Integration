import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformQrCode(data: String, modifier: Modifier) {
    Text(data)
}