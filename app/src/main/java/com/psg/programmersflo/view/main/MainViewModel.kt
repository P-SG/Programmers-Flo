package com.psg.programmersflo.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.psg.programmersflo.data.model.Lyrics
import com.psg.programmersflo.data.model.Music
import com.psg.programmersflo.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val repository: AppRepository): ViewModel() {

    val music: LiveData<Music> get() = _music
    private var _music = MutableLiveData<Music>()

    // 현재 재생중인 가사 객체
    val lyrics: LiveData<List<Lyrics>> get() = _lyrics
    private var _lyrics = MutableLiveData<List<Lyrics>>()

    // 현재 재생중인 음악의 길이
    val lyricsEndTime: LiveData<String> get() = _lyricsEndTime
    private var _lyricsEndTime = MutableLiveData("")

    // 현재 가사 재생중인지
    val isLyrics: LiveData<Boolean> get() = _isLyrics
    private var _isLyrics = MutableLiveData(false)

    // 현재 재생중인 가사의 인덱스
    val playingIndex: LiveData<Int> get() = _playingIndex
    private var _playingIndex = MutableLiveData(0)

    // 현재 재생중 시간
    val playingLyricsTime: LiveData<String> get() = _playingLyricsTime
    private var _playingLyricsTime = MutableLiveData("")

    // 프로그래스바 최대 길이
    val maxProgress: LiveData<Int> get() = _maxProgress
    private var _maxProgress = MutableLiveData(0)

    // 현재 재생중 프로그래스바 위치
    val playingProgress: LiveData<Int> get() = _playingProgress
    private var _playingProgress = MutableLiveData(0)


    init {
        searchMusic()
    }

    private fun searchMusic(){
        var musicRes: Music
        viewModelScope.launch {
            withContext(Dispatchers.Default){
                musicRes = repository.getMusic().body()!!
            }
            _music.value = musicRes
        }
    }

    fun setIsLyrics(){
        _isLyrics.value = _isLyrics.value == false
    }

    fun setLyricsList(lyrics: String) {
        val list = lyrics.split("\n")
        val lyricsList = mutableListOf<Lyrics>()
        list.forEach { lyricsList.add(Lyrics(it.substring(11),parseTimeToSec(it.substring(1,6)),false)) }
        _lyrics.value = lyricsList
    }

    fun setLyricsEndTime(endTime: Int){
        _lyricsEndTime.value = parseTimeToStr(endTime)
    }

    fun setMaxProgress(progress: Int){
        _maxProgress.value = progress
    }

    // 현재 재생중인 가사 시간 세팅
    fun setPLayingLyrics(time: Int) {
        _playingLyricsTime.value = parseTimeToStr(time)
    }

    // 현재 재생중인 프로그래스바 위치 세팅
    fun setPlayingProgress(progress: Int){
        _playingProgress.value = progress
    }

    //40
    fun setPlayingIndex(time: Int){
        val list = lyrics.value
        val result: Int
        for (i in list!!.indices){
            if (list[i].time > time){
                result = if (i > 0) i - 1 else i
                _playingIndex.value = result
                break
            }
        }
    }


    private fun parseTimeToStr(time: Int):String {
        var result = ""
        when {
            time > 59 -> {
                result += if ((time / 60) > 9) "${(time / 60)}:" else "0${(time / 60)}:"
                result += if (time % 60 > 9) time % 60 else "0${time % 60}"
            }
            time > 9 -> {
                result = "00:$time"
            }
            else -> {
                result = "00:0$time"
            }
        }
        return result
    }


    // 01:12
    private fun parseTimeToSec(duration:String): Int{
        var result = 0
        val min = duration.substring(0,duration.indexOf(":")).toInt()
        val sec = duration.substring(duration.indexOf(":")+1).toInt()
        if (min > 0) result += min * 60
        result += sec
        return result
    }

}