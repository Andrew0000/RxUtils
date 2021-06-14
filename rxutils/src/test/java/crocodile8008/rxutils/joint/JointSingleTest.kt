package crocodile8008.rxutils.joint

import crocodile8008.rxutils.log.RxUtilsLog
import crocodile8008.rxutils.log.RxUtilsLogTarget
import crocodile8008.rxutils.memcache.MaxSizeCache
import crocodile8008.rxutils.memcache.MemCache
import crocodile8008.rxutils.memcache.OneValueCache
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class JointSingleTest {

    private val scheduler: TestScheduler = TestScheduler()
    private var workCounter = 0

    @Before
    fun setUp() {
        RxUtilsLog.target = RxUtilsLogTarget.CONSOLE
    }

    @Test
    fun `No subscriptions to request - no work started`() {
        val request = createRequest()
        request.getSingle("1")
        scheduler.triggerActions()
        assertEquals(0, workCounter)
    }

    @Test
    fun `Requested first time - work started`() {
        val request = createRequest()
        request.getSingle("1").subscribe()
        scheduler.triggerActions()
        assertEquals(1, workCounter)
    }

    @Test
    fun `Requested 2 times one by one - only one work started`() {
        val request = createRequest()
        lateinit var result1: Any
        lateinit var result2: Any

        request.getSingle(key = "1").subscribe { result -> result1 = result }
        request.getSingle(key = "1").subscribe { result -> result2 = result }
        scheduler.triggerActions()

        assertEquals(1, workCounter)
        assertEquals(result1, result2)
    }

    @Test
    fun `Requested 2 times with same key and 1 time with different key one by one - 2 works started`() {
        val request = createRequest()
        lateinit var result1: Any
        lateinit var result2: Any
        lateinit var result3: Any

        request.getSingle(key = "1").subscribe { result -> result1 = result }
        request.getSingle(key = "1").subscribe { result -> result2 = result }
        request.getSingle(key = "2").subscribe { result -> result3 = result }
        scheduler.triggerActions()

        assertEquals(2, workCounter)
        assertEquals(result1, result2)
        assertNotEquals(result1, result3)
    }

    @Test
    fun `Requested 2 times one by one with different keys - both works started`() {
        val request = createRequest()
        lateinit var result1: Any
        lateinit var result2: Any

        request.getSingle(key = "1").subscribe { result -> result1 = result }
        request.getSingle(key = "2").subscribe { result -> result2 = result }
        scheduler.triggerActions()

        assertEquals(2, workCounter)
        assertNotEquals(result1, result2)
    }

    @Test
    fun `Requested 2 times with waiting - both works started`() {
        val request = createRequest()
        lateinit var result1: Any
        lateinit var result2: Any

        request.getSingle(key = "1").subscribe { result -> result1 = result }
        scheduler.triggerActions()
        request.getSingle(key = "1").subscribe { result -> result2 = result }
        scheduler.triggerActions()

        assertEquals(2, workCounter)
        assertNotEquals(result1, result2)
    }

    @Test
    fun `Requested 2 times with waiting and unbounded cache - 1 work started`() {
        val request = createRequest(
            MaxSizeCache(
                maxSize = Int.MAX_VALUE
            )
        )
        lateinit var result1: Any
        lateinit var result2: Any

        request.getSingle(key = "1").subscribe { result -> result1 = result }
        scheduler.triggerActions()
        request.getSingle(key = "1").subscribe { result -> result2 = result }
        scheduler.triggerActions()

        assertEquals(1, workCounter)
        assertEquals(result1, result2)
    }

    @Test
    fun `Requested 3 times, 2 keys with waiting and unbounded cache - 2 works started`() {
        val request = createRequest(
            MaxSizeCache(
                maxSize = Int.MAX_VALUE
            )
        )
        lateinit var result1: Any
        lateinit var result2: Any
        lateinit var result3: Any

        request.getSingle(key = "1").subscribe { result -> result1 = result }
        scheduler.triggerActions()
        request.getSingle(key = "1").subscribe { result -> result2 = result }
        scheduler.triggerActions()
        request.getSingle(key = "2").subscribe { result -> result3 = result }
        scheduler.triggerActions()

        assertEquals(2, workCounter)
        assertEquals(result1, result2)
        assertNotEquals(result1, result3)
    }

    @Test
    fun `Requested 3 times, 3 keys with waiting and unbounded cache - 3 works started`() {
        val request = createRequest(
            MaxSizeCache(
                maxSize = Int.MAX_VALUE
            )
        )
        lateinit var result1: Any
        lateinit var result2: Any
        lateinit var result3: Any

        request.getSingle(key = "1").subscribe { result -> result1 = result }
        scheduler.triggerActions()
        request.getSingle(key = "2").subscribe { result -> result2 = result }
        scheduler.triggerActions()
        request.getSingle(key = "3").subscribe { result -> result3 = result }
        scheduler.triggerActions()

        assertEquals(3, workCounter)
        assertNotEquals(result1, result2)
        assertNotEquals(result1, result3)
    }

    @Test
    fun `Requested 4 times, 3 keys with waiting and cache size 2 - 4 works started`() {
        val request = createRequest(
            MaxSizeCache(
                maxSize = 2
            )
        )
        lateinit var result1: Any
        lateinit var result2: Any
        lateinit var result3: Any
        lateinit var result4: Any

        request.getSingle(key = "1").subscribe { result -> result1 = result }
        scheduler.triggerActions()
        request.getSingle(key = "2").subscribe { result -> result2 = result }
        scheduler.triggerActions()
        request.getSingle(key = "3").subscribe { result -> result3 = result }
        scheduler.triggerActions()
        request.getSingle(key = "1").subscribe { result -> result4 = result }
        scheduler.triggerActions()

        assertEquals(4, workCounter)
        assertNotEquals(result1, result2)
        assertNotEquals(result1, result3)
        assertNotEquals(result1, result4)
    }

    @Test
    fun `Requested 5 times, 3 keys with waiting and cache size 1 - 5 works started`() {
        val request = createRequest(OneValueCache())
        lateinit var result1: Any
        lateinit var result2: Any
        lateinit var result3: Any
        lateinit var result4: Any
        lateinit var result5: Any

        request.getSingle(key = "1").subscribe { result -> result1 = result }
        scheduler.triggerActions()
        request.getSingle(key = "2").subscribe { result -> result2 = result }
        scheduler.triggerActions()
        request.getSingle(key = "3").subscribe { result -> result3 = result }
        scheduler.triggerActions()
        request.getSingle(key = "1").subscribe { result -> result4 = result }
        scheduler.triggerActions()
        request.getSingle(key = "2").subscribe { result -> result5 = result }
        scheduler.triggerActions()

        assertEquals(5, workCounter)
        assertNotEquals(result1, result2)
        assertNotEquals(result1, result3)
        assertNotEquals(result1, result4)
        assertNotEquals(result1, result5)
    }

    @Test
    fun `Requested 5 times, 3 keys with waiting and cache size 3 - 3 works started`() {
        val request = createRequest(
            MaxSizeCache(
                maxSize = 3
            )
        )
        lateinit var result1: Any
        lateinit var result2: Any
        lateinit var result3: Any
        lateinit var result4: Any
        lateinit var result5: Any

        request.getSingle(key = "1").subscribe { result -> result1 = result }
        scheduler.triggerActions()
        request.getSingle(key = "2").subscribe { result -> result2 = result }
        scheduler.triggerActions()
        request.getSingle(key = "3").subscribe { result -> result3 = result }
        scheduler.triggerActions()
        request.getSingle(key = "1").subscribe { result -> result4 = result }
        scheduler.triggerActions()
        request.getSingle(key = "2").subscribe { result -> result5 = result }
        scheduler.triggerActions()

        assertEquals(3, workCounter)
        assertNotEquals(result1, result2)
        assertNotEquals(result1, result3)
        assertEquals(result1, result4)
        assertEquals(result2, result5)
    }

    private fun createRequest(cache: MemCache<Any> = MemCache.EMPTY): JointSingle<Any, Any> =
        JointSingle(cache, scheduler) {
            Single.fromCallable {
                println("do work")
                workCounter++
                Any()
            }
        }
}
