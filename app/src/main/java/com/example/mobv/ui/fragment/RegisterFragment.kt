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
import com.example.mobv.databinding.FragmentRegisterBinding
import com.example.mobv.helpers.Injection
import com.example.mobv.helpers.PreferenceData
import com.example.mobv.ui.viewmodel.AuthViewModel

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProvider(
            this,
            Injection.providePubsViewModelFactory(requireContext())
        )[AuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
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


        binding.registerBtn.setOnClickListener {
            if (binding.username.text.toString().isNotBlank() && binding.password.text.toString().isNotBlank()
                && binding.password.text.toString().compareTo(binding.passwordConfirm.text.toString())==0) {
                authViewModel.signup(
                    binding.username.text.toString(),
                    binding.password.text.toString()
                )
            } else if (binding.username.text.toString().isBlank() || binding.password.text.toString().isBlank()){
                authViewModel.show("Fill in name and password")
            } else {
                authViewModel.show("Passwords must be same")
            }
        }

        binding.loginBtn.setOnClickListener {
            it.findNavController().navigate(R.id.action_to_login)
        }

        authViewModel.user.observe(viewLifecycleOwner){
            it?.let {
                PreferenceData.getInstance().putUserItem(requireContext(),it)
                Navigation.findNavController(requireView()).navigate(R.id.action_to_pubs)
            }
        }
    }
}