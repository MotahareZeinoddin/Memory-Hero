package ir.shariaty.memoryhero

import android.media.SoundPool
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import ir.shariaty.memoryhero.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),OnClickListener {
    private lateinit var binding : ActivityMainBinding
    private var score = 0
    private var result : String = ""
    private var userAnswer : String = ""
    private lateinit var soundPool: SoundPool
    private var soundIdPanelOn = 0
    private var soundIdPanelClick = 0
    private var soundIdWin = 0
    private var soundIdLose = 0

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        soundPool = SoundPool.Builder().setMaxStreams(5).build()
        soundIdPanelOn = soundPool.load(this, R.raw.onpanel,1)
        soundIdPanelClick = soundPool.load(this, R.raw.panelclick, 1)
        soundIdWin = soundPool.load(this, R.raw.win, 1)
        soundIdLose = soundPool.load(this, R.raw.lose, 1)


        // init Views
        binding.apply {
            panel1.setOnClickListener(this@MainActivity)
            panel2.setOnClickListener(this@MainActivity)
            panel3.setOnClickListener(this@MainActivity)
            panel4.setOnClickListener(this@MainActivity)
            startGame()
        }
    }

    fun playPanelOnSound() {
        soundPool.play(soundIdPanelOn, 1.0f, 1.0f, 1, 0, 1.0f)
    }
    fun playPanelClickSound() {
        soundPool.play(soundIdPanelClick, 1.0f, 1.0f, 1, 0, 1.0f)
    }
    fun playwinSound() {
        soundPool.play(soundIdWin, 1.0f, 1.0f, 1, 0, 1.0f)
    }
    fun playloseSound() {
        soundPool.play(soundIdLose, 1.0f, 1.0f, 1, 0, 1.0f)
    }



    private fun disableButtons(){
        binding.root.forEach { view ->
            if (view is AppCompatButton)
            {
                view.isEnabled = false
            }
        }
    }

    private fun enableButtons(){
        binding.root.forEach { view ->
            if (view is AppCompatButton)
            {
                view.isEnabled = true
            }
        }
    }

    private fun startGame(){
        result = ""
        userAnswer = ""
        disableButtons()
        lifecycleScope.launch {
            val round = (3 .. 5).random()
            repeat(round)
            {
                delay(400)
                val randomPanel = (1 .. 4).random()
                result += randomPanel
                var panel = when(randomPanel) {
                    1 -> binding.panel1
                    2 -> binding.panel2
                    3 -> binding.panel3
                    else -> binding.panel4
                }
                val drawbleYellow = ActivityCompat.getDrawable(this@MainActivity,
                    R.drawable.btn_yellow
                )
                val drawableDefault = ActivityCompat.getDrawable(this@MainActivity,R.drawable.btn_state)
                playPanelOnSound()
                panel.background = drawbleYellow
                delay(1000)
                panel.background = drawableDefault
            }
            enableButtons()
        }
    }
    private fun loseAnimation(){
        binding.apply {
            score = 0
            tvScore.text = "0"
            disableButtons()
            val drawbleLose = ActivityCompat.getDrawable(this@MainActivity,R.drawable.btn_lose)
            val drawableDefault = ActivityCompat.getDrawable(this@MainActivity,R.drawable.btn_state)
            lifecycleScope.launch {
                binding.root.forEach { view ->
                    if (view is AppCompatButton) {
                        view.background = drawbleLose
                        delay(300)
                        view.background = drawableDefault
                    }
                }
                delay(1000)
                startGame()
            }
        }
    }

    override fun onClick(view: View?) {
        view?.let { playPanelClickSound()
            userAnswer += when(it.id) {
                R.id.panel1 -> "1"
                R.id.panel2 -> "2"
                R.id.panel3 -> "3"
                R.id.panel4 -> "4"
                else -> ""

            }
            if (userAnswer == result){
                Toast.makeText(this@MainActivity, "W I N! 👌🍕",Toast.LENGTH_SHORT).show()
                score++
                binding.tvScore.text = score.toString()
                playwinSound()
                startGame()

            }
            else if  (userAnswer.length >= result.length)  {
                playloseSound()
                loseAnimation()
            }
        }
    }

}