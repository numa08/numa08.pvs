package net.numa08.analyzer

import com.typesafe.config.Config

trait PVAnalyzer {
  def analyze(config : Config) : List[Either[Throwable, PVAnalyzerResult]]
}

case class PVAnalyzerResult(pv : Int, id : String)