package com.example.chatapplication.ui.authentication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.example.chatapplication.databinding.FragmentRegistrationBinding
import com.example.chatapplication.ui.authentication.mvi.AuthenticationStateEvent
import com.example.chatapplication.ui.authentication.mvi.AuthenticationState
import com.example.chatapplication.ui.base.BaseAuthFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
@InternalCoroutinesApi
class RegistrationFragment : BaseAuthFragment<FragmentRegistrationBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentRegistrationBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()

        binding.tvAlreadyHaveAccount.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnRegister.setOnClickListener {
            signUp()
        }
    }

    private fun subscribeToObservers(){
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.authState.collect {
                when (it){
                    is AuthenticationState.Success -> {
                        Log.d("AppDebug", "subscribeToObservers: success: ${it.user.email}")
                    }
                }
            }
        }
    }

    private fun signUp() {
        lifecycleScope.launch {
            viewModel.setIntent(
                AuthenticationStateEvent.SignUpUser(
                    binding.edtEmail.text.toString(),
                    binding.edtUsername.text.toString(),
                    binding.edtPassword.text.toString(),
                )
            )
        }
    }
}