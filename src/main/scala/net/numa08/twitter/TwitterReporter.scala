package net.numa08.twitter

import net.numa08.analyzer.PVAnalyzerResult
import net.numa08.reporter.{PVReporter, PVReporterResult}

class TwitterReporter extends PVReporter {
  
  def reporteAnalyzerResult(result : PVAnalyzerResult) : Either[Throwable, PVReporterResult] = null

}