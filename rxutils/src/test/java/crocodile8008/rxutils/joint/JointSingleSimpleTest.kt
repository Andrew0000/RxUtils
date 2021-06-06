package crocodile8008.rxutils.joint

import crocodile8008.rxutils.log.RxUtilsLog
import crocodile8008.rxutils.log.RxUtilsLogTarget
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class JointSingleSimpleTest {

    private val scheduler: TestScheduler = TestScheduler()
    private var workCounter = 0

    @Before
    fun setUp() {
        RxUtilsLog.target = RxUtilsLogTarget.CONSOLE
    }

    @Test
    fun `Requested first time cached - work started`() {
        val request = createCached()
        request.getSingle().subscribe()
        scheduler.triggerActions()
        assertEquals(1, workCounter)
    }

    @Test
    fun `Requested first time not cached - work started`() {
        val request = createNotCached()
        request.getSingle().subscribe()
        scheduler.triggerActions()
        assertEquals(1, workCounter)
    }

    @Test
    fun `Requested 2 times one by one cached - only one work started`() {
        val request = createCached()
        lateinit var result1: Any
        lateinit var result2: Any

        request.getSingle().subscribe { result -> result1 = result }
        request.getSingle().subscribe { result -> result2 = result }
        scheduler.triggerActions()

        assertEquals(1, workCounter)
        assertEquals(result1, result2)
    }

    @Test
    fun `Requested 2 times one by one not cached - only one work started`() {
        val request = createNotCached()
        lateinit var result1: Any
        lateinit var result2: Any

        request.getSingle().subscribe { result -> result1 = result }
        request.getSingle().subscribe { result -> result2 = result }
        scheduler.triggerActions()

        assertEquals(1, workCounter)
        assertEquals(result1, result2)
    }

    @Test
    fun `Requested 4 times cached - 1 work started`() {
        val request = createCached()
        lateinit var result1: Any
        lateinit var result2: Any
        lateinit var result3: Any
        lateinit var result4: Any

        request.getSingle().subscribe { result -> result1 = result }
        scheduler.triggerActions()
        request.getSingle().subscribe { result -> result2 = result }
        scheduler.triggerActions()
        request.getSingle().subscribe { result -> result3 = result }
        scheduler.triggerActions()
        request.getSingle().subscribe { result -> result4 = result }
        scheduler.triggerActions()

        assertEquals(1, workCounter)
        assertEquals(result1, result2)
        assertEquals(result1, result3)
        assertEquals(result1, result4)
    }

    @Test
    fun `Requested 4 times not cached - 4 works started`() {
        val request = createNotCached()
        lateinit var result1: Any
        lateinit var result2: Any
        lateinit var result3: Any
        lateinit var result4: Any

        request.getSingle().subscribe { result -> result1 = result }
        scheduler.triggerActions()
        request.getSingle().subscribe { result -> result2 = result }
        scheduler.triggerActions()
        request.getSingle().subscribe { result -> result3 = result }
        scheduler.triggerActions()
        request.getSingle().subscribe { result -> result4 = result }
        scheduler.triggerActions()

        assertEquals(4, workCounter)
        assertNotEquals(result1, result2)
        assertNotEquals(result1, result3)
        assertNotEquals(result1, result4)
    }

    private fun createCached(): JointSingleSimple<Any> =
        JointSingleSimple.cached(scheduler) {
            Single.fromCallable {
                println("do work")
                workCounter++
                Any()
            }
        }

    private fun createNotCached(): JointSingleSimple<Any> =
        JointSingleSimple.notCached(scheduler) {
            Single.fromCallable {
                println("do work")
                workCounter++
                Any()
            }
        }
}
