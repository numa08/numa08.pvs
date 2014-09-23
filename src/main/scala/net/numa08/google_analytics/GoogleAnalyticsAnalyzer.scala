package net.numa08.google_analytics

import java.util.Date

import akka.actor.Actor
import akka.actor.Actor.Receive
import com.google.api.services.analytics.Analytics
import net.numa08.analyzer.{PVAnalyzer,PVAnalyzerResult}

class GoogleAnalyticsAnalyzer extends PVAnalyzer {
  def analyze(identifiers : List[String]) : List[Either[Throwable, PVAnalyzerResult]] = ???
}

sealed class Analyzer extends Actor {
  override def receive: Receive = {
    case Analyze(analytics, id) => {

    }
  }

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
