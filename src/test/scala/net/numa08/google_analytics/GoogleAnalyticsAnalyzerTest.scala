package net.numa08.google_analytics

import com.typesafe.config.ConfigFactory
import net.numa08.analyzer.PVAnalyzerFactory
import org.scalatest.{Matchers, FlatSpec}

class GoogleAnalyticsAnalyzerTest  extends FlatSpec with Matchers{

  "analyzer" should "be analyze" in {
    val analyzerName = "google-analytics"
    val analyzer = PVAnalyzerFactory.analyzerByName(analyzerName)
    val result = analyzer.analyze(ConfigFactory.load())
    result.nonEmpty should be (true)
    result.head.isRight should be (true)
  }
}
