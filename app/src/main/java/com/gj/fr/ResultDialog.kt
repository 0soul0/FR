package com.gj.fr

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.gj.arcoredraw.R


class ResultDialog(
    private val result: FRBean,
    private val listener: (data:String) -> Unit
) : DialogFragment() {



    private val contentView by lazy {
        LayoutInflater.from(context).inflate(R.layout.activity_result, null)
    }

    private val itv_back by lazy {
        contentView.findViewById(R.id.itv_back) as ImageTextView
    }

//    private val view_fr by lazy {
//        contentView.findViewById(R.id.view_fr) as FrView
//    }

    private val tv_title by lazy {
        contentView.findViewById(R.id.tv_title) as TextView
    }

    private val tv_size by lazy {
        contentView.findViewById(R.id.tv_size) as TextView
    }

    private val tv_level by lazy {
        contentView.findViewById(R.id.tv_level) as TextView
    }

    private val tv_out by lazy {
        contentView.findViewById(R.id.tv_out) as TextView
    }

    private val tv_hole by lazy {
        contentView.findViewById(R.id.tv_hole) as TextView
    }

    private val tv_thiness by lazy {
        contentView.findViewById(R.id.tv_thiness) as TextView
    }

    private val tv_screw by lazy {
        contentView.findViewById(R.id.tv_screw) as TextView
    }

    private val tv_material by lazy {
        contentView.findViewById(R.id.tv_material) as TextView
    }

    private val tv_standard by lazy {
        contentView.findViewById(R.id.tv_standard) as TextView
    }

    private val itv_camera by lazy {
        contentView.findViewById(R.id.itv_camera) as ImageTextView
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
        window?.decorView?.setPadding(0,8,0,0)
        val lp = window?.attributes
        lp?.run { windowAnimations = R.style.BottomShowAnimation }
        return dialog
    }

    private fun bindEvent() {
        itv_back.setOnClickListener {
            dismiss()
        }
        tv_title.text = result.type
        tv_size.text=result.type
        tv_level.text=result.type
        tv_out.text=result.out+"mm"
        tv_hole.text=result.hole+"mm"
        tv_thiness.text=result.thickness+"mm"
        tv_screw.text=result.screw
        tv_material.text=result.type
        tv_standard.text=result.type
        itv_camera.setOnClickListener {
            listener("")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return alertDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}