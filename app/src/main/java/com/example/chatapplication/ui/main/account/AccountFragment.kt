package com.example.chatapplication.ui.main.account

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.example.chatapplication.R
import com.example.chatapplication.databinding.FragmentAccountBinding
import com.example.chatapplication.ui.base.BaseFragment
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.users.QBUsers

class AccountFragment : BaseFragment<FragmentAccountBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentAccountBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.logout.setOnClickListener {
            logout()
        }

    }

    private fun logout(){
        QBUsers.signOut().performAsync(object : QBEntityCallback<Void>{
            override fun onSuccess(p0: Void?, p1: Bundle?) {
                Log.d("AppDeubg", "onSuccess: loged out")
            }

            override fun onError(p0: QBResponseException?) {
                Log.d("AppDeubg", "onError: not logged out.....")
            }

        })
    }
}