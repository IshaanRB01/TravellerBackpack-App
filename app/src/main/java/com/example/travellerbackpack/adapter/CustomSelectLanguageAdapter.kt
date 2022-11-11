package com.example.travellerbackpack.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travellerbackpack.modal.Language
import com.example.travellerbackpack.databinding.ItemCustomTranslateListBinding

class CustomSelectLanguageAdapter(
    private val context: Context,
    private val listItems: List<Language>,
    private val listener: ISelectItem
) : RecyclerView.Adapter<CustomSelectLanguageAdapter.ViewHolder>() {

    class ViewHolder(view: ItemCustomTranslateListBinding) : RecyclerView.ViewHolder(view.root) {

        val rvItemText = view.rvItemText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCustomTranslateListBinding =
            ItemCustomTranslateListBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItems[position]
        holder.rvItemText.text = item.languageString

        holder.itemView.setOnClickListener {
            listener.selectedListItem(item)
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

}

interface ISelectItem {
    fun selectedListItem(item: Language)
}