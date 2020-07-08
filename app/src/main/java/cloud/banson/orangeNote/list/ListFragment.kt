package cloud.banson.orangeNote.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cloud.banson.orangeNote.list.ListFragmentDirections
import cloud.banson.orangeNote.R
import cloud.banson.orangeNote.databinding.FragmentListBinding

class ListFragment : Fragment() {
    lateinit var binding: FragmentListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_list, container, false
        )

        val buttonAdd = binding.buttonAdd
        buttonAdd.setOnClickListener {
            val navController = this.findNavController()
            navController.navigate(ListFragmentDirections.actionListFragmentToDetailsFragment())
        }
        return binding.root
    }
}