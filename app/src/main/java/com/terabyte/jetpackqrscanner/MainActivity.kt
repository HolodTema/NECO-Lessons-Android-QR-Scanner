package com.terabyte.jetpackqrscanner

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.terabyte.jetpackqrscanner.ui.theme.JetpackQRScannerTheme

class MainActivity : ComponentActivity() {
    private val launcherScan = registerForActivityResult(
        ScanContract()
    ) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Nothing was scanned.", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this, "Scan data: ${result.contents}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetpackQRScannerTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            scan()
                        }
                    ) {
                        Text("Scan new QR")
                    }
                }
            }
        }
    }

    private fun scan() {
        val scanOptions = ScanOptions()
        scanOptions.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        scanOptions.setPrompt("Scan a QR code")
        scanOptions.setCameraId(0)
        scanOptions.setBeepEnabled(false)
        scanOptions.setBarcodeImageEnabled(true)

        launcherScan.launch(scanOptions)
    }
}

