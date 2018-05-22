package uk.co.tradingdevelopment.trading.scala.rss

import rx.lang.scala._
import rx.lang.scala.observables._
import rx.lang.scala.schedulers._

trait RxStreamer[A]{
 def start:Observable[A]
def stop
}