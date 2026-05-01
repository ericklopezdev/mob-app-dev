package com.app.exwk06

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.exwk06.ui.theme.Exwk06Theme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Exwk06Theme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun MainScreen(
    onNavigateToNoStockScreen: () -> Unit = {},
    viewModel: StockState = viewModel(),
) {

    Column() {
        Text(text = "Main")
        Divider()
        Text(text = "${viewModel.cantidadEstado}")

        Button(
            enabled = true,
            onClick = {viewModel.cantidadEstado -= 1}
        ) {
            Text(text = "Vender Unidad")

            if (viewModel.cantidadEstado <= 0){
                onNavigateToNoStockScreen()
            }
        }
    }




}

class StockState: ViewModel() {
    var cantidadEstado by mutableStateOf(5)
}

@Composable
fun NoStockScreen (
    onBack: () -> Unit = {},
) {
    Column() {
        Text(text = "Agotado")
        Divider()
        Text(
            text = "No quedan existencias disponibles"
        )
        Button(
            onClick = onBack
        ) {
            Text(text = "Volvamooo")
        }
    }

}

