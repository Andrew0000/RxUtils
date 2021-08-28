package crocodile8008.rxutils.stream

import org.junit.Assert.*
import org.junit.Test

class ConsumableStreamTest {

    @Test
    fun `Cache values until someone subscribes and receive values once in first active subscriber`() {
        val stream = ConsumableStream<Any>()
        stream.push(1)
        stream.push("2")
        stream.push(true)

        val receivedValues1 = mutableListOf<Any>()
        val disposable1 = stream.observe()
            .subscribe {
                receivedValues1.add(it)
            }

        val receivedValues2 = mutableListOf<Any>()
        stream.observe()
            .subscribe {
                receivedValues2.add(it)
            }

        assertEquals(listOf(1, "2", true), receivedValues1)
        assertEquals(listOf<Any>(), receivedValues2)

        disposable1.dispose()
        stream.push(3)
        stream.push("4")
        stream.push(false)

        assertEquals(listOf(1, "2", true), receivedValues1)
        assertEquals(listOf(3, "4", false), receivedValues2)
    }

    @Test
    fun `Receive ongoing values once only in all active subscribers`() {
        val stream = ConsumableStream<Any>()

        val receivedValues1 = mutableListOf<Any>()
        stream.observe()
            .subscribe {
                receivedValues1.add(it)
            }

        val receivedValues2 = mutableListOf<Any>()
        stream.observe()
            .subscribe {
                receivedValues2.add(it)
            }

        stream.push(1)
        stream.push("2")
        stream.push(true)

        val receivedValues3 = mutableListOf<Any>()
        stream.observe()
            .subscribe {
                receivedValues3.add(it)
            }

        assertEquals(listOf(1, "2", true), receivedValues1)
        assertEquals(listOf(1, "2", true), receivedValues2)
        assertEquals(listOf<Any>(), receivedValues3)
    }
}