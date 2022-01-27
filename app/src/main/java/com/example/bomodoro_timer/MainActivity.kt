package com.example.bomodoro_timer

import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val minTime : TextView by lazy {
        findViewById(R.id.minTime)
    }
    private val seekBar : SeekBar by lazy {
        findViewById(R.id.seekBar)
    }
    private val secondTime : TextView by lazy {
        findViewById(R.id.secondTime)
    }
    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null

    private var currentCountDownTimer:CountDownTimer? = null // 중간에 멈추고 다시 설정하기위한 것
    private val soundPool = SoundPool.Builder().build()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        initSound()
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
    private fun bindViews(){
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                    minTime.text = "$p1"  이러면 한자리숫자가 1,2,3 이런식으로 나온다 우리가 원하는건 01,02,03..
//                    minTime.text = "%02d".format(progress)
                    if (fromUser) {
                        updateRemainTime(progress * 60 * 1000L)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    currentCountDownTimer?.cancel()
                    currentCountDownTimer = null
                    soundPool.autoPause()

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar ?: return
                    if(seekBar.progress == 0){
                        currentCountDownTimer?.cancel()
                        currentCountDownTimer = null
                        soundPool.autoPause()
                    }else{
                        currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L).start()
                        currentCountDownTimer?.start()
                        tickingSoundId?.let{ soundId ->
                            soundPool.play(soundId, 1F, 1F, 0 , -1, 1F)
                        }

                    }
                }
            }

        )
    }
    private fun createCountDownTimer(initalMilliSecond :Long) =
        object : CountDownTimer(initalMilliSecond, 1000L){
            override fun onTick(p0: Long) {
                //TODO("Not yet implemented")
                updateRemainTime(p0)
                updateSeekBar(p0)
            }

            override fun onFinish() {
                //TODO("Not yet implemented")
                updateSeekBar(0)
                updateRemainTime(0)
                soundPool.autoPause()
                bellSoundId?.let { soundId ->
                    soundPool.play(soundId, 1F, 1F, 0, 0, 1F)
                }
            }
        }
    private fun initSound(){
        tickingSoundId = soundPool.load(this,R.raw.timer_ticking,1) //id로 반환
        bellSoundId = soundPool.load(this,R.raw.timer_bell,1)
    }

    private fun updateRemainTime(millsecond: Long){ // 남은시간 계산
            val remainSecond = millsecond / 1000

            minTime.text = "%02d'".format(remainSecond/60)
            secondTime.text = "%02d".format(remainSecond % 60)
    }
    private fun updateSeekBar(millsecond: Long){ // 남은시간을 seekBar 로 표시하기위함
            seekBar.progress = (millsecond / 1000 / 60).toInt()
    }
}