package net.numa08.analyzer

import net.numa08.google_analytics.GoogleAnalyticsAnalyzer

import org.scalatest.FunSuite

class PVAnalyzerFactoryTest extends FunSuite {
  
  test("Find analyzer by name") {
    val analyzerName = "google-analytics"
    val analyzer = PVAnalyzerFactory.analyzerByName(analyzerName)
    assert(analyzer.isInstanceOf[GoogleAnalyticsAnalyzer])
  }
}