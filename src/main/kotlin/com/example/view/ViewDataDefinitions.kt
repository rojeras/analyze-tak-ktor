package com.example.view

import com.example.models.ComponentType
import com.example.models.obtainTakInfo

data class ViewData(
    val headings: List<String>,
    val content: List<List<String>>
)

// fun mkComponentViewData(components: List<ServiceComponent>): ViewData {
suspend fun mkComponentViewData(cpId: Int, componentType: ComponentType): ViewData {
    val takInfo = obtainTakInfo(cpId)

    val components = when (componentType) {
        ComponentType.CONSUMER -> takInfo.serviceConsumers
        ComponentType.PRODUCER -> takInfo.serviceProducers
    }

    val content = mutableListOf<List<String>>()

    for (component in components) {
        content.add(listOf<String>(component.id.toString(), component.hsaId, component.description))
    }
    return ViewData(listOf("Id", "HsaId", "Beskrivning"), content)
}
