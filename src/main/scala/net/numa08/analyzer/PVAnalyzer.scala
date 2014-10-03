package net.numa08.analyzer

trait PVAnalyzer {
  def analyze(identifiers : List[String], configure : Map[String, String]) : List[Either[Throwable, PVAnalyzerResult]]
}

case class PVAnalyzerResult(pv : Int, id : String)