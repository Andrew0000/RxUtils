package crocodile8008.rxutils.stream

import org.junit.Assert.*
import org.junit.Test

class StatefulStreamTest {

    @Test
    fun `When add elements - Then can get it later`() {
        val stream = StatefulStream<Int, String>()
        stream.set(1, "1")
        stream.set(2, "2")
        assertEquals(
            mapOf(1 to "1", 2 to "2"),
            stream.allBlocking()
        )
    }

    @Test
    fun `When remove element - Then can not get it later`() {
        val stream = StatefulStream<Int, String>()
        stream.set(1, "1")
        stream.set(2, "2")
        stream.remove(1)
        assertEquals(
            mapOf(2 to "2"),
            stream.allBlocking()
        )
    }
}