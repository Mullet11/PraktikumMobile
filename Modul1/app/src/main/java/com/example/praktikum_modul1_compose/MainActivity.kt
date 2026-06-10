package com.example.praktikum_modul1_compose

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.praktikum_modul1_compose.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Main
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DiceRollerApp()
                }
            }
        }
    }
}

// UI Composable
@Composable
fun DiceRollerApp() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    var dice1 by remember { mutableIntStateOf(0) }
    var dice2 by remember { mutableIntStateOf(0) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    var isRolling by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    var isWin by remember { mutableStateOf(false) }

    var rotationAngle by remember { mutableFloatStateOf(0f) }

    val animatedRotation by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = tween(
            durationMillis = DiceConfig.SHUFFLE_DURATION_MILLIS.toInt(),
            easing = FastOutSlowInEasing
        ),
        label = "dice_spin"
    )

    val dice1Resource = getDiceResource(dice1)
    val dice2Resource = getDiceResource(dice2)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.casino_bg),
            contentDescription = stringResource(R.string.bg_casino_description),
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AnimatedVisibility(
                visible = resultMessage.isNotEmpty() && !isRolling,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Text(
                    text = resultMessage,
                    color = if (isWin) WinGreen else LossRed,
                    fontSize = FontSizeLarge,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = PaddingLarge)
                        .background(TranslucentBlack, RoundedCornerShape(CornerRadiusMedium))
                        .padding(PaddingMedium)
                )
            }

            // Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = dice1Resource),
                    contentDescription = stringResource(R.string.dice_1_description),
                    modifier = Modifier
                        .size(DiceSize)
                        .rotate(animatedRotation)
                        .shadow(ShadowElevation, RoundedCornerShape(CornerRadiusLarge))
                )
                Spacer(modifier = Modifier.width(DiceSpacing))
                Image(
                    painter = painterResource(id = dice2Resource),
                    contentDescription = stringResource(R.string.dice_2_description),
                    modifier = Modifier
                        .size(DiceSize)
                        .rotate(animatedRotation)
                        .shadow(ShadowElevation, RoundedCornerShape(CornerRadiusLarge))
                )
            }

            Spacer(modifier = Modifier.height(PaddingExtraLarge))

            Button(
                onClick = {
                    if (isRolling) return@Button

                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                    isRolling = true
                    resultMessage = ""
                    rotationAngle += DiceConfig.ROTATION_INCREMENT

                    mediaPlayer?.stop()
                    mediaPlayer?.release()

                    coroutineScope.launch {

                        for (i in 1..DiceConfig.SHUFFLE_LOOP_COUNT) {
                            dice1 = (1..6).random()
                            dice2 = (1..6).random()
                            delay(DiceConfig.SHUFFLE_STEP_MILLIS)
                        }

                        dice1 = (1..6).random()
                        dice2 = (1..6).random()
                        isRolling = false

                        if (dice1 == dice2) {
                            isWin = true
                            resultMessage = context.getString(R.string.jackpot_message)
                            mediaPlayer = MediaPlayer.create(context, R.raw.faaah)
                        } else {
                            isWin = false
                            resultMessage = context.getString(R.string.try_again_message)
                            mediaPlayer = MediaPlayer.create(context, R.raw.sad_violin)
                        }
                        mediaPlayer?.start()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ButtonGold),
                shape = RoundedCornerShape(CornerRadiusLarge),
                modifier = Modifier
                    .width(ButtonWidth)
                    .height(ButtonHeight)
                    .shadow(ShadowElevation, RoundedCornerShape(CornerRadiusLarge))
            ) {
                Text(
                    text = stringResource(R.string.roll_button_text),
                    color = Color.Black,
                    fontSize = FontSizeButton,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun getDiceResource(diceValue: Int): Int {
    return when (diceValue) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> R.drawable.dice_0
    }
}
