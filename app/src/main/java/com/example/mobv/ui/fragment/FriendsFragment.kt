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
import com.example.mobv.databinding.FragmentFriendsBinding
import com.example.mobv.helpers.Injection
import com.example.mobv.helpers.PreferenceData
import com.example.mobv.ui.viewmodel.FriendsViewModel

class FriendsFragment : Fragment() {
    private lateinit var binding: FragmentFriendsBinding
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
        binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val x = PreferenceData.getInstance().getUserItem(requireContext())
        if ((x?.uid ?: "").isBlank()) {
            Navigation.findNavController(view).navigate(R.id.action_to_login)
            return
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            bnd.addFriendBtn.setOnClickListener {
                it.findNavController().navigate(R.id.action_to_add_friend)
            }

            bnd.back.setOnClickListener { it.findNavController().popBackStack() }

            bnd.swipeRefresh.setOnRefreshListener {
                viewModel.refreshData()
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            binding.swipeRefresh.isRefreshing = it
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }
    }
}