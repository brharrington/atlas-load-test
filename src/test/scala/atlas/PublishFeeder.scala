package atlas

import io.gatling.core.feeder.Feeder
import io.gatling.core.feeder.Record

import scala.util.Random

class PublishFeeder(name: String, node: Int, numMetrics: Int) extends Feeder[String] {

  private val nodeId = f"i-$node%017x"
  private val values = (0 until 7919).map(_ => Random.nextDouble()).toArray
  private var i = 0

  private val commonTags =
    s"""
      |{
      |  "nf.app":       "foo",
      |  "nf.cluster":   "foo-bar",
      |  "nf.asg":       "foo-bar-v000",
      |  "nf.stack":     "bar",
      |  "nf.node":      "$nodeId",
      |  "nf.region":    "us-east-1",
      |  "nf.zone":      "us-east-1a",
      |  "nf.vmtype":    "r3.2xlarge"
      |}
    """.stripMargin

  private def metric(j: Int, timestamp: Long, value: Double) = {
    val mname = f"$j%08x"
    s"""
      |{
      |  "tags": {
      |    "name":         "test.metric.$mname",
      |    "atlas.dstype": "gauge"
      |  },
      |  "timestamp": $timestamp,
      |  "value": $value
      |}
    """.stripMargin
  }

  override def hasNext: Boolean = true

  override def next(): Record[String] = {
    val timestamp = System.currentTimeMillis()
    val payload = new StringBuilder
    payload.append("""{"tags":""").append(commonTags).append(""","metrics":[""")
    payload.append(metric(0, timestamp, 0))
    (1 until numMetrics).foreach { j =>
      i = Integer.remainderUnsigned(i + 1, values.length)
      val value = values(i)
      payload.append(",").append(metric(j, timestamp, value))
    }
    payload.append("]}")
    Map(name -> payload.toString())
  }
}
