package cloud.banson.orangeNote.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cloud.banson.orangeNote.R
import cloud.banson.orangeNote.database.Note
import cloud.banson.orangeNote.database.NoteDatabase
import cloud.banson.orangeNote.databinding.FragmentDetailsBinding
import com.google.android.material.snackbar.Snackbar
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.coroutines.*
import java.util.*

class DetailsFragment : Fragment(), TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener {

    private lateinit var viewModel: DetailsViewModel
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

        val arguments = DetailsFragmentArgs.fromBundle(requireArguments())

        val viewModelFactory =
            DetailsViewModelFactory(dataSource, application, arguments.currentNoteId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DetailsViewModel::class.java)

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
                    Snackbar.LENGTH_LONG
                ).show()

                viewModel.doneMakeSnackBar()
            }
        })

        viewModel.makeDatePicker.observe(viewLifecycleOwner, Observer {
            if (it) {
                val calendar = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog.newInstance(this, calendar)
                datePickerDialog.show(childFragmentManager, "DatePickerDialog")
            }
        })

        viewModel.makeTimePicker.observe(viewLifecycleOwner, Observer {
            if (it) {
                //val calendar = Calendar.getInstance()
                val timePickerDialog = TimePickerDialog.newInstance(this, true)
                timePickerDialog.show(childFragmentManager, "TimePickerDialog")
            }
        })

        viewModel.addToSysCalendar.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                val intent = Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(
                        CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        it.alarmTime
                    )
                    .putExtra(
                        CalendarContract.EXTRA_EVENT_END_TIME,
                        it.alarmTime
                    )
                    .putExtra(CalendarContract.Events.TITLE, it.title)
                    .putExtra(CalendarContract.Events.DESCRIPTION, it.details)
                startActivity(intent)
                viewModel.doneAddSysCalendar()
            }
        })

        binding.switchAlarm.setOnCheckedChangeListener { button: CompoundButton, isChecked: Boolean ->
            viewModel.switchStatusChanged(isChecked)
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        val inputMethodManager =
            requireNotNull(this.activity).application.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        viewModel.doneTimeClick(hourOfDay, minute)
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        viewModel.doneDateClick(year, monthOfYear + 1, dayOfMonth)
    }
}
