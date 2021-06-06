package crocodile8008.rxutils.joint

import crocodile8008.rxutils.memcache.MemCache
import crocodile8008.rxutils.memcache.OneValueCache
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

@Suppress("UNCHECKED_CAST", "UNUSED")
class JointSingleSimple<V : Any>(
    private val cache: MemCache<String>,
    private val workObservationScheduler: Scheduler = Schedulers.io(),
    private val work: (key: String) -> Single<V>
) {
    companion object {

        fun <V : Any> cached(
            workObservationScheduler: Scheduler = Schedulers.io(),
            work: (key: String) -> Single<V>
        ) = JointSingleSimple(
            cache = OneValueCache(),
            workObservationScheduler = workObservationScheduler,
            work = work
        )

        fun <V : Any> notCached(
            workObservationScheduler: Scheduler = Schedulers.io(),
            work: (key: String) -> Single<V>
        ) = JointSingleSimple(
            cache = MemCache.EMPTY as MemCache<String>,
            workObservationScheduler = workObservationScheduler,
            work = work
        )
    }

    private val request = JointSingle(
        cache,
        workObservationScheduler,
        work
    )

    fun getSingle(): Single<V> = request.getSingle("")
}