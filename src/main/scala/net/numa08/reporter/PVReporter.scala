package net.numa08.reporter

import net.numa08.analyzer.PVAnalyzerResult

trait PVReporter {
  def reporteAnalyzerResult(result : PVAnalyzerResult) : Either[Throwable, PVReporterResult]
}

case class PVReporterResult(message : String, id : Long)