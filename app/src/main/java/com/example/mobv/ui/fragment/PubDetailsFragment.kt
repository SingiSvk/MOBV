package com.example.mobv.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mobv.R
import com.example.mobv.databinding.FragmentPubDetailsBinding
import com.example.mobv.helpers.Injection
import com.example.mobv.helpers.PreferenceData
import com.example.mobv.ui.viewmodel.DetailViewModel

class PubDetailsFragment : Fragment() {

    private lateinit var binding: FragmentPubDetailsBinding
    private lateinit var viewModel: DetailViewModel

    private val args: PubDetailsFragmentArgs by navArgs()

    override fun onCreate(
        savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            Injection.providePubsViewModelFactory(requireContext())
        )[DetailViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPubDetailsBinding.inflate(inflater, container, false)
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
            bnd.visitors.text = buildString {
                append(args.visitors)
                append(" návštevníkov")
            }

            bnd.back.setOnClickListener { it.findNavController().popBackStack() }

            bnd.showOnMapBtn.setOnClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            "geo:0,0,?q=" +
                                    "${viewModel.pub.value?.lat ?: 0}," +
                                    "${viewModel.pub.value?.lon ?: 0}" +
                                    "(${viewModel.pub.value?.name ?: ""}"
                        )
                    )
                )
            }
        }

        viewModel.loadPub(args.id)
    }
}