@file:Suppress("unused")

package crocodile8008.rxutils.subscribe

import crocodile8008.rxutils.log.RxUtilsLog
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable

fun <T : Any> Observable<T>.subscribeSafe(
    onNext: (T) -> Unit = {},
): Disposable =
    subscribe(
        { onNext(it) },
        { RxUtilsLog.e(throwable = it) }
    )

fun <T : Any> Single<T>.subscribeSafe(
    onSuccess: (T) -> Unit = {},
): Disposable =
    subscribe(
        { onSuccess(it) },
        { RxUtilsLog.e(throwable = it) }
    )

fun Completable.subscribeSafe(
    onSuccess: () -> Unit = {},
): Disposable =
    subscribe(
        { onSuccess() },
        { RxUtilsLog.e(throwable = it) }
    )
