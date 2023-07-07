package com.gj.fr

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gj.arcoredraw.R


class ResultListDialog(
    private val result: List<FRBean>,
    private val listener: (data:FRBean,dialog:ResultListDialog) -> Unit
) : DialogFragment() {



    private val contentView by lazy {
        LayoutInflater.from(context).inflate(R.layout.dialog_result_list, null)
    }

    private val rvList by lazy {
        contentView.findViewById(R.id.rv_list) as RecyclerView
    }

    private val alertDialog by lazy { createDialog() }

    @SuppressLint("SetTextI18n")
    private fun createDialog(): AlertDialog {
        val dialog = AlertDialog.Builder(requireContext(), R.style.dialog_center)
            .setView(contentView)
            .create()

        rvList.apply {
            adapter=ResultListAdapter(result,listener,this@ResultListDialog)
            layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        }

        val window = dialog.window
        window?.setDimAmount(0f)
        val lp = window?.attributes
        lp?.run { windowAnimations = R.style.BottomShowAnimation }
        return dialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return alertDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}