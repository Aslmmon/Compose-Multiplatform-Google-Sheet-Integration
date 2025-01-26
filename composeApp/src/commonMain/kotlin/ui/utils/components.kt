package ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upwork.googlesheetreader.ui.postData.PlayerData
import io.github.alexzhirkevich.qrose.rememberQrCodePainter


@Composable
fun PlayerDataRow(
    modifier: Modifier = Modifier, playerData: PlayerData,
    onStatusChange: (PlayerData, String) -> Unit,
    openDialogChangeStatus: () -> Unit,
    qrCodeData: Array<String>,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = try {
                    "${playerData.firstName} ${playerData.secondName}"
                } catch (e: Exception) {
                    "exception"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            Text(
                "sheet : ${playerData.spreadSheetName}",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
//
//        if (playerData.isCaptured.isNotEmpty()) {
//            if (playerData.isCaptured.contains("FALSE", true)) {
//                Icon(
//                    Icons.Rounded.Cancel,
//                    contentDescription = "",
//                    tint = Color.Red,
//                    modifier = modifier
//                        .size(20.dp)
//                        .weight(0.5f)
//                        .clickable {
//                            onStatusChange(playerData, "TRUE")
//                            openDialogChangeStatus()
//                        }
//                )
//            } else {
//                Icon(
//                    Icons.Rounded.CheckCircle,
//                    contentDescription = "",
//                    tint = Color.DarkGray,
//                    modifier = modifier
//                        .size(20.dp)
//                        .weight(0.5f)
//                        .clickable {
//                            onStatusChange(playerData, "FALSE")
//                            openDialogChangeStatus()
//                        }
//                )
//            }
//        }
        QRcodePlayer(
            modifier = modifier.fillMaxWidth(), data = qrCodeData
        )
    }

}

@Composable
fun QRcodePlayer(modifier: Modifier, vararg data: String) {
    Image(
        modifier = modifier,
        painter = rememberQrCodePainter(
            data.joinToString()
        ),
        contentDescription = "QR code referring to the playerData"
    )
}
