package crocodile8008.rxutils

import crocodile8008.InstantRxSchedulersRule
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import org.junit.Rule
import org.junit.Test
import java.lang.RuntimeException

class WithRetryingTest {

    @get:Rule
    val instantRx = InstantRxSchedulersRule()

    private val successValue = 1
    private val fallbackValueDefault = 100
    private val error = RuntimeException()

    @Test
    fun `When error 2 times and retry 3 times - Then got success value`() {
        createStreamWithErrorsAndRetrying(errorsCount = 2, tryCnt = 3)
            .test()
            .assertValue(successValue)
    }

    @Test
    fun `When error 1 times and retry 2 times - Then got success value`() {
        createStreamWithErrorsAndRetrying(errorsCount = 1, tryCnt = 2)
            .test()
            .assertValue(successValue)
    }

    @Test
    fun `When error 3 times and retry 4 times - Then got success value`() {
        createStreamWithErrorsAndRetrying(errorsCount = 3, tryCnt = 4)
            .test()
            .assertValue(successValue)
    }

    @Test
    fun `When no errors and retry 5 times - Then got success value`() {
        createStreamWithErrorsAndRetrying(errorsCount = 0, tryCnt = 5)
            .test()
            .assertValue(successValue)
    }

    @Test
    fun `When error 3 times and retry 3 times - Then got fallback value`() {
        createStreamWithErrorsAndRetrying(errorsCount = 3, tryCnt = 3)
            .test()
            .assertValue(fallbackValueDefault)
    }

    @Test
    fun `When error 3 times and retry 1 times - Then got fallback value`() {
        createStreamWithErrorsAndRetrying(errorsCount = 3, tryCnt = 1)
            .test()
            .assertValue(fallbackValueDefault)
    }

    @Test
    fun `When error 3 times and retry 3 times without fallback - Then got error`() {
        createStreamWithErrorsAndRetrying(errorsCount = 3, tryCnt = 3, fallbackValue = null)
            .test()
            .assertError(error)
    }

    @Test
    fun `When error 3 times and retry 1 times without fallback - Then got error`() {
        createStreamWithErrorsAndRetrying(errorsCount = 3, tryCnt = 1, fallbackValue = null)
            .test()
            .assertError(error)
    }

    @Test
    fun `When error 3 times and retry 0 times - Then got error`() {
        createStreamWithErrorsAndRetrying(errorsCount = 3, tryCnt = 0)
            .test()
            .assertError(error)
    }

    @Test
    fun `When error 3 times and retry 0 times without fallback - Then got error`() {
        createStreamWithErrorsAndRetrying(errorsCount = 3, tryCnt = 0, fallbackValue = null)
            .test()
            .assertError(error)
    }

    private fun createStreamWithErrorsAndRetrying(
        errorsCount: Int,
        tryCnt: Int,
        fallbackValue: Int? = fallbackValueDefault
    ): Observable<Int> {
        return createObservableWithErrorsAtFirst(errorsCount = errorsCount)
            .withRetrying(
                fallbackValue = fallbackValue,
                tryCnt = tryCnt,
                intervalMillis = { 0L },
            )
    }

    private fun createObservableWithErrorsAtFirst(errorsCount: Int): Observable<Int> {
        var emitCount = 0
        val stream = Observable.create { emitter: ObservableEmitter<Int> ->
            emitCount++
            if (emitCount < errorsCount + 1) {
                emitter.tryOnError(error)
            } else {
                emitter.onNext(successValue)
            }
        }
        return stream
    }
}