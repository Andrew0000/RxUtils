@file:Suppress("unused")

package crocodile8008.rxutils.android

import crocodile8008.rxutils.fromIo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

fun <T : Any> Single<T>.fromIoToMain(): Single<T> = this
    .fromIo()
    .toMain()

fun <T : Any> Observable<T>.fromIoToMain(): Observable<T> = this
    .fromIo()
    .toMain()

fun Completable.fromIoToMain(): Completable = this
    .fromIo()
    .toMain()

fun <T : Any> Maybe<T>.fromIoToMain(): Maybe<T> = this
    .fromIo()
    .toMain()

fun <T : Any> Observable<T>.toMain(): Observable<T> = this
    .observeOn(AndroidSchedulers.mainThread())

fun <T : Any> Single<T>.toMain(): Single<T> = this
    .observeOn(AndroidSchedulers.mainThread())

fun Completable.toMain(): Completable = this
    .observeOn(AndroidSchedulers.mainThread())

fun <T : Any> Maybe<T>.toMain(): Maybe<T> = this
    .observeOn(AndroidSchedulers.mainThread())
