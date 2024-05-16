package com.example.ecommercestore.dialoge

import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.ecommercestore.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.setupBottomSheetDialoge(
    onSendClick:(String)->Unit
){
    val dialoge = BottomSheetDialog(requireContext(),R.style.DialogeStyle)
    val view = LayoutInflater.from(requireContext()).inflate(R.layout.reset_password_dialoge,null)
    dialoge.setContentView(view)
    dialoge.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialoge.show()

    val email = view.findViewById<EditText>(R.id.edResetPassword)
    val buttonSend = view.findViewById<Button>(R.id.buttonSendResetPassword)
    val buttonCancel = view.findViewById<Button>(R.id.buttonCancelResetPassword)

    buttonSend.setOnClickListener{
        onSendClick(email.text.toString().trim())
        dialoge.dismiss()
    }
    buttonCancel.setOnClickListener{
        dialoge.dismiss()
    }
}