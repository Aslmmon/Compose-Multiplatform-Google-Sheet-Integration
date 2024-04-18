import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
expect fun PlatformQrCode(data: String, modifier: Modifier)
