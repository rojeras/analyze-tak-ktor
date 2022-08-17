package com.example.view

import com.example.models.ServiceConsumer

data class ViewData(
    val headings: List<String>
    val content: List<List<String>>
)

fun mkConsumerViewData(headings: List<String>, consumers: List<ServiceConsumer>): ViewData {
    val content = mutableListOf<List<String>>()
    for (consumer in consumers) {
        content.add(listOf<String>(consumer.id.toString(), consumer.hsaId, consumer.description))
    }
    return ViewData(headings, content)
}