package com.example.praktikum_modul1_xml

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var isRolling = false

    private lateinit var btnRoll: Button
    private lateinit var tvResultMessage: TextView
    private lateinit var imgDice1: ImageView
    private lateinit var imgDice2: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRoll = findViewById(R.id.btnRoll)
        tvResultMessage = findViewById(R.id.tvResultMessage)
        imgDice1 = findViewById(R.id.imgDice1)
        imgDice2 = findViewById(R.id.imgDice2)

        btnRoll.setOnClickListener {
            if (!isRolling) {
                rollDice()
            }
        }
    }

    private fun rollDice() {
        isRolling = true

        btnRoll.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

        tvResultMessage.visibility = View.INVISIBLE

        mediaPlayer?.release()

        imgDice1.animate().rotationBy(360f).setDuration(500).start()
        imgDice2.animate().rotationBy(360f).setDuration(500).start()

        lifecycleScope.launch {
            repeat(10) {
                val temp1 = Random.nextInt(1, 7)
                val temp2 = Random.nextInt(1, 7)
                imgDice1.setImageResource(getDiceResource(temp1))
                imgDice2.setImageResource(getDiceResource(temp2))
                delay(50)
            }

            val finalDice1 = Random.nextInt(1, 7)
            val finalDice2 = Random.nextInt(1, 7)

            imgDice1.setImageResource(getDiceResource(finalDice1))
            imgDice2.setImageResource(getDiceResource(finalDice2))

            showResult(finalDice1, finalDice2)
            isRolling = false
        }
    }

    private fun showResult(d1: Int, d2: Int) {
        tvResultMessage.visibility = View.VISIBLE

        if (d1 == d2) {
            tvResultMessage.text = "Selamat, Anda dapat dadu double!"
            tvResultMessage.setTextColor(Color.parseColor("#46953D"))
            mediaPlayer = MediaPlayer.create(this, R.raw.faaah)
        } else {
            tvResultMessage.text = "Anda belum Beruntung!\nCoba lagi."
            tvResultMessage.setTextColor(Color.parseColor("#7F0303"))
            mediaPlayer = MediaPlayer.create(this, R.raw.sad_violin)
        }

        mediaPlayer?.start()
    }

    private fun getDiceResource(diceValue: Int): Int {
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

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
