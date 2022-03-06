package com.psg.programmersflo.view.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.psg.programmersflo.R
import com.psg.programmersflo.data.model.Lyrics
import com.psg.programmersflo.databinding.LyricsItemBinding

class MainAdapter(var list: List<Lyrics> = mutableListOf()):
    RecyclerView.Adapter<MainAdapter.MainHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder = (MainHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.lyrics_item, parent, false
        )
    ))


    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setData(items: List<Lyrics>){
        list = items
        notifyDataSetChanged()
    }

    fun setPlayingLyrics(index:Int){
        if (list.isNotEmpty()){
            list.forEach { it.playing = false }
            list[index].playing = true
            notifyDataSetChanged()
        }

    }

    fun resetLyrics(){
        list.forEach { it.playing = false }
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, data: Lyrics, pos: Int)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class MainHolder(private val binding: LyricsItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Lyrics) {
            binding.lyrics = item

            val pos = bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION){
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView,item,pos)
                }
            }
        }



    }
}