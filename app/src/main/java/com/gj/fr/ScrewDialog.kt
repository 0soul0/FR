package com.gj.fr

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.gzuliyujiang.wheelview.widget.WheelView
import com.gj.arcoredraw.R


class ScrewDialog(
    val listener: (picker:WheelView) -> Unit = {},
    val dListener: () -> Unit = {}
) : DialogFragment() {


    val screwList = listOf("4","8","12","16","20","24","28","32","36","40")

    private val contentView by lazy {
        LayoutInflater.from(context).inflate(R.layout.dialog_screw, null)
    }

    private val wheel_view by lazy {
        contentView.findViewById(R.id.wheel_view) as WheelView
    }
    private val ll_body by lazy {
        contentView.findViewById(R.id.ll_body) as LinearLayout
    }

    private val alertDialog by lazy { createDialog() }

    @SuppressLint("SetTextI18n")
    private fun createDialog(): AlertDialog {
        val dialog = AlertDialog.Builder(requireContext(), R.style.dialog_center)
            .setView(contentView)
            .create()

        wheel_view.data=screwList
        listener(wheel_view)

        ll_body.setOnClickListener {
            dListener()
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