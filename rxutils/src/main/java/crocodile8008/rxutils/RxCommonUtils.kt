@file:Suppress("unused")

package crocodile8008.rxutils

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

fun <T> BehaviorSubject<T>.updateUnsafe(updater: (T?) -> Unit) {
    val valueCopy = value
    updater(valueCopy)
    onNext(valueCopy)
}

fun <T> BehaviorSubject<T>.update(updater: (T?) -> Unit) {
    synchronized(this) {
        updateUnsafe(updater)
    }
}

fun <T : Any> Observable<T>.doOnNextOnce(action: () -> Unit): Observable<T> {
    var nextCounter = 0
    return doOnNext {
        synchronized(this) {
            if (nextCounter < 1) {
                action()
                nextCounter++
            }
        }
    }
}

fun <T : Any> Observable<T>.withRetrying(
    fallbackValue: T?,
    retryCnt: Int,
    intervalMillis: (tryCnt: Int) -> Long,
    retryCheck: (Throwable) -> Boolean = { true }
): Observable<T> {
    if (retryCnt <= 0) {
        return this
    }
    return this
        .retryWhen { errors ->
            errors
                .zipWith(
                    Observable.range(1, retryCnt),
                    { th: Throwable, attempt: Int ->
                        if (retryCheck(th) && attempt < retryCnt) {
                            Observable.timer(intervalMillis(attempt), TimeUnit.MILLISECONDS)
                        } else {
                            Observable.error(th)
                        }
                    }
                )
                .flatMap { retryCount: Observable<Long> -> retryCount }
        }
        .let {
            if (fallbackValue == null) {
                it
            } else {
                it.onErrorResumeNext { Observable.just(fallbackValue) }
            }
        }
}
