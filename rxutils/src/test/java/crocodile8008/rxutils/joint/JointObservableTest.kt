package crocodile8008.rxutils.joint

import crocodile8008.rxutils.log.RxUtilsLog
import crocodile8008.rxutils.log.RxUtilsLogTarget
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

class JointObservableTest {

    private var workCounter = 0
    private val scheduler: TestScheduler = TestScheduler()

    @Before
    fun setUp() {
        RxUtilsLog.target = RxUtilsLogTarget.CONSOLE
    }

    @Test
    fun `No subscriptions to request - no work started`() {
        val request = simpleRequest()
        request.getObservable("1")
        scheduler.triggerActions()
        assertEquals(0, workCounter)
    }

    @Test
    fun `Requested at first time - work started`() {
        val request = simpleRequest()
        request.getObservable("1").subscribe()
        scheduler.triggerActions()
        assertEquals(1, workCounter)
    }

    @Test
    fun `Requested 2 times one by one with same key - only one work started`() {
        val request = simpleRequest()
        lateinit var result1: Any
        lateinit var result2: Any

        request.getObservable(key = "1").subscribe { result1 = it }
        request.getObservable(key = "1").subscribe { result2 = it }
        scheduler.triggerActions()

        assertEquals(1, workCounter)
        assertEquals(result1, result2)
    }

    @Test
    fun `Requested 2 times with same key and 1 time with different key one by one - 2 works started`() {
        val request = simpleRequest()
        lateinit var result1: Any
        lateinit var result2: Any
        lateinit var result3: Any

        request.getObservable(key = "1").subscribe { result1 = it }
        request.getObservable(key = "1").subscribe { result2 = it }
        request.getObservable(key = "2").subscribe { result3 = it }
        scheduler.triggerActions()

        assertEquals(2, workCounter)
        assertEquals(result1, result2)
        assertNotEquals(result1, result3)
    }

    @Test
    fun `Requested 2 times one by one with different keys - both works started`() {
        val request = simpleRequest()
        lateinit var result1: Any
        lateinit var result2: Any

        request.getObservable(key = "1").subscribe { result1 = it }
        request.getObservable(key = "2").subscribe { result2 = it }
        scheduler.triggerActions()

        assertEquals(2, workCounter)
        assertNotEquals(result1, result2)
    }

    @Test
    fun `Requested 2 times with waiting - both works started`() {
        val request = simpleRequest()
        lateinit var result1: Any
        lateinit var result2: Any

        request.getObservable(key = "1").subscribe { result1 = it }
        scheduler.triggerActions()
        request.getObservable(key = "1").subscribe { result2 = it }
        scheduler.triggerActions()

        assertEquals(2, workCounter)
        assertNotEquals(result1, result2)
    }

    @Test
    fun `Requested 2 times with same key and 1 time with different key with waiting - 3 works started`() {
        val request = simpleRequest()
        lateinit var result1: Any
        lateinit var result2: Any
        lateinit var result3: Any

        request.getObservable(key = "1").subscribe { result1 = it }
        scheduler.triggerActions()
        request.getObservable(key = "1").subscribe { result2 = it }
        scheduler.triggerActions()
        request.getObservable(key = "2").subscribe { result3 = it }
        scheduler.triggerActions()

        assertEquals(3, workCounter)
        assertNotEquals(result1, result2)
        assertNotEquals(result1, result3)
    }

    @Test
    fun `Requested 2 times with same key and 1 time with different key - 2 works started`() {
        val request = longRunningRequest()
        lateinit var result1: Any
        lateinit var result2: Any
        lateinit var result3: Any

        request.getObservable(key = "1").subscribe { result1 = it }
        request.getObservable(key = "1").subscribe { result2 = it }
        request.getObservable(key = "2").subscribe { result3 = it }
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        assertEquals(2, workCounter)
        assertEquals(result1, result2)
        assertNotEquals(result1, result3)
    }

