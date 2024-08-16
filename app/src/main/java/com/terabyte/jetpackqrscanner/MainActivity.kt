package com.terabyte.jetpackqrscanner

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.terabyte.jetpackqrscanner.data.MainDB
import com.terabyte.jetpackqrscanner.data.Product
import com.terabyte.jetpackqrscanner.ui.theme.JetpackQRScannerTheme
import com.terabyte.jetpackqrscanner.ui.theme.Purple80
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var mainDB: MainDB

    var counter = 0

    private val launcherScan = registerForActivityResult(
        ScanContract()
    ) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Nothing was scanned.", Toast.LENGTH_SHORT).show()
        }
        else {
            CoroutineScope(Dispatchers.IO).launch {
                val productByQR = mainDB.dao.getProductByQR(result.contents)
                if(productByQR==null) {
                    mainDB.dao.insertProduct(
                        Product(
                            null,
                            "Product ${counter++}",
                            result.contents,
                        )
                    )
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Saved!", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Item has been already scanned!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private val launcherCheckScan = registerForActivityResult(
        ScanContract()
    ) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Nothing was scanned.", Toast.LENGTH_SHORT).show()
        }
        else {
            CoroutineScope(Dispatchers.IO).launch {
                val productByQR = mainDB.dao.getProductByQR(result.contents)
                if(productByQR==null) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Product has not been added yet", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    mainDB.dao.updateProduct(
                        productByQR.copy(
                            isChecked = true
                        )
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val coroutineScope = rememberCoroutineScope()

            val productStateList = mainDB.dao.getAllProducts().collectAsState(initial = emptyList())
            JetpackQRScannerTheme {
                Scaffold { paddingVals ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = paddingVals.calculateTopPadding() + 10.dp,
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.8f)
                        ) {
                            items(productStateList.value) { product ->
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                )
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp, end = 10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if(product.isChecked) {
                                            Color.Blue
                                        } else {
                                            Purple80
                                        },
                                        contentColor = if(product.isChecked) {
                                            Purple80
                                        } else {
                                            Color.Blue
                                        }
                                    )
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(15.dp),
                                        text = product.name,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                        Button(
                            onClick = {
                                scan()
                            }
                        ) {
                            Text(
                                text = "Add new product"
                            )
                        }
                        Button(
                            onClick = {
                                scanCheck()
                            }
                        ) {
                            Text(
                                text = "Check product"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun scan() {
        launcherScan.launch(getScanOptions())
    }

    private fun scanCheck() {
        launcherCheckScan.launch(getScanOptions())
    }

    private fun getScanOptions(): ScanOptions {
        return ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt("Scan a QR code")
            setCameraId(0)
            setBeepEnabled(false)
            setBarcodeImageEnabled(true)
        }
    }
}

