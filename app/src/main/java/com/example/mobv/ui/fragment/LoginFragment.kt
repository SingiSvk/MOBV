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
import com.example.mobv.databinding.FragmentLoginBinding
import com.example.mobv.helpers.Injection
import com.example.mobv.helpers.PreferenceData
import com.example.mobv.ui.viewmodel.AuthViewModel

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProvider(
            this,
            Injection.providePubsViewModelFactory(requireContext())
        )[AuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val x = PreferenceData.getInstance().getUserItem(requireContext())
        if ((x?.uid ?: "").isNotBlank()) {
            Navigation.findNavController(view).navigate(R.id.action_to_pubs)
            return
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = authViewModel
        }

        binding.loginBtn.setOnClickListener {

            if (binding.username.text.toString().isNotBlank() && binding.password.text.toString().isNotBlank()) {
                authViewModel.login(
                    binding.username.text.toString(),
                    binding.password.text.toString()
                )
            }else {
                authViewModel.show("Fill in name and password")
            }
        }

        binding.registerBtn.setOnClickListener {
            it.findNavController().navigate(R.id.action_to_sign_up)
        }

        authViewModel.user.observe(viewLifecycleOwner){
            it?.let {
                PreferenceData.getInstance().putUserItem(requireContext(),it)
                Navigation.findNavController(requireView()).navigate(R.id.action_to_pubs)
            }
        }
    }
}