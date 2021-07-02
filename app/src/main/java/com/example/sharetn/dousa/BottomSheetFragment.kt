package com.example.sharetn.dousa

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.sharetn.EditActivity
import com.example.sharetn.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment(): BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.serch_bottom_sheet,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.kensaku).setOnClickListener{
            val intent = Intent(activity, EditActivity::class.java)
            startActivity(intent)
        }

    }
}