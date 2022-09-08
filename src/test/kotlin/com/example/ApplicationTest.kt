package com.example

import com.example.models.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        // application {configureRouting() }
        client.get("/hello").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText(), "Server does not work correctly!")
        }
    }

    @Test
    fun testRoutingView1() = testApplication {
        // application {configureRouting()}
        ConnectionPoint.load()
        client.get("/tak/5/routings").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertContains(bodyAsText(), "Vägval i SLL-PROD", false, "Routing page does not display as expected!")
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
        val takInfo = obtainTakInfo(7)

        assertTrue(
            takInfo.contracts.size > 120 && takInfo.contracts.size < 140,
            "Wrong number of contracts"
        )
        // assertTrue(takInfo5.logicalAddress.size > 4500 && takInfo5.logicalAddress.size < 4600, "Wrong number of logical addresses")
    }

    @Test
    fun testTakInfo2() = runBlocking {
        val takInfo = obtainTakInfo(7)
        println(takInfo.logicalAddresses.size)

        assertTrue(
            takInfo.logicalAddresses.size > 110 && takInfo.logicalAddresses.size < 130,
            "Wrong number of logical addresses"
        )
    }

    @Test
    fun testTakInfo3() = runBlocking {
        val takInfo = obtainTakInfo(7)
        println(takInfo.serviceConsumers.size)

        assertTrue(
            takInfo.serviceConsumers.size > 60 && takInfo.serviceConsumers.size < 80,
            "Wrong number of service consumers"
        )
    }

    @Test
    fun testTakInfo4() = runBlocking {
        val takInfo = obtainTakInfo(7)
        println(takInfo.serviceProducers.size)

        assertTrue(
            takInfo.serviceProducers.size > 30 && takInfo.serviceProducers.size < 50,
            "Wrong number of service consumers"
        )
    }

    @Test
    fun testCheck1() = runBlocking {
        val takInfo = obtainTakInfo(6)

        val num = takInfo.tkNotPartOfAuthorization.size

        println("TK not used: $num")

        assertTrue(
            num > 40,
            "Wrong number of contracts not part of auths"
        )
    }

    @Test
    fun testIntegrationCheck1() = runBlocking {
        LogicalAddress.load(6)
        InstalledContract.load(6)

        val auth1 = Authorization(5, 1, 5, 6)
        val rout1 = Routing(5, 2, 5, 6, "dummy")

        assertTrue(
            rout1.matchAuthorization(auth1),
            "Auth and Route does not match into an integration!"
        )
    }

    @Test
    fun testIntegrationCheck3() = runBlocking {
        LogicalAddress.load(6)
        InstalledContract.load(6)

        val auth1 = Authorization(5, 1, 4, 6)
        val rout1 = Routing(5, 2, 5, 6, "dummy")

        assertFalse(
            rout1.matchAuthorization(auth1),
            "Different logical addresses should not match!"
        )
    }

    @Test
    fun testIntegrationCheck4() = runBlocking {
        LogicalAddress.load(6)
        InstalledContract.load(6)

        val auth1 = Authorization(5, 1, 5, 5)
        val rout1 = Routing(5, 2, 5, 6, "dummy")

        assertFalse(
            rout1.matchAuthorization(auth1),
            "Different contracts should not match!"
        )
    }

    @Test
    fun testTabulatorData1() = runBlocking {
        val num = ServiceComponent.tabulatorRowSpecifications.size

        println("Number of rows in ServiceComponent.tabulatorRowSpecification: $num")

        assertEquals(
            num,
            3,
            "ServiceComponent.tabulatorRowSpecification not initialized correctly"
        )
    }
}
