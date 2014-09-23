package net.numa08.google_analytics

import java.text.SimpleDateFormat
import java.util.Date
import org.scalatest._

class AnalyticsQueryTest extends FlatSpec with Matchers{

  val mockQuery = new AnalyticsQuery {
    override def startDate: Date = new SimpleDateFormat("yyyy-MM-dd").parse("2014-09-23")

    override def endDate: Date = startDate
  }

  "Query string" should "be created by analytics id" in {
    mockQuery.queryById("U-1111") should be ("ga:U-1111")
  }

  "Start query date format" should "be formatted yyyy-MM-dd" in {
    mockQuery.startDateQuery should be ("2014-09-23")
  }

  "End query date format" should "be formatted yyyy-MM-dd" in {
    mockQuery.endDateQuery should be ("2014-09-23")
  }
}
