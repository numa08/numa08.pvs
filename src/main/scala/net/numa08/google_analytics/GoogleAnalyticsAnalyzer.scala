package net.numa08.google_analytics

import scala.util.control.Exception._
import scala.collection.JavaConversions._

import java.util.Date

import akka.actor.Actor
import com.google.api.services.analytics.Analytics
import net.numa08.analyzer.{PVAnalyzer, PVAnalyzerResult}
import org.apache.commons.lang3.time.DateUtils

class GoogleAnalyticsAnalyzer extends PVAnalyzer {
  def analyze(identifiers : List[String]) : List[Either[Throwable, PVAnalyzerResult]] = ???
}

sealed class Analyzer extends Actor with AnalyticsQuery {
  override def receive: Receive = {
    case Analyze(analytics, id) => allCatch either {
      val gaData = analytics.data().ga().get(queryById(id), startDateQuery, endDateQuery, metrics).execute()
      val pv = gaData.getRows.head.head.toInt
      val result = PVAnalyzerResult(pv, id)
      sender() ! result
    }
  }
  case class Analyze(analytics : Analytics, id : String)

  override def startDate: Date = targetDate

  override def endDate: Date = targetDate

  private val targetDate = DateUtils.addDays(new Date(), -1)
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
