package net.numa08.google_analytics

import java.util.Date

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.google.api.services.analytics.Analytics
import net.numa08.analyzer.{PVAnalyzer, PVAnalyzerResult}
import net.numa08.google_analytics.Analyzer.Analyze
import org.apache.commons.lang3.time.DateUtils

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.control.Exception._

class GoogleAnalyticsAnalyzer extends PVAnalyzer {
  def analyze(identifiers : List[String]) : List[Either[Throwable, PVAnalyzerResult]] = {
    val actorSystem = ActorSystem.create("googleanalytics")
    val actor = actorSystem.actorOf(Props[Analyzer], "analyzer")
    val analytics : Analytics = ???
    implicit val timeout = Timeout(5 minutes)
    val futures = identifiers.map{id =>
      val message = Analyze(analytics, id)
      (actor ? message).mapTo[Either[Throwable, PVAnalyzerResult]]
    }
    Await.result(Future.sequence(futures), timeout.duration)
  }
}

sealed class Analyzer extends Actor with AnalyticsQuery {
  override def receive: Receive = {
    case Analyze(analytics, id) => {
      val result = allCatch either {
        val gaData = analytics.data().ga().get(queryById(id), startDateQuery, endDateQuery, metrics).execute()
        val pv = gaData.getRows.head.head.toInt
        PVAnalyzerResult(pv, id)
      }
      sender() ! result
    }

  }

  override def startDate: Date = targetDate

  override def endDate: Date = targetDate

  private val targetDate = DateUtils.addDays(new Date(), -1)
}

object Analyzer {
  case class Analyze(analytics : Analytics, id : String)
}

protected trait AnalyticsQuery {
  def startDate : Date
  def endDate : Date

  def queryById(id : String) : String = s"ga:$id"

  def metrics : String = "ga:pageviews"

  private val queryDateString = (d : Date) => "%tY-%<tm-%<td".format(d)

  def startDateQuery : String = queryDateString(startDate)
  def endDateQuery : String = queryDateString(endDate)
}
