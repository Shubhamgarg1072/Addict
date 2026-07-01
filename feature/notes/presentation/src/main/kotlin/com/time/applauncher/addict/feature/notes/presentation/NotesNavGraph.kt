package com.time.applauncher.addict.feature.notes.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@Serializable
data object NotesRoute

fun NavGraphBuilder.notesGraph(onBack: () -> Unit) {
    composable<NotesRoute> {
        NotesRoot(onBack = onBack)
    }
}

val notesPresentationModule = module {
    viewModelOf(::NotesViewModel)
}