    @Test
    fun `Requested 3 times with same key and 1 time with different key and disposing - 2 works started`() {
        val request = longRunningRequest()
        var result1: Any? = null
        var result2: Any? = null
        var result3: Any? = null
        var result4: Any? = null

        val observable1 = request.getObservable(key = "1")
        val observable2 = request.getObservable(key = "2")

        val disposable1 = observable1.subscribe { result1 = it }
        scheduler.triggerActions()
        val disposable2 = observable1.subscribe { result2 = it }
        scheduler.triggerActions()
        disposable1.dispose()
        scheduler.triggerActions()
        val disposable3 = observable2.subscribe { result3 = it }
        val disposable4 = observable1.subscribe { result4 = it }
        scheduler.triggerActions()
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        assertEquals(2, workCounter)
        assertNull(result1)
        assertEquals(result2, result4)
        assertNotEquals(result2, result3)
    }

    @Test
    fun `Requested 3 times with same key and 1 time with different keys - 2 works started`() {
        val request = longRunningRequest()
        lateinit var result1: Any
        lateinit var result2: Any
        lateinit var result3: Any
        lateinit var result4: Any

        request.getObservable(key = "1").subscribe { result1 = it }
        request.getObservable(key = "1").subscribe { result2 = it }
        request.getObservable(key = "2").subscribe { result3 = it }
        request.getObservable(key = "1").subscribe { result4 = it }
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        assertEquals(2, workCounter)
        assertEquals(result1, result2)
        assertEquals(result1, result4)
        assertNotEquals(result1, result3)
    }

    @Test
    fun `Requested 3 times with same key and 1 time with different keys and 1 disposed - 2 works started`() {
        val request = longRunningRequest()
        var result1: Any? = null
        var result2: Any? = null
        var result3: Any? = null
        var result4: Any? = null

        val disposable1 = request.getObservable(key = "1").subscribe { result1 = it }
        val disposable2 = request.getObservable(key = "1").subscribe { result2 = it }
        val disposable3 = request.getObservable(key = "2").subscribe { result3 = it }
        val disposable4 = request.getObservable(key = "1").subscribe { result4 = it }
        disposable2.dispose()
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        assertEquals(2, workCounter)
        assertNull(result2)
        assertEquals(result1, result4)
        assertNotEquals(result1, result3)
    }

    @Test
    fun `Requested 3 times with same key and 1 time with different keys and again - 4 works started`() {
        val request = longRunningRequest()
        lateinit var result1: Any
        lateinit var result2: Any
        lateinit var result3: Any
        lateinit var result4: Any

        lateinit var result5: Any
        lateinit var result6: Any
        lateinit var result7: Any
        lateinit var result8: Any

        request.getObservable(key = "1").subscribe { result1 = it }
        request.getObservable(key = "1").subscribe { result2 = it }
        request.getObservable(key = "2").subscribe { result3 = it }
        request.getObservable(key = "1").subscribe { result4 = it }
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        request.getObservable(key = "1").subscribe { result5 = it }
        request.getObservable(key = "1").subscribe { result6 = it }
        request.getObservable(key = "2").subscribe { result7 = it }
        request.getObservable(key = "1").subscribe { result8 = it }
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        assertEquals(4, workCounter)

        assertEquals(result1, result2)
        assertEquals(result1, result4)
        assertNotEquals(result1, result3)

        assertEquals(result5, result6)
        assertEquals(result5, result8)
        assertNotEquals(result5, result7)

        assertNotEquals(result1, result5)
    }

    @Test
    fun `Requested 3 times with 1 key and 1 time with another and dispose 1 - Receive 2 results`() {
        val request = longRunningRequest()

        var result1: Any? = null
        lateinit var result2: Any
        lateinit var result3: Any
        lateinit var result4: Any

        val disposable1 = request.getObservable(key = "1").subscribe { result1 = it }
        val disposable2 = request.getObservable(key = "1").subscribe { result2 = it }
        val disposable3 = request.getObservable(key = "2").subscribe { result3 = it }
        val disposable4 = request.getObservable(key = "1").subscribe { result4 = it }
        disposable1.dispose()
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        assertEquals(2, workCounter)
        assertNull(result1)
        assertNotNull(result2)
        assertNotNull(result3)
        assertEquals(result2, result4)
        assertNotEquals(result1, result3)
    }

