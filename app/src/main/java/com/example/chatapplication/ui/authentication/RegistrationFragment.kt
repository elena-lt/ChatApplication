package com.example.chatapplication.ui.authentication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
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
import com.example.chatapplication.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

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

    private fun handleOnClickEvents() {
        binding.tvAlreadyHaveAccount.setOnClickListener {
            binding.edtEmail.text?.clear()
            binding.edtPassword.text?.clear()
            binding.edtUsername.text?.clear()
            findNavController().popBackStack()
        }

        binding.btnRegister.setOnClickListener {
            signUp()
        }

        binding.profileImg.setOnClickListener {
            pickImage()
        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun subscribeToObservers() {
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


    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, Constants.GALLERY_REQUEST_CODE)
    }

    private var imageUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.GALLERY_REQUEST_CODE -> {
                    data?.data?.let {
                        binding.profileImg.setImageURI(it)
                        imageUri = it
                    } ?: Toast.makeText(requireContext(), "Cannot load image", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


    private fun signUp() {
        var imageFile: File? = null
        imageUri?.let {
            try {
                val input = requireContext().contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(input)
                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
                val file = File("${requireContext().getExternalFilesDir(null)}" + "img.png")
                val fos = FileOutputStream(file)
                fos.write(bos.toByteArray())
                fos.flush()
                fos.close()
                imageFile = file
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
        lifecycleScope.launch {
            viewModel.setIntent(
                AuthenticationStateEvent.SignUpUser(
                    binding.edtUsername.text.toString(),
                    binding.edtEmail.text.toString(),
                    binding.edtFullName.text.toString(),
                    binding.edtPassword.text.toString(),
                    binding.edtConfirmPassword.text.toString(),
                    imageFile
                )
            )
        }
    }
}