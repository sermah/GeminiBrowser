package dev.sermah.geminibrowser

object InstanceProvider {
    private val lazySingles = mutableMapOf<Class<out Any>, Lazy<Any>>()

    private val map = mutableMapOf<Class<out Any>, () -> Any>()

    operator fun <T> get(klass: Class<T>): T = checkNotNull(map[klass]?.invoke() as? T)

    fun provide(block: ProvideDsl.() -> Unit) = ProvideDsl().apply(block)

    class ProvideDsl {
        /** Lazily provide a singleton */
        fun <T : Any> single(klass: Class<T>, block: () -> Any) {
            check(klass !in lazySingles) { "Class $klass is already registered" }
            lazySingles[klass] = lazy(block)

            map[klass] = { lazySingles[klass]!!.value }
        }

        /** Lazily provide an instance on each ::get call */
        fun <T : Any> many(klass: Class<T>, block: () -> Any) {
            map[klass] = block
        }
    }
}