package com.example.mobv.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.mobv.R
import com.example.mobv.adapter.ItemAdapter
import com.example.mobv.data.Datasource
import com.example.mobv.databinding.FragmentPubListBinding
import java.nio.charset.Charset

class PubListFragment : Fragment() {

    companion object {
        val removePubs: ArrayList<Int> = ArrayList()
    }

    private var _binding: FragmentPubListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        println(savedInstanceState.toString())
        _binding = FragmentPubListBinding.inflate(inflater, container, false)
        val view = binding.root

        // Get Pubs
        val pubsJson = resources.openRawResource(R.raw.pubs).readBytes().toString(Charset.defaultCharset())
        val pubs = Datasource().loadPubs(pubsJson).elements.sortedBy { pub -> pub.tags.name }.toMutableList()

        removePubs.forEach {
            println(it)
            pubs.removeAt(it)
        }



        // Fill recyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = activity?.let { ItemAdapter(it.applicationContext, pubs) }
        recyclerView.setHasFixedSize(true)

        // Add pub btn
        binding.addPub.setOnClickListener{
            view.findNavController().navigate(R.id.addPubFragment)
        }

        return view
    }

    override fun onResume() {
        println("remsume")
        super.onResume()
    }
}