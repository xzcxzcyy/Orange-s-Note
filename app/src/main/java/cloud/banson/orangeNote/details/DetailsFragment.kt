package cloud.banson.orangeNote.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cloud.banson.orangeNote.R
import cloud.banson.orangeNote.database.NoteDatabase
import cloud.banson.orangeNote.databinding.FragmentDetailsBinding
import com.google.android.material.snackbar.Snackbar

class DetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val binding: FragmentDetailsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_details, container, false
        )
        val application = requireNotNull(this.activity).application
        val dataSource = NoteDatabase.getInstance(application).noteDatabaseDao
        val viewModelFactory = DetailsViewModelFactory(dataSource, application)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(DetailsViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.navigateToListFragment.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                this.findNavController()
                    .navigate(DetailsFragmentDirections.actionDetailsFragmentToListFragment())
                viewModel.doneNavigating()
            }
        })

        viewModel.makeSnackBar.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    it,
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()

                viewModel.doneMakeSnackBar()
            }
        })

        return binding.root
    }
}