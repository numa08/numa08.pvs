package net.numa08.google_analytics

import akka.actor.Actor
import akka.actor.Actor.Receive
import net.numa08.analyzer.{PVAnalyzer,PVAnalyzerResult}

class GoogleAnalyticsAnalyzer extends PVAnalyzer {
  def analyze(identifiers : List[String]) : List[Either[Throwable, PVAnalyzerResult]] = ???
}

sealed class Analyzer extends Actor {
  override def receive: Receive = {
    case Analyze(id) => {}
  }

  case class Analyze(id : String)
}

