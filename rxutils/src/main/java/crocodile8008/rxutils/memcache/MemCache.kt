package crocodile8008.rxutils.memcache

interface MemCache<K> {

    operator fun <T> get(key: K): T?

    operator fun set(key: K, value: Any)

    fun clear()

    companion object {
        val EMPTY = object : MemCache<Any> {
            override fun <T> get(key: Any): T? = null

            override fun set(key: Any, value: Any) {}

            override fun clear() {}
        }
    }
}
