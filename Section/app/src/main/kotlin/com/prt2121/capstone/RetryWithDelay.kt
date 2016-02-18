package com.prt2121.capstone

import rx.Observable
import rx.Observable.Transformer
import rx.functions.Func1
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by pt2121 on 2/16/16.
 */
class RetryWithDelay(maxRetries: Int, retryDelayMillis: Long) : Func1<Observable<out Throwable>, Observable<*>> {
  private var maxRetries: Int = 0
  private var retryDelayMillis: Long = 0
  private var retryCount: Int = 0

  init {
    this.maxRetries = maxRetries
    this.retryDelayMillis = retryDelayMillis
    this.retryCount = 0
  }

  override fun call(attempts: Observable<out Throwable>): Observable<*> {
    return attempts.flatMap { throwable ->
      if (++retryCount < maxRetries) {
        // When this Observable calls onNext, the original
        // Observable will be retried (i.e. re-subscribed).
        Observable.timer(retryDelayMillis, TimeUnit.MILLISECONDS)
      } else
      // Max retries hit. Just pass the error along.
        Observable.error(throwable as Throwable?)
    }
  }
}

/**
 * @param interval The base interval to start backing off from. The function is: attemptNum^2 * intervalTime
 * @param units The units for interval
 * @param retryAttempts The max number of attempts to retry this task or -1 to try MAX_INT times,
 */
fun <T> backoff(interval: Long, units: TimeUnit, retryAttempts: Int): Observable.Transformer<T, T> =
    Transformer { observable ->
      observable.retryWhen(
          retryFunc(interval, units, retryAttempts),
          Schedulers.immediate())
    }

private fun retryFunc(interval: Long, units: TimeUnit, attempts: Int): Func1<in Observable<out Throwable>, out Observable<*>> =
    Func1<rx.Observable<out Throwable>, rx.Observable<Long>> { observable ->
      // zip our number of retries to the incoming errors so that we only produce retries
      // when there's been an error
      observable.zipWith(
          Observable.range(1, if (attempts > 0) attempts else Int.MAX_VALUE),
          { throwable, attemptNumber -> attemptNumber }
      )
          .flatMap {
            var newInterval = interval * (it.toLong() * it.toLong())
            if (newInterval < 0) {
              newInterval = Long.MAX_VALUE
            }
            // use Schedulers#immediate() to keep on same thread
            Observable.timer(newInterval, units, Schedulers.immediate())
          }
    }