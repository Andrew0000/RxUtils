@file:Suppress("unused")

package crocodile8008.rxutils

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

fun <T : Any> Observable<T>.toComputation(): Observable<T> = this
    .observeOn(Schedulers.computation())

fun <T : Any> Observable<T>.toIo(): Observable<T> = this
    .observeOn(Schedulers.io())
