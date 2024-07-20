package com.nizamisadykhov.notepad.features.detail

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.PickContact
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.core.content.FileProvider
import androidx.core.os.BundleCompat
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.nizamisadykhov.notepad.R
import com.nizamisadykhov.notepad.data.Note
import com.nizamisadykhov.notepad.databinding.FragmentNoteDetailBinding
import com.nizamisadykhov.notepad.utils.getScaledBitmap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

@AndroidEntryPoint
class NoteDetailFragment : Fragment() {

    private var _binding: FragmentNoteDetailBinding? = null

    private val binding: FragmentNoteDetailBinding
        get() = _binding
            ?: throw IllegalStateException("Cannot access binding because it is null. Is the view visible?")

    private val viewModel: NoteDetailViewModel by viewModels()

    private val selectSuspect = registerForActivityResult(PickContact()) { uri ->
        uri?.let(::parseContactSelection)
    }

    private val takePhoto = registerForActivityResult(TakePicture()) { didTakePhoto ->
        if (didTakePhoto && photoName != null) {
            viewModel.updateNote { oldNote ->
                oldNote.copy(photoFileName = photoName)
            }
        }
    }

    private var photoName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteDetailBinding.inflate(layoutInflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        collectUiState()
        setDataPickerListener()
    }

    private fun setDataPickerListener() {
        setFragmentResultListener(DatePickerFragment.REQUEST_KEY_DATE) { _, bundle ->
            BundleCompat.getSerializable(
                bundle,
                DatePickerFragment.BUNDLE_KEY_DATE,
                Date::class.java
            )?.let { newDate ->
                viewModel.updateNote { oldNote -> oldNote.copy(date = newDate) }
            }
        }
    }

    private fun setupUi() {
        binding.apply {
            title.doOnTextChanged { text, _, _, _ ->
                viewModel.updateNote { oldNote ->
                    oldNote.copy(title = text.toString())
                }
            }

            noteSolved.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateNote { oldNote ->
                    oldNote.copy(isSolved = isChecked)
                }
            }

            suspect.setOnClickListener {
                selectSuspect.launch(null)
            }

            val selectSuspectIntent = selectSuspect.contract.createIntent(requireContext(), null)
            suspect.isEnabled = canResolveIntent(selectSuspectIntent)


            camera.setOnClickListener {
                val authority = "${requireContext().packageName}.fileprovider"
                photoName = "IMG_${Date()}.JPG"
                val filesDir = requireContext().applicationContext.filesDir
                val photoFile = File(filesDir, photoName.orEmpty())
                val photoUri = FileProvider.getUriForFile(requireContext(), authority, photoFile)
                takePhoto.launch(photoUri)
            }

            val captureImageIntent = takePhoto.contract.createIntent(requireContext(), Uri.EMPTY)
            camera.isEnabled = canResolveIntent(captureImageIntent)
        }
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.note.collect(::updateUi)
            }
        }
        viewModel.initialize()
    }

    private fun updateUi(note: Note) {
        binding.apply {
            if (note.title != title.text.toString()) {
                title.setText(note.title)
            }
            noteDate.text = DateFormat.format(DATE_FORMAT, note.date).toString()
            noteDate.setOnClickListener {
                findNavController().navigate(
                    NoteDetailFragmentDirections.selectDate(note.date)
                )
            }
            noteSolved.isChecked = note.isSolved

            report.setOnClickListener {
                val reportIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getNoteReport(note))
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.report_subject))
                }

                val chooserIntent = Intent.createChooser(
                    reportIntent,
                    getString(R.string.report)
                )

                startActivity(chooserIntent)
            }

            suspect.text = note.author.ifEmpty {
                getString(R.string.author_text)
            }
            updatePhoto(note.photoFileName)
        }
    }

    private fun getNoteReport(note: Note): String {
        val solvedString = if (note.isSolved) {
            getString(R.string.report_solved)
        } else {
            getString(R.string.report_unsolved)
        }

        val dataString = DateFormat.format(DATE_FORMAT, note.date).toString()
        val suspectText = if (note.author.isBlank()) {
            getString(R.string.report_no_author)
        } else {
            getString(R.string.report_author, note.author)
        }

        return getString(R.string.report, note.title, dataString, solvedString, suspectText)
    }

    private fun parseContactSelection(contactUri: Uri) {
        val queryField = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        val queryCursor = requireActivity().contentResolver
            .query(contactUri, queryField, null, null, null)

        queryCursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val author = cursor.getString(0)
                viewModel.updateNote { oldNote ->
                    oldNote.copy(author = author)
                }
            }
        }
    }

    private fun canResolveIntent(intent: Intent): Boolean {
        val resolvedActivity = requireActivity()
            .packageManager
            .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)

        return resolvedActivity != null
    }

    private fun updatePhoto(photoFileName: String?) {
        if (binding.photo.tag != photoFileName) {
            val photoFile = photoFileName?.let { fileName ->
                File(requireContext().applicationContext.filesDir, fileName)
            }

            if (photoFile?.exists() == true) {
                binding.photo.doOnLayout { measuredView ->
                    val scaledBitmap = getScaledBitmap(
                        path = photoFile.path,
                        destWidth = measuredView.width,
                        destHeight = measuredView.height
                    )
                    binding.photo.setImageBitmap(scaledBitmap)
                    binding.photo.tag = photoFileName
                }
            } else {
                binding.photo.setImageBitmap(null)
                binding.photo.tag = null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DATE_FORMAT = "dd.MM.yyyy"
    }

}
