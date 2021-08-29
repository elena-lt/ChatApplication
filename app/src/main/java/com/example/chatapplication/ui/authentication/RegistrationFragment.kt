package com.example.chatapplication.ui.authentication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.example.chatapplication.databinding.FragmentRegistrationBinding
import com.example.chatapplication.models.mapper.UserMapper
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
        handleOnClickEvents()
    }

    private fun handleOnClickEvents(){
        binding.tvAlreadyHaveAccount.setOnClickListener {
            binding.edtEmail.text?.clear()
            binding.edtPassword.text?.clear()
            binding.edtUsername.text?.clear()
            findNavController().popBackStack()
        }

        binding.btnRegister.setOnClickListener {
            signUp()
        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun subscribeToObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.dataState.collect {
                        onStateChangeListener.onDataStateChanged(it)
                        it.data?.let {
                            viewModel.setState(AuthenticationState.Success(UserMapper.toUser(it)))
                        }
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