@file:Suppress("unused")

package crocodile8008.rxutils

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

// Observable

fun <T : Any> Observable<T>.fromComputation(): Observable<T> = this
    .subscribeOn(Schedulers.computation())

fun <T : Any> Observable<T>.toComputation(): Observable<T> = this
    .observeOn(Schedulers.computation())

fun <T : Any> Observable<T>.fromIo(): Observable<T> = this
    .subscribeOn(Schedulers.io())

fun <T : Any> Observable<T>.toIo(): Observable<T> = this
    .observeOn(Schedulers.io())

// Single

fun <T : Any> Single<T>.fromComputation(): Single<T> = this
    .subscribeOn(Schedulers.computation())

fun <T : Any> Single<T>.toComputation(): Single<T> = this
    .observeOn(Schedulers.computation())

fun <T : Any> Single<T>.fromIo(): Single<T> = this
    .subscribeOn(Schedulers.io())

fun <T : Any> Single<T>.toIo(): Single<T> = this
    .observeOn(Schedulers.io())

// Completable

fun Completable.fromComputation(): Completable = this
    .subscribeOn(Schedulers.computation())

fun Completable.toComputation(): Completable = this
    .observeOn(Schedulers.computation())

fun Completable.fromIo(): Completable = this
    .subscribeOn(Schedulers.io())

fun Completable.toIo(): Completable = this
    .observeOn(Schedulers.io())

// Maybe

fun <T : Any> Maybe<T>.fromComputation(): Maybe<T> = this
    .subscribeOn(Schedulers.computation())

fun <T : Any> Maybe<T>.toComputation(): Maybe<T> = this
    .observeOn(Schedulers.computation())

fun <T : Any> Maybe<T>.fromIo(): Maybe<T> = this
    .subscribeOn(Schedulers.io())

fun <T : Any> Maybe<T>.toIo(): Maybe<T> = this
    .observeOn(Schedulers.io())

