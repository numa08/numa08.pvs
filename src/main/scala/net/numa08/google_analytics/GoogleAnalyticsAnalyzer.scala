package net.numa08.google_analytics

import scala.util.control.Exception._
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import java.util.Date

import akka.actor.{Props, ActorSystem, Actor}
import akka.pattern.ask
import akka.util.Timeout
import com.google.api.services.analytics.Analytics
import net.numa08.analyzer.{PVAnalyzer, PVAnalyzerResult}
import org.apache.commons.lang3.time.DateUtils
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import net.numa08.google_analytics.Analyzer.Analyze
import scala.concurrent.{Future, Await}


class GoogleAnalyticsAnalyzer extends PVAnalyzer {
  implicit val analyzeTimeout = Timeout(5 minutes)

  def analyze(identifiers : List[String]) : List[Either[Throwable, PVAnalyzerResult]] = {
    val credentials : Credential = ???
    val transport = GoogleNetHttpTransport.newTrustedTransport
    val jsonFactory = JacksonFactory.getDefaultInstance
    val actorSystem = ActorSystem.create("google-analytics")
    val actor = actorSystem.actorOf(Props[Analyzer], "analytics-analyzer")
    val analytics = new Analytics.Builder(transport, jsonFactory, credentials).setApplicationName("analytics-analyzer").build()
    val futures = identifiers.map{i => (actor ? Analyze(analytics, i)).mapTo[Either[Throwable, PVAnalyzerResult]]}
    Await.result(Future.sequence(futures), analyzeTimeout.duration)
  }
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


  override def startDate: Date = targetDate

  override def endDate: Date = targetDate

  private val targetDate = DateUtils.addDays(new Date(), -1)
}

private object Analyzer {
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
