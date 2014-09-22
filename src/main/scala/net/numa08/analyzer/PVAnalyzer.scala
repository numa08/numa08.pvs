package net.numa08.analyzer

trait PVAnalyzer {
  def analyze : Either[Throwable, PVAnalyzerResult]
}

case class PVAnalyzerResult(pv : Int, id : String)