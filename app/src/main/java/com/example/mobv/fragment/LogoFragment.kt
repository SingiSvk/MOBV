package com.example.mobv.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.mobv.databinding.FragmentLogoBinding

class LogoFragment : Fragment() {

    private var _binding: FragmentLogoBinding? = null
    private val binding get() = _binding!!

    private val args: LogoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogoBinding.inflate(inflater, container, false)
        binding.textView.text = args.name
        binding.textView2.text = args.name2

        binding.showOnMapBtn.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:${args.lat},${args.lon}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
        return binding.root
    }
}