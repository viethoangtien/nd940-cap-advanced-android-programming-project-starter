package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter

class ElectionsFragment : Fragment() {
    private var _binding: FragmentElectionBinding? = null
    private val binding get() = _binding!!

    private val electionsViewModel: ElectionsViewModel by lazy {
        ViewModelProvider(
            this, ElectionsViewModelFactory(application = requireActivity().application)
        )[ElectionsViewModel::class.java]
    }

    private lateinit var electionUpcomingListAdapter: ElectionListAdapter
    private lateinit var electionSavedListAdapter: ElectionListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentElectionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = electionsViewModel
        observerLiveData()
        initAdapter()
        electionsViewModel.getUpcomingElections()
    }

    private fun observerLiveData() {
        electionsViewModel.electionsLiveData.observe(viewLifecycleOwner) { upcomingElections ->
            electionUpcomingListAdapter.submitList(upcomingElections)
        }
    }

    private fun initAdapter() {
        electionUpcomingListAdapter = ElectionListAdapter(onItemClickListener = { election ->

        })
        binding.recyclerUpcomingElections.adapter = electionUpcomingListAdapter
        electionSavedListAdapter = ElectionListAdapter(onItemClickListener = { election ->

        })
        binding.recyclerSavedElections.adapter = electionSavedListAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}