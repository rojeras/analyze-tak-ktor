package com.example

import com.example.models.ConnectionPoint
import com.example.models.InstalledContract
import com.example.models.obtainTakInfo
import com.example.plugins.configureRouting
import com.example.view.mkConsumerViewData
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
    fun testTakApiPlattformGetFun() = runBlocking {
        ConnectionPoint.load()
        val plattform = ConnectionPoint.getPlattform(5)
        assertEquals(plattform!!.id, 5, "ConnectionPoint.getPlattform() does not return expected cp!")
    }

    @Test
    fun testInstalledContracts() = runBlocking {
        val installedContracts = InstalledContract.load(5)

        assertTrue(installedContracts.size > 100 && installedContracts.size < 120, "Wrong number of contracts")
    }

    @Test
    fun testTakInfo1() = runBlocking {
        val takInfo5 = obtainTakInfo(5)

        assertTrue(takInfo5.contracts.size > 100 && takInfo5.contracts.size < 120, "Wrong number of contracts")
        // assertTrue(takInfo5.logicalAddress.size > 4500 && takInfo5.logicalAddress.size < 4600, "Wrong number of logical addresses")
    }

    @Test
    fun testTakInfo2() = runBlocking {
        val takInfo5 = obtainTakInfo(5)
        println(takInfo5.logicalAddress.size)

        assertTrue(
            takInfo5.logicalAddress.size > 4000 && takInfo5.logicalAddress.size < 4500,
            "Wrong number of logical addresses"
        )
    }

    @Test
    fun testTakInfo3() = runBlocking {
        val takInfo5 = obtainTakInfo(5)
        println(takInfo5.serviceConsumers.size)

        assertTrue(
            takInfo5.serviceConsumers.size > 30 && takInfo5.serviceConsumers.size < 45,
            "Wrong number of service consumers"
        )
    }

    @Test
    fun testTakInfo4() = runBlocking {
        val takInfo5 = obtainTakInfo(5)
        println(takInfo5.serviceProducers.size)

        assertTrue(
            takInfo5.serviceProducers.size > 18 && takInfo5.serviceProducers.size < 30,
            "Wrong number of service consumers"
        )
    }

    fun testViewData1() = runBlocking {
        val takInfo5 = obtainTakInfo(5)
        val noOfConsumers = takInfo5.serviceProducers.size

        val mCVD = mkConsumerViewData(5)

        assertEquals(noOfConsumers, mCVD.content.size, "Wrong number of lines in consumer view data")
    }
}
