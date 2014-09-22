package net.numa08.reporter

import net.numa08.twitter.TwitterReporter

import org.scalatest.FunSuite

class PVReporterFactoryTest extends FunSuite {
  
  test("Find reporter by name") {
    val reporterName = "twitter"
    val reporter = PVReporterFactory.reporterByName(reporterName)
    assert(reporter.isInstanceOf[TwitterReporter])
  }
}