package com.gj.fr

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.gj.arcoredraw.R


class SaveDialog(
) : DialogFragment() {



    private val contentView by lazy {
        LayoutInflater.from(context).inflate(R.layout.dialog_save, null)
    }

    private val tv_close by lazy {
        contentView.findViewById(R.id.tv_close) as TextView
    }

    private val alertDialog by lazy { createDialog() }

    @SuppressLint("SetTextI18n")
    private fun createDialog(): AlertDialog {
        val dialog = AlertDialog.Builder(requireContext(), R.style.dialog_center)
            .setView(contentView)
            .create()

        bindEvent()


        val window = dialog.window
        window?.setDimAmount(0f)
        val lp = window?.attributes
        lp?.run { windowAnimations = R.style.BottomShowAnimation }
        return dialog
    }

    private fun bindEvent() {
        tv_close.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return alertDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}