package com.example.view

import com.example.models.ServiceConsumer
import com.example.models.obtainTakInfo

data class ViewData(
    val headings: List<String>,
    val content: List<List<String>>
)

suspend fun mkConsumerViewData(cpId: Int): ViewData {

    val takInfo = obtainTakInfo(cpId)
    val consumers = takInfo.serviceConsumers

    val content = mutableListOf<List<String>>()
    for (consumer in consumers) {
        content.add(listOf<String>(consumer.id.toString(), consumer.hsaId, consumer.description))
    }
    return ViewData(listOf("Id", "HsaId", "Beskrivning"), content)
}
