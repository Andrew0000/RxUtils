package crocodile8008.rxutils.joint

import crocodile8008.rxutils.log.RxUtilsLog
import crocodile8008.rxutils.log.RxUtilsLogTarget
import crocodile8008.rxutils.memcache.MemCache
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

@Suppress("UNCHECKED_CAST", "UNUSED")
class JointSingle<K, V : Any>(
    private val cache: MemCache<K> = MemCache.EMPTY as MemCache<K>,
    private val workObservationScheduler: Scheduler = Schedulers.io(),
    private val work: (key: K) -> Single<V>
) {

    private val requests = HashMap<K, Observable<V>>()

    fun getSingle(key: K): Single<V> {
        cache.get<V>(key)?.let { cached ->
            log("cached", key)
            return@getSingle Single.just(cached)
        }

        return Observable
            .defer {
                synchronized(requests) {
                    requests[key]?.let { existing ->
                        log("exists", key)
                        return@defer existing
                    }

                    val newRequest = work(key)
                        .observeOn(workObservationScheduler)
                        .doOnSuccess {
                            cache[key] = it
                        }
                        .doFinally {
                            log("completed", key)
                            synchronized(requests) { requests.remove(key) }
                        }
                        .toObservable()
                        .replay(1)
                        .refCount()

                    requests[key] = newRequest
                    log("new", key)
                    return@defer newRequest
                }
            }
            .firstOrError()
    }

    private fun log(msg: String, key: K) {
        if (RxUtilsLog.target != RxUtilsLogTarget.EMPTY) {
            RxUtilsLog.d("[JointSingle] $msg: $key")
        }
    }
}
