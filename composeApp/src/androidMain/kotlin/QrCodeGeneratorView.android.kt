import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lightspark.composeqr.QrCodeView


@Composable
actual fun PlatformQrCode(data: String, modifier: Modifier) = QrCodeView(data, modifier)