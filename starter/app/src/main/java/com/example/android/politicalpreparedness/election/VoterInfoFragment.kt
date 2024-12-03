package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {
    private var _binding: FragmentVoterInfoBinding? = null
    private val binding get() = _binding!!

    private val voterInfoViewModel: VoterInfoViewModel by lazy {
        ViewModelProvider(
            this, VoterInfoViewModelFactory(application = requireActivity().application)
        )[VoterInfoViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoterInfoBinding.inflate(inflater)
        return binding.root

        // TODO: Add ViewModel values and create ViewModel

        // TODO: Add binding values

        // TODO: Populate voter info -- hide views without provided data.

        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */

        // TODO: Handle loading of URLs

        // TODO: Handle save button UI state
        // TODO: cont'd Handle save button clicks
    }

    // TODO: Create method to load URL intents

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}