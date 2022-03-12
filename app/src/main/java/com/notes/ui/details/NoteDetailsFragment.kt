package com.notes.ui.details

import android.os.Bundle
import androidx.core.os.bundleOf
import com.notes.data.NoteDbo
import com.notes.databinding.FragmentNoteDetailsBinding
import com.notes.ui._base.ViewBindingFragment
import com.notes.ui.list.NoteListFragment
import com.notes.ui.list.format
import java.time.LocalDateTime

class NoteDetailsFragment : ViewBindingFragment<FragmentNoteDetailsBinding>(
    FragmentNoteDetailsBinding::inflate
) {
    private var callback: NoteListFragment.OnItemClickCallback? = null
    private var inNote: NoteDbo? = null

    override fun onViewBindingCreated(
        viewBinding: FragmentNoteDetailsBinding,
        savedInstanceState: Bundle?
    ) {
        super.onViewBindingCreated(viewBinding, savedInstanceState)
        arguments?.let {
            inNote = it.getParcelable(NOTE_KEY)
        }
        setListeners(viewBinding)
        setNoteData(viewBinding)
    }

    private fun setNoteData(binding: FragmentNoteDetailsBinding) = with(binding) {
        titleLayout.editText?.setText(inNote?.title)
        contentLayout.editText?.setText(inNote?.content)
        "Modified: ${inNote?.modifiedAt?.format()}".also { editedTime.text = it }
    }

    private fun setListeners(binding: FragmentNoteDetailsBinding) = with(binding) {

        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        btnSave.setOnClickListener {
            val title = titleLayout.editText?.text.toString()
            val content = contentLayout.editText?.text.toString()
            if (title.isNotEmpty() || content.isNotEmpty()) inNote?.let {
                val note = NoteDbo(
                    it.id,
                    title,
                    content,
                    it.createdAt,
                    LocalDateTime.now()
                )
                callback?.onItemClick(NoteAction.Save, note)
            }
            activity?.onBackPressed()
        }

        btnDelete.setOnClickListener {
            callback?.onItemClick(NoteAction.Delete, inNote)
            activity?.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        callback = null
    }

    companion object {
        fun newInstance(note: NoteDbo, callback: NoteListFragment.OnItemClickCallback) =
            NoteDetailsFragment()
                .apply {
                    arguments = bundleOf(
                        NOTE_KEY to note
                    )
                    this.callback = callback
                }

        private const val NOTE_KEY = "Note"
    }
}

enum class NoteAction {
    Save,
    Delete,
    Edit
}