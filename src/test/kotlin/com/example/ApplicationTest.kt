package com.example

import com.example.models.ConnectionPoint
import com.example.models.InstalledContract
import com.example.models.ServiceContract
import com.example.models.TakInfo
import com.example.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/hello").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText(), "Server does not work correctly!")
        }
    }

    @Test
    fun testTakApiPlattforms() = runBlocking {
        ConnectionPoint.load()
        assertEquals(ConnectionPoint.plattforms.size, 5, "Wrong number of connection points returned!")
    }

    @Test
    fun testInstalledContracts() = runBlocking {

        val takInfo = TakInfo(5)

        val installedContracts = InstalledContract.load(5)

        for (ic in installedContracts) {
            takInfo.addContract(
                ic.serviceContract.id,
                ic.serviceContract.namespace,
                ic.serviceContract.major
            )
        }

        assertTrue(takInfo.contracts.size > 100 && takInfo.contracts.size < 120, "Wrong number of contracts")
    }
}
