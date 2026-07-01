package com.time.applauncher.addict.feature.agenda.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@Serializable
data object AgendaRoute

fun NavGraphBuilder.agendaGraph(onBack: () -> Unit) {
    composable<AgendaRoute> {
        AgendaRoot(onBack = onBack)
    }
}

val agendaPresentationModule = module {
    viewModelOf(::AgendaViewModel)
}
