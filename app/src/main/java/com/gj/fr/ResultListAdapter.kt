package com.gj.fr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gj.arcoredraw.R
import kotlinx.android.synthetic.main.item_result.view.tv_sort
import kotlinx.android.synthetic.main.item_result.view.tv_type

class ResultListAdapter(result: List<FRBean>,
                        private val listener: (data: FRBean, dialog: ResultListDialog) -> Unit,
                        private val dialog: ResultListDialog
) : RecyclerView.Adapter<ResultListAdapter.ViewHolder>() {

    private var data = result

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_result, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int =data.size

    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        private val tvSort = itemView.tv_sort
        private val tvType = itemView.tv_type
        fun bind(item: FRBean){
            tvSort.text=item.sort
            tvType.text=item.type
            itemView.setOnClickListener {
                listener(item,dialog)
            }
        }
    }
}