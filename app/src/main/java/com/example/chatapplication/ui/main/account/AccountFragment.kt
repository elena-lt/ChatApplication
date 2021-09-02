package com.example.chatapplication.ui.main.account

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.example.chatapplication.databinding.FragmentAccountBinding
import com.example.chatapplication.models.Models
import com.example.chatapplication.ui.base.BaseFragment
import com.example.chatapplication.utils.Constants.GALLERY_REQUEST_CODE
import com.quickblox.auth.session.QBSessionManager
import com.quickblox.auth.session.QBSessionParameters
import com.quickblox.content.QBContent
import com.quickblox.content.model.QBFile
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.QBProgressCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.users.QBUsers
import com.quickblox.users.model.QBUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.*
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding>() {

    @Inject
    lateinit var qbSessionManager: QBSessionManager

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentAccountBinding::inflate

    val viewModel: AccountViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        handleOnClickEvents()
        subscribeToObservers()

    }

    private fun handleOnClickEvents() {
        binding.logout.setOnClickListener {
            logout()
        }

        binding.userProfileImage.setOnClickListener {
            intent()
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
                        Log.d("AppDebug", "subscribeToObservers: new data statet collected")
                        onStateChangeListener.onDataStateChanged(it)
                    }
                }

                launch {
                    viewModel.viewState.collect { viewState ->
                        viewState.user?.let {
                            Log.d("AppDebug", "subscribeToObservers: new view state collected")
                            updateProfileInfo(it)
                        }
                    }
                }
            }
        }
    }

    private fun updateProfileInfo(user: Models.User) {
        binding.textView.text = user.login
        binding.userProfileImage.setImageBitmap(user.blobId)
    }

    private fun intent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun changeProfileImage(imageUri: Uri) {
        try {
            val input = requireContext().contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(input)
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
            val file = File("${requireContext().getExternalFilesDir(null)}" + "img.png")
//            val file = File ("${Environment.getExternalStorageDirectory()}"+"img.png")
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