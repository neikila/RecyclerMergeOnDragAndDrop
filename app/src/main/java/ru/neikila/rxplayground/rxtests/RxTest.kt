package ru.neikila.rxplayground.rxtests

import android.util.Log
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class RxTest {
    private val tag = "TEST123"

    private val longObservable = Observable.interval(0, 10, TimeUnit.SECONDS)
        .doOnNext {
            Log.d(tag, "longObservable.doOnNext $it")
            relay.onNext(it * 10L)
        }
        .switchMap { relay }
        .share()

    private val relay = BehaviorSubject.createDefault(2L)

    data class SomeData(val num: Int, val text: String)

    fun start() {
        longObservable.subscribe()
        Log.d(tag, "Start")
//        val p1 = Observable.interval(0, 10, TimeUnit.SECONDS)
//            .filter {
//                Log.d(tag, "filter $it")
//                true
//            }
//            .switchMap {
//                if (it <= 1) Observable.just(Wrapper.Some(it))
//                else Observable.just(Wrapper.None<Long>())
//            }
//            .map { it.map { SomeData(it.toInt(), "a") } }
//
//
        relay.subscribe {
            Log.d(tag, "relay emit $it")
        }
    }

}

sealed class Optional<T> {
    abstract fun <R> map(action: (T) -> R): Optional<R>

    data class Some<T>(val data: T): Optional<T>() {
        override fun <R> map(action: (T) -> R): Optional<R> = Some(action(data))

        override fun toString(): String = "Some($data)"
    }

    class None<T>: Optional<T>() {
        override fun <R> map(action: (T) -> R): Optional<R> = None()

        override fun toString(): String = "None"
    }
}
