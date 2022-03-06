package com.psg.programmersflo.view.main

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.psg.programmersflo.R
import com.psg.programmersflo.data.model.Lyrics
import com.psg.programmersflo.databinding.ActivityMainBinding
import com.psg.programmersflo.view.main.adapter.MainAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by inject()
    private lateinit var binding: ActivityMainBinding
    private val adapter = MainAdapter()
    private var player = MediaPlayer()
    private var lyricsList  = mutableListOf<Lyrics>()
    private var fileUrl = ""
    private var playingIndex = 0
    private var isLyrics = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = viewModel
        binding.activity = this
        binding.lifecycleOwner = this
        initRv()
        initLyrics()
        setLyricsView()


        viewModel.music.observe(this, {
            if (it != null){
                initPlayer(it.file)
                fileUrl = it.file
            }
        })

        viewModel.lyrics.observe(this, {
            if (it != null){
                lyricsList = it as MutableList<Lyrics>
                adapter.setData(lyricsList)
            }
        })

        viewModel.playingIndex.observe(this, {
            if (it != null){
                playingIndex = it
                adapter.setPlayingLyrics(it)
            }
        })


    }

    // 어댑터 초기설정
    private fun initRv(){
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        adapter.setHasStableIds(true)

        binding.rvLyrics.layoutManager = layoutManager
        binding.rvLyrics.setHasFixedSize(true)
        binding.rvLyrics.adapter = adapter

        adapter.setOnItemClickListener(object : MainAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: Lyrics, pos: Int) {
                setPlayingView(lyricsList[pos].time)
                player.seekTo(lyricsList[pos].time * 1000)

            }

        })

    }

    // 미디어 플레이어 초기설정
    private fun initPlayer(url: String){
        player.stop()
        player.release()

        player = MediaPlayer().apply {
            setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
            setDataSource(url)
            prepare()
        }
        viewModel.setLyricsEndTime(player.duration/1000)
        viewModel.setMaxProgress(player.duration / 1000)

        binding.sbPlayTime.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player.seekTo(progress * 1000)
                    setPlayingView(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    viewModel.setPlayingIndex(seekBar.progress)
                    SystemClock.sleep(200)
                    if (isLyrics) binding.rvLyrics.smoothScrollToPosition(playingIndex)
                }
            }
        })

    }

    private fun initLyrics(){
        viewModel.music.observe(this, {
            if (it != null){
                viewModel.setLyricsList(it.lyrics)
            }
        })
    }


    // 가사보기
    private fun setLyricsView(){
        viewModel.isLyrics.observe(this,{
            if (it){
                binding.llName.visibility = GONE
                binding.cardView.visibility = GONE
                binding.tvLyrics.visibility = GONE
                binding.rvLyrics.visibility = VISIBLE
                isLyrics = true
            } else {
                binding.llName.visibility = VISIBLE
                binding.cardView.visibility = VISIBLE
                binding.tvLyrics.visibility = VISIBLE
                binding.rvLyrics.visibility = GONE
                isLyrics = false
            }
        })

    }


    fun lyricsEvent(){
        viewModel.setIsLyrics()
        binding.rvLyrics.smoothScrollToPosition(playingIndex)
    }


    // 음악 재생정지
     fun playPauseEvent(){
        if (player.isPlaying){
            pause()
            setPlayingView(player.currentPosition / 1000)
        }else{
            play()
            CoroutineScope(Dispatchers.Default).launch {
                while (player.isPlaying){
                    if (binding.sbPlayTime.max <= player.currentPosition/1000) {
                        CoroutineScope(Dispatchers.Main).launch {
                            adapter.resetLyrics()
                            pause()
                            initPlayer(fileUrl)
                        }
                        break
                    }
                    SystemClock.sleep(200)
                    setPlayingView(player.currentPosition / 1000)

                }
            }

        }
    }

    // 재생중인 뷰 정보 변경
    private fun setPlayingView(position: Int){
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.setPlayingIndex(position)
            viewModel.setPlayingProgress(position)
            viewModel.setPLayingLyrics(position)
        }
    }


    private fun pause(){
        binding.ivPlayPause.setImageResource(R.drawable.ic_baseline_play_24)
        player.pause()
    }

    private fun play(){
        binding.ivPlayPause.setImageResource(R.drawable.ic_baseline_pause_24)
        player.start()
    }




}