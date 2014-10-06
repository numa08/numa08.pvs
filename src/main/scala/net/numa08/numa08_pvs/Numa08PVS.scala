package net.numa08.numa08_pvs

import net.numa08.analyzer.PVAnalyzerFactory
import com.typesafe.config.ConfigFactory

object Num08PVS {

  def main(args: Array[String]) :Unit = {
    val config = ConfigFactory.load

    // // analyze by google analytics
    val googleAnalytics = PVAnalyzerFactory.analyzerByName("google-analytics")
    val analyzeResults = googleAnalytics.analyze(config)

    // // report to twitter
    // val twitter = PVReporterFactory.reporterByName("twitter")
    // val reportResults = analyzeResults.right{r => twitter.reportAnalyzeResult(r)}

    // reportResults match {
    //   case Left(e) => //printerr
    //   case _ => //no error
    // }
    
  }

}