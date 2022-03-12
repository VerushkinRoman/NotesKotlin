package com.notes.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.notes.data.NoteDbo
import com.notes.databinding.FragmentNoteListBinding
import com.notes.databinding.ListItemNoteBinding
import com.notes.di.DependencyManager
import com.notes.ui._base.FragmentNavigator
import com.notes.ui._base.ViewBindingFragment
import com.notes.ui._base.findImplementationOrThrow
import com.notes.ui.details.NoteAction
import com.notes.ui.details.NoteDetailsFragment
import com.notes.ui.list.NoteListFragment.OnItemClickCallback
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class NoteListFragment : ViewBindingFragment<FragmentNoteListBinding>(
    FragmentNoteListBinding::inflate
) {

    private val viewModel by lazy { DependencyManager.noteListViewModel() }

    private var recyclerViewAdapter: RecyclerViewAdapter

    private val onClickCallback = OnItemClickCallback { action, note ->
        when (action) {
            NoteAction.Save -> note?.let { viewModel.saveNote(note) }
            NoteAction.Delete -> note?.let { viewModel.deleteNote(note) }
            NoteAction.Edit -> note?.let { openNoteEditor(note) }
        }
    }

    init {
        recyclerViewAdapter = RecyclerViewAdapter(onClickCallback)
    }

    override fun onViewBindingCreated(
        viewBinding: FragmentNoteListBinding,
        savedInstanceState: Bundle?
    ) {
        super.onViewBindingCreated(viewBinding, savedInstanceState)
        viewBinding.list.adapter = recyclerViewAdapter
        viewBinding.list.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayout.VERTICAL
            )
        )
        viewBinding.createNoteButton.setOnClickListener {
            openNoteEditor(
                NoteDbo(
                    UUID.randomUUID().leastSignificantBits,
                    "",
                    "",
                    LocalDateTime.now(),
                    LocalDateTime.now()
                )
            )
        }

        viewModel.notes.observe(viewLifecycleOwner) { recyclerViewAdapter.setItems(it) }
    }

    private fun openNoteEditor(note: NoteDbo) {
        findImplementationOrThrow<FragmentNavigator>()
            .navigateTo(
                NoteDetailsFragment.newInstance(note, onClickCallback)
            )
    }

    fun interface OnItemClickCallback {
        fun onItemClick(action: NoteAction, note: NoteDbo?)
    }

    private class RecyclerViewAdapter(
        private val callback: OnItemClickCallback
    ) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

        private val items = mutableListOf<NoteDbo>()

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ) = ViewHolder(
            ListItemNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
        ) {
            holder.bind(items[position], callback)
        }

        override fun getItemCount() = items.size

        fun setItems(
            items: List<NoteDbo>?
        ) {
            this.items.clear()
            items?.let {
                val sortedList = items.sortedByDescending { it.modifiedAt }
                this.items.addAll(sortedList)
            }
            notifyDataSetChanged()
        }

        private class ViewHolder(
            private val binding: ListItemNoteBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(note: NoteDbo, callback: OnItemClickCallback) = with(binding) {
                titleLabel.text = note.title
                contentLabel.text = note.content
                "Created: ${note.createdAt.format()}".also { created.text = it }
                "Modified: ${note.modifiedAt.format()}".also { modified.text = it }
                root.setOnClickListener {
                    callback.onItemClick(NoteAction.Edit, note)
                }
            }

        }

    }

}

fun LocalDateTime.format():String{
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
    return this.format(formatter)
}