    @Test
    fun `Requested 3 times with 1 key and 1 time with another and dispose 2 times - Receive 2 results`() {
        val request = longRunningRequest()

        var result1: Any? = null
        lateinit var result2: Any
        var result3: Any? = null
        lateinit var result4: Any

        val disposable1 = request.getObservable(key = "1").subscribe { result1 = it }
        val disposable2 = request.getObservable(key = "1").subscribe { result2 = it }
        val disposable3 = request.getObservable(key = "2").subscribe { result3 = it }
        val disposable4 = request.getObservable(key = "1").subscribe { result4 = it }
        disposable1.dispose()
        disposable3.dispose()
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        assertEquals(1, workCounter)
        assertNull(result1)
        assertNull(result3)
        assertNotNull(result2)
        assertEquals(result2, result4)
    }

    @Test
    fun `Requested 3 times with 1 key and 1 time with another and dispose 2 times and request 1 more time - Receive 2 results`() {
        val request = longRunningRequest()

        var result1: Any? = null
        lateinit var result2: Any
        var result3: Any? = null
        lateinit var result4: Any
        lateinit var result5: Any

        val disposable1 = request.getObservable(key = "1").subscribe { result1 = it }
        val disposable2 = request.getObservable(key = "1").subscribe { result2 = it }
        val disposable3 = request.getObservable(key = "2").subscribe { result3 = it }
        val disposable4 = request.getObservable(key = "1").subscribe { result4 = it }
        disposable1.dispose()
        disposable3.dispose()
        val disposable5 = request.getObservable(key = "2").subscribe { result5 = it }
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        assertEquals(2, workCounter)
        assertNull(result1)
        assertNull(result3)
        assertNotNull(result2)
        assertNotNull(result5)
        assertEquals(result2, result4)
        assertNotEquals(result2, result5)
    }

    @Test
    fun `Error received - subscribe later normally`() {
        var shouldFail = false
        val request: JointObservable<Any, Any> =
            JointObservable(
                scheduler,
                work = { key ->
                    Observable
                        .timer(1, TimeUnit.SECONDS, scheduler)
                        .map {
                            if (shouldFail) {
                                throw RuntimeException("Sample fail")
                            }
                            workCounter++
                            Any()
                        }
                        .doOnComplete { println("doOnComplete") }
                        .doOnError { println("doOnError") }
                        .doOnDispose { println("doOnDispose") }
                }
            )

        var result1: Any? = null
        request.getObservable(key = "1").subscribe {  result1 = it }
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        assertEquals(1, workCounter)
        assertNotNull(result1)


        var result2: Any? = null
        var result3: Any? = null
        request.getObservable(key = "1").subscribe(
            { result -> result2 = result },
            { }
        )
        request.getObservable(key = "1").subscribe(
            { result -> result3 = result },
            { }
        )
        shouldFail = true
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        assertEquals(1, workCounter)
        assertNull(result2)
        assertNull(result3)

        var result4: Any? = null
        request.getObservable(key = "1").subscribe { result4 = it }
        shouldFail = false
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        assertEquals(2, workCounter)
        assertNotNull(result4)
    }

    private fun simpleRequest(): JointObservable<Any, Any> =
        JointObservable(scheduler) {
            Single.fromCallable {
                println("do work")
                workCounter++
                Any()
            }.toObservable()
        }

    private fun longRunningRequest(): JointObservable<Any, Any> =
        JointObservable(scheduler) { key ->
            Observable
                .timer(1, TimeUnit.SECONDS, scheduler)
                .map {
                    println("do work")
                    workCounter++
                    Any()
                }
        }
}
