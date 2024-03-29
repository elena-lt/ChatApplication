package com.example.chatapplication.ui.main.account

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.example.chatapplication.databinding.FragmentAccountBinding
import com.example.chatapplication.ui.base.BaseFragment
import com.example.chatapplication.ui.main.account.mvi.AccountStateEvent
import com.example.chatapplication.utils.Constants.GALLERY_REQUEST_CODE
import com.quickblox.auth.session.QBSessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding>() {

    @Inject
    lateinit var qbSessionManager: QBSessionManager

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentAccountBinding::inflate

    val viewModel: AccountViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        loadAccountProperties()
        handleOnClickEvents()
        subscribeToObservers()

    }

    private fun handleOnClickEvents() {
        binding.logout.setOnClickListener {
            logout()
        }

        binding.changeProfileImg.setOnClickListener {
            intent()
        }

        binding.editProfile.setOnClickListener {
            setEditableMode(true)
        }

        binding.btnSubmitChanges.setOnClickListener{
            applyAccountChanges()
        }

        binding.btnDissmiss.setOnClickListener {
            setEditableMode(false)
        }
    }

    private fun setEditableMode(editableMode: Boolean) {
        val email = binding.userEmail.text.toString()
        val login = binding.userLogin.text.toString()

        binding.userLogin.isVisible = !editableMode
        binding.userEmail.isVisible = !editableMode
        binding.editProfile.isVisible = !editableMode

        binding.btnSubmitChanges.isVisible = editableMode
        binding.edtEmailAddress.isVisible = editableMode
        binding.edtLogin.isVisible = editableMode
        binding.btnDissmiss.isVisible = editableMode

        if (editableMode){
            binding.edtLogin.setText(login)
            binding.edtEmailAddress.setText(email)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    data?.data?.let {
//                        binding.userProfileImage.setImageURI(it)
                        changeProfileImage(it)
                    } ?: Toast.makeText(requireContext(), "Cannot load image", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun subscribeToObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.dataState.collect {
                        onStateChangeListener.onDataStateChanged(it)
                    }
                }

                launch {
                    viewModel.viewState.collect { viewState ->
                        viewState.user?.let {
                            binding.currUser = it
                        }
                    }
                }
            }
        }
    }

    private fun loadAccountProperties() {
        viewModel.setStateEvent(AccountStateEvent.LoadAccountProperties)
    }

    private fun applyAccountChanges  (){

    }

    private fun intent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun changeProfileImage(imageUri: Uri) {
        try {
            val input = requireContext().contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(input)
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
            val file = File("${requireContext().getExternalFilesDir(null)}" + "img.png")
            val fos = FileOutputStream(file)
            fos.write(bos.toByteArray())
            fos.flush()
            fos.close()

            viewModel.changeProfileImage(file)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun logout() {
        viewModel.logout()
    }
}