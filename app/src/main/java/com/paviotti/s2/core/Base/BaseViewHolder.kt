package com.paviotti.s2.core.Base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/** é uma classe abstrata generica que podemos passar qualquer viewHolder (T)
 * ela retorna o item que queremos acessar no viewHolder
 * Ela estende viewHolder e passa a view itemView
 * ela recebe um itemView de RecyclerView.ViewHolder*/

abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T)
}