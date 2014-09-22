package net.numa08.reporter

object PVReporterFactory {
  
  private val reporters = Map("twitter" -> {() => new net.numa08.twitter.TwitterReporter()})

  def reporterByName(name : String) : PVReporter = {
    reporters.get(name) match {
      case None => throw new PVReporterNotFoundException(s"Can not find $name reporter")
      case Some(r) => r()
    }
    
  }
}

class PVReporterNotFoundException(m : String) extends Exception(m)