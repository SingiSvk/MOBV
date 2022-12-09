package com.example.mobv.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.mobv.R
import com.example.mobv.databinding.FragmentAddFriendBinding
import com.example.mobv.helpers.Injection
import com.example.mobv.helpers.PreferenceData
import com.example.mobv.ui.viewmodel.FriendsViewModel

class AddFriendFragment : Fragment() {
    private lateinit var binding: FragmentAddFriendBinding
    private lateinit var viewModel: FriendsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            Injection.providePubsViewModelFactory(requireContext())
        )[FriendsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val x = PreferenceData.getInstance().getUserItem(requireContext())
        if ((x?.uid ?: "").isBlank()) {
            Navigation.findNavController(view).navigate(R.id.action_to_login)
            return
        }

        binding.addFriendBtn.setOnClickListener {

            viewModel.addFriend(binding.userName.text.toString())
            it.findNavController().navigate(R.id.action_to_friends)
        }


        viewModel.addedFriend.observe(viewLifecycleOwner) { it ->
            it?.getContentIfNotHandled()?.let { it ->
                if (it) {
                    viewModel.show("Successfully added friend.")
                }
            }
        }
    }
}