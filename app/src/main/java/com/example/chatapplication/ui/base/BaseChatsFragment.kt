package com.example.chatapplication.ui.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import com.example.chatapplication.ui.OnDataStateChangeListener
import com.example.chatapplication.ui.main.chats.ChatsViewModel
import com.example.chatapplication.utils.Constants
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
abstract class BaseChatsFragment <out T: ViewBinding> : Fragment() {

    private var _binding: ViewBinding? = null

    protected val binding: T
        get() = _binding as T

    val viewModel: ChatsViewModel by activityViewModels()

    lateinit var onStateChangeListener: OnDataStateChangeListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater(inflater)
        return _binding!!.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try{
            onStateChangeListener = context as OnDataStateChangeListener
        }catch (e: ClassCastException){
            Log.e(Constants.TAG, "$context must implement OnDataStateChangeListener")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    protected abstract val bindingInflater: (LayoutInflater) -> ViewBinding

}