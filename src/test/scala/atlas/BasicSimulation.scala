package atlas

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val httpConf = http
    .baseURL("http://localhost:7101")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val tags = scenario("tags")
    .during(2 minutes) {
      exec(http("tags")
        .get("/api/v1/tags"))
      //.pause(1)
    }

  val graph = scenario("graph")
    .during(2 minutes) {
      exec(http("graph")
        .get("/api/v1/graph?q=name,sps,:eq,(,nf.cluster,),:by"))
      //.pause(1)
    }

  setUp(
    tags.inject(atOnceUsers(10)).protocols(httpConf),
    graph.inject(atOnceUsers(50)).protocols(httpConf))
}
