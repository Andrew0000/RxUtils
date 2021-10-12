@file:Suppress("unused")

package crocodile8008.rxutils

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.TimeUnit


fun <T : Any> Completable.withRetrying(
    tryCnt: Int,
    intervalMillis: (tryCnt: Int) -> Long,
    retryCheck: (Throwable) -> Boolean = { true }
): Completable = this
    .toObservable<Any>()
    .withRetrying(null, tryCnt, intervalMillis, retryCheck)
    .ignoreElements()

fun <T : Any> Single<T>.withRetrying(
    fallbackValue: T?,
    tryCnt: Int,
    intervalMillis: (tryCnt: Int) -> Long,
    retryCheck: (Throwable) -> Boolean = { true }
): Single<T> = this
    .toObservable()
    .withRetrying(fallbackValue, tryCnt, intervalMillis, retryCheck)
    .firstOrError()

fun <T : Any> Observable<T>.withRetrying(
    fallbackValue: T?,
    tryCnt: Int,
    intervalMillis: (tryCnt: Int) -> Long,
    retryCheck: (Throwable) -> Boolean = { true }
): Observable<T> {
    if (tryCnt <= 0) {
        return this
    }
    return this
        .retryWhen { errors ->
            errors
                .zipWith(
                    Observable.range(1, tryCnt),
                    { th: Throwable, attempt: Int ->
                        if (retryCheck(th) && attempt < tryCnt) {
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
