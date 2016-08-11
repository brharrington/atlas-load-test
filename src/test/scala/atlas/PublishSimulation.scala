package atlas

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class PublishSimulation extends Simulation {

  val httpConf = http
    .baseURL("http://localhost:7101")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("test")
    .header("Content-Encoding", "gzip")

  val publish = (0 until 150).map { i =>
    scenario(s"publish-$i")
      .during(10 hours) {
        exec(feed(new PublishFeeder("test", i, 1000)))
          .exec(http("publish").post("/api/v1/publish").body(StringBody("""${test}""")).processRequestBody(gzipBody))
          .pause(1 seconds)
      }
  }

  //setUp(publish.inject(atOnceUsers(1)).protocols(httpConf))
  setUp(publish.map(_.inject(atOnceUsers(1)).protocols(httpConf)): _*)
}
