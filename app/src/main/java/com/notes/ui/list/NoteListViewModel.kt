package com.notes.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.data.NoteDatabase
import com.notes.data.NoteDbo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoteListViewModel @Inject constructor(
    private val noteDatabase: NoteDatabase
) : ViewModel() {

    private val _notes = MutableLiveData<List<NoteDbo>?>()
    val notes: LiveData<List<NoteDbo>?> = _notes

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _notes.postValue(noteDatabase.noteDao().getAll())
        }
    }

    fun saveNote(note: NoteDbo) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDatabase.noteDao().addNote(note)
            val newValue = _notes.value?.toMutableList() ?: mutableListOf()
            var itemToReplace: NoteDbo? = null
            newValue.forEach {
                if (it.id == note.id) {
                    itemToReplace = it
                    return@forEach
                }
            }
            if (itemToReplace != null) {
                itemToReplace?.let {
                    val index = newValue.indexOf(it)
                    newValue.removeAt(index)

                }
            }
            newValue.add(note)
            _notes.postValue(newValue)
        }
    }

    fun deleteNote(note: NoteDbo) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDatabase.noteDao().deleteNoteByID(note.id)
            val newValue = _notes.value?.toMutableList()
            newValue?.remove(note)
            _notes.postValue(newValue)
        }
    }
}