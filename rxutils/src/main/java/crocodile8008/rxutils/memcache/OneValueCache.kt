package crocodile8008.rxutils.memcache

open class OneValueCache<K> : MaxSizeCache<K>(maxSize = 1)
