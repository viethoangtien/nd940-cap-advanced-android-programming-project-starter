package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.google.android.material.snackbar.Snackbar

class VoterInfoFragment : Fragment() {
    private var _binding: FragmentVoterInfoBinding? = null
    private val binding get() = _binding!!
    private val args: VoterInfoFragmentArgs by navArgs()

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = voterInfoViewModel
        getArgs()
        initViews()
        observeLiveData()
        voterInfoViewModel.getVoterInformation()
    }

    private fun observeLiveData() {
        voterInfoViewModel.votingLocation.observe(viewLifecycleOwner) { votingLocationUrl ->
            openUrl(votingLocationUrl)
        }
        voterInfoViewModel.ballotInformation.observe(viewLifecycleOwner) { ballotInformationUrl ->
            openUrl(ballotInformationUrl)
        }
        voterInfoViewModel.allElectionLiveData.observe(viewLifecycleOwner) { elections ->
            voterInfoViewModel.updateFollowStatus(elections)
        }
    }

    private fun getArgs() {
        voterInfoViewModel.election = args.election
    }

    private fun initViews() {
        binding.apply {
            textVotingLocation.setOnClickListener {
                this@VoterInfoFragment.voterInfoViewModel.showVotingLocation()
            }
            textBallotInformation.setOnClickListener {
                this@VoterInfoFragment.voterInfoViewModel.showBallotInformation()
            }
            textFollow.setOnClickListener {
                this@VoterInfoFragment.voterInfoViewModel.buttonClicked()
            }
        }
    }

    private fun openUrl(url: String) {
        runCatching {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }.getOrElse {
            Snackbar.make(
                requireView(),
                getString(R.string.failed_to_open_link),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}