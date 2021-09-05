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
import com.example.chatapplication.R
import com.example.chatapplication.databinding.FragmentLoginBinding
import com.example.chatapplication.models.mapper.UserMapper
import com.example.chatapplication.ui.authentication.mvi.AuthenticationState
import com.example.chatapplication.ui.authentication.mvi.AuthenticationStateEvent
import com.example.chatapplication.ui.base.BaseAuthFragment
import com.example.chatapplication.utils.Constants.TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@AndroidEntryPoint
class LoginFragment : BaseAuthFragment<FragmentLoginBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLoginBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleOnClickEvents()
        subscribeToEvents()
    }

    private fun handleOnClickEvents() {
        binding.btnLogin.setOnClickListener {
            viewModel.setIntent(
                AuthenticationStateEvent.LoginUser(
                    binding.edtEmail.text.toString(),
                    binding.edtPassword.text.toString()
                )
            )
        }

        binding.tvDontHaveAccount.setOnClickListener {
            binding.edtPassword.text?.clear()
            binding.edtEmail.text?.clear()
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun subscribeToEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.dataState.collect {
                        onStateChangeListener.onDataStateChanged(it)
                        it.data?.let {user ->
                            viewModel.setState(
                                AuthenticationState(
                                    success = AuthenticationState.Success(
                                        user = UserMapper.toUser(user)
                                    )
                                )
                            )
                        }
                    }
                }

                launch {
                    viewModel.authState.collect { authState ->
                        authState.error?.errorMessage?.let { errorMsg ->
                            binding.tvErrorMessage.visibility = View.VISIBLE
                            binding.tvErrorMessage.text = errorMsg
                        }
                    }
                }
            }
        }
    }
}