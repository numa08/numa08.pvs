package net.numa08.google_analytics

import net.numa08.analyzer.{PVAnalyzer,PVAnalyzerResult}

class GoogleAnalyticsAnalyzer extends PVAnalyzer {
  def analyze : Either[Throwable, PVAnalyzerResult] = Left(null)
}