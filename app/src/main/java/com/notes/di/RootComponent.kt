package com.notes.di

import com.notes.data.NoteDatabase
import com.notes.ui.list.NoteListViewModel
import dagger.Component
import dagger.Module
import dagger.Provides

@RootScope
@Component(
    dependencies = [
        AppComponent::class,
    ],
    modules = [
        RootComponent.ViewModelProvider::class
    ]
)
interface RootComponent {

    @Component.Factory
    interface Factory {
        fun create(
            appComponent: AppComponent
        ): RootComponent
    }

    fun getNoteListViewModel(): NoteListViewModel

    @Module
    class ViewModelProvider {

        @RootScope
        @Provides
        fun getVieModel(noteDatabase: NoteDatabase): NoteListViewModel =
            NoteListViewModel(noteDatabase)
    }
}