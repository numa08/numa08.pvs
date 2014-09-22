package net.numa08.analyzer

object PVAnalyzerFactory {
  
  private val analyzers = Map("google-analytics" -> {() => new net.numa08.google_analytics.GoogleAnalyticsAnalyzer()})

  def analyzerByName(name: String) : PVAnalyzer = {
    analyzers.get(name) match {
      case None => throw new PVAnalyzerNotFoundException(s"Can not find $name analyzer")
      case Some(a) => a()
    }
  }
}

class PVAnalyzerNotFoundException(m : String) extends Exception(m)