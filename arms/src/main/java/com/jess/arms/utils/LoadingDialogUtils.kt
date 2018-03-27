package com.jess.arms.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.jess.arms.R
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialogBuilder

@SuppressLint("StaticFieldLeak")
/**
 * Created by chan on 2018/1/25.
 */
object LoadingDialogUtils {
    private var dialog: QMUIDialog? = null
    private var mContext: Context? = null

    fun show(context: Context) {
        try {
            if (mContext === null || mContext !== context || dialog === null) {
                mContext = context
                dialog = mContext?.let { LoadingDialog(it).create() }
            }
            if (!dialog!!.isShowing || !(mContext as Activity).isFinishing) {
                dialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismiss() {
        if (dialog == null) {
            return
        }
        if (dialog!!.isShowing || !(mContext as Activity).isFinishing) {
            dialog!!.dismiss()
        }
    }

    fun clear() {
        dismiss()
        dialog = null
        mContext = null
    }
}

/**
 * 网络请求loading
 */
class LoadingDialog(context: Context) : QMUIDialogBuilder<LoadingDialog>(context) {
    override fun onCreateContent(dialog: QMUIDialog, parent: ViewGroup) {
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        parent.layoutParams = lp
        val view = LayoutInflater.from(mContext).inflate(R.layout.dialog_loading, parent, false)
//        val imageView = view.findViewById<ImageView>(R.id.progressbar)
//        Glide.with(mContext).asGif().load(R.drawable.ic_refresh_gif).into(imageView)
        parent.addView(view)
    }
}