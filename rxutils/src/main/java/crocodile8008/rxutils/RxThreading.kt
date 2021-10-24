@file:Suppress("unused")

package crocodile8008.rxutils

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

// Observable

fun <T : Any> Observable<T>.fromComputation(): Observable<T> = subscribeOn(Schedulers.computation())

fun <T : Any> Observable<T>.toComputation(): Observable<T> = observeOn(Schedulers.computation())

fun <T : Any> Observable<T>.fromIo(): Observable<T> = subscribeOn(Schedulers.io())

fun <T : Any> Observable<T>.toIo(): Observable<T> = observeOn(Schedulers.io())

// Single

fun <T : Any> Single<T>.fromComputation(): Single<T> = subscribeOn(Schedulers.computation())

fun <T : Any> Single<T>.toComputation(): Single<T> = observeOn(Schedulers.computation())

fun <T : Any> Single<T>.fromIo(): Single<T> = subscribeOn(Schedulers.io())

fun <T : Any> Single<T>.toIo(): Single<T> = observeOn(Schedulers.io())

// Completable

fun Completable.fromComputation(): Completable = subscribeOn(Schedulers.computation())

fun Completable.toComputation(): Completable = observeOn(Schedulers.computation())

fun Completable.fromIo(): Completable = subscribeOn(Schedulers.io())

fun Completable.toIo(): Completable = observeOn(Schedulers.io())

// Maybe

fun <T : Any> Maybe<T>.fromComputation(): Maybe<T> = subscribeOn(Schedulers.computation())

fun <T : Any> Maybe<T>.toComputation(): Maybe<T> = observeOn(Schedulers.computation())

fun <T : Any> Maybe<T>.fromIo(): Maybe<T> = subscribeOn(Schedulers.io())

fun <T : Any> Maybe<T>.toIo(): Maybe<T> = observeOn(Schedulers.io())

