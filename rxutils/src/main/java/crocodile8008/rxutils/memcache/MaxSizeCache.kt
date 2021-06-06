package crocodile8008.rxutils.memcache

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

open class MaxSizeCache<K>(
    private val maxSize: Int
) : MemCache<K> {

    private val lock = ReentrantReadWriteLock()
    private var cache = mutableMapOf<K, Any>()

    override fun set(key: K, value: Any) {
        lock.write {
            cache[key] = value
            val exceedBy = cache.size - maxSize
            if (exceedBy > 0) {
                val keysToRemove = cache.entries.take(exceedBy).map { it.key }
                keysToRemove.forEach {
                    cache.remove(it)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(key: K): T? =
        lock.read {
            cache[key] as? T
        }

    override fun clear() {
        lock.write {
            cache.clear()
        }
    }
}
