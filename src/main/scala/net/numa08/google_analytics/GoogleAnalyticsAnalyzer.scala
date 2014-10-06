package net.numa08.google_analytics

import scala.util.control.Exception._
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import java.util.{Collections, Date}

import akka.actor.{Props, ActorSystem, Actor}
import akka.pattern.ask
import akka.util.Timeout
import com.google.api.services.analytics.{AnalyticsScopes, Analytics}
import net.numa08.analyzer.{PVAnalyzer, PVAnalyzerResult}
import net.numa08.google_analytics.Analyzer.Analyze
import org.apache.commons.lang3.time.DateUtils
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import net.numa08.google_analytics.Analyzer.Analyze
import scala.concurrent.{Future, Await}
import net.numa08.google_analytics.GoogleCredential.GoogleCredentialInfo
import com.google.api.client.http.HttpTransport
import java.io.{InputStreamReader, File, FileInputStream}
import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleClientSecrets}
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow.Builder
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.typesafe.config.Config


class GoogleAnalyticsAnalyzer extends PVAnalyzer with GoogleCredential {
  implicit val analyzeTimeout = Timeout(5.minutes)

  case class CredentialFileNotFoundException(m : String) extends Exception(m)

  def analyze(config : Config) : List[Either[Throwable, PVAnalyzerResult]] = {
    val credFilePath = config.getString("googleanalytics.credfile")
    val credFile = new File(credFilePath)
    if (!credFile.exists()) {
      throw new CredentialFileNotFoundException(s"Cred file donesn't exists at $credFilePath")
    }
    val transport = GoogleNetHttpTransport.newTrustedTransport
    val jsonFactory = JacksonFactory.getDefaultInstance
    val credentials : Credential = credential(GoogleCredentialInfo(credFile), jsonFactory, transport) match {
      case Left(e) => throw e
      case Right(c) => c
    }
    val actorSystem = ActorSystem.create("google-analytics")
    val actor = actorSystem.actorOf(Props[Analyzer], "analytics-analyzer")
    val analytics = new Analytics.Builder(transport, jsonFactory, credentials).setApplicationName("analytics-analyzer").build()
    val identifiers = config.getStringList("googleanalytics.identifiers").toList
    val futures = identifiers.map{i => (actor ? Analyze(analytics, i)).mapTo[Either[Throwable, PVAnalyzerResult]]}
    Await.result(Future.sequence(futures), analyzeTimeout.duration)
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

sealed trait GoogleCredential {

  def credential(info : GoogleCredentialInfo, jsonFactory : JacksonFactory, transport : HttpTransport) : Either[Throwable, Credential]= allCatch either {
    val fileIn = new FileInputStream(info.json)
    val secret = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(fileIn))
    val flow = new Builder(transport, jsonFactory, secret, Collections.singleton(AnalyticsScopes.ANALYTICS_READONLY)).build()
    new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user")
  }
}

private object GoogleCredential {
  case class GoogleCredentialInfo(json : File)
}
