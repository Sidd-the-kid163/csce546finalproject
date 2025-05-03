package com.example.acccomparison.ui

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.acccomparison.R
import java.util.LinkedList
import java.util.Queue
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.LiveData
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.acccomparison.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Composable
fun GameScreen(
    gameViewModel: GameViewModel = viewModel(),
    onButtonClick1: () -> Unit,
    onButtonClick2: () -> Unit,
    onButtonClick3: () -> Unit,
    accStatus: Boolean,
    excellents: Int,
    goods: Int,
    oks: Int,
    totals: Int,
    color: Color
    //messageLiveData: LiveData<GameUiState>
) {
    //val gameUiState by gameViewModel.uiState.collectAsStateWithLifecycle()
    val mediumPadding = dimensionResource(R.dimen.medpad)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding()
            .padding(mediumPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 50.dp)
                .background(Color.Red, shape = RoundedCornerShape(12.dp))
        )
        {
            Text(
                text = "Just Naach",
                style = typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
                textAlign = TextAlign.Center,
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onButtonClick1()
                //gameViewModel.truefalse()
            }
        ) {
            Text(
                text = "Start new",
                fontSize = 16.sp
            )
        }

        //change2 = gameUiState.change2
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onButtonClick3()
                //gameViewModel.truefalse()
            }
        ) {
            Text(
                text = "Compare",
                fontSize = 16.sp
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onButtonClick2,
        ) {
            Text(
                text = "Load file",
                fontSize = 16.sp
            )
        }

        //GameStatus(accStatus = gameUiState.accStatus, modifier = Modifier.padding(20.dp))
        GameStatus(viewModel = gameViewModel, accStatus = accStatus, excellents = excellents, goods = goods, oks = oks, totals = totals, color = color)
    }
}

@Composable
fun GameStatus(viewModel: GameViewModel, accStatus: Boolean, excellents: Int, goods: Int, oks: Int, totals: Int, color: Color) {
    //val message by viewModel.acc.observeAsState(GameUiState(accStatus = false, excellents = 0, goods = 0, oks = 0, totals = 0, color  = Color(0xFF6200EE)))
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xffffffff)),
        modifier = Modifier
            //.fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 50.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                .fillMaxSize()
        )
        {
            Text(
                text = accStatus.toString(),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
                textAlign = TextAlign.Center,
                style = typography.headlineMedium
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                //.fillMaxSize()
                .background(color, shape = RoundedCornerShape(16.dp)),
                //.padding(start = 0.dp, top = 30.dp, end = 0.dp, bottom = 30.dp),
        )
        {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                    //.background(color, shape = RoundedCornerShape(16.dp)),
                        //horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medpad)),
                horizontalArrangement = Arrangement.Center,
                //verticalAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Excellent",
                        style = typography.titleLarge,

                        )
                    Text(
                        text = excellents.toString(),
                        style = typography.titleLarge,
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Good",
                        style = typography.titleLarge,
                    )
                    Text(
                        text = goods.toString(),
                        style = typography.titleLarge,
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Ok",
                        style = typography.titleLarge,
                    )
                    Text(
                        text = oks.toString(),
                        style = typography.titleLarge,
                    )
                }
            }
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                    //.background(message.color, shape = RoundedCornerShape(16.dp)),
                //horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medpad)),
                horizontalArrangement = Arrangement.Center,
                //verticalAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Total",
                        style = typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = totals.toString(),
                        style = typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}














