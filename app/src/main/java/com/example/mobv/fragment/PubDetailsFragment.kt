package com.example.mobv.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mobv.R
import com.example.mobv.databinding.FragmentPubDetailsBinding

class PubDetailsFragment : Fragment() {

    private var _binding: FragmentPubDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: PubDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPubDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.pubNazov.text = args.name
        binding.showOnMapBtn.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:${args.lat},${args.lon}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
        binding.removePub.setOnClickListener {
            PubListFragment.removePubs.add(args.index)
            view.findNavController().navigate(R.id.pubList)
        }
        return view
    }
}