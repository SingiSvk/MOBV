package com.example.mobv.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobv.R
import com.example.mobv.databinding.FragmentAddPubBinding

class AddPubFragment : Fragment() {

    private var _binding: FragmentAddPubBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPubBinding.inflate(inflater, container, false)
        val view = binding.root
        val button = view.findViewById<Button>(R.id.save_data_btn)

        button.setOnClickListener {
            val action = AddPubFragmentDirections.actionAddPubFragmentToLogoFragment(
                binding.name.text.toString(),
                binding.establishmentName.text.toString(),
                binding.longitude.text.toString(),
                binding.latitude.text.toString()
            )
            findNavController().navigate(action)
        }
        return view
    }
}