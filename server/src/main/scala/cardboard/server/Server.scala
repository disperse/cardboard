package cardboard.server

import akka.actor.ActorSystem
import spray.http.{HttpEntity, MediaTypes}
import spray.routing.SimpleRoutingApp
import upickle.default._

import scala.util.Properties

object Server extends SimpleRoutingApp {
  private val Index = {
    <html>
      <head>
        <title>Wayne's Dream</title>
        <style>
        </style>
      </head>
      <body>
        <div id="map-div">
          <canvas id="canvas"/>
        </div>
        <!-- script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/phaser/2.4.6/phaser.min.js"></script -->
        <!-- script type="application/javascript" src="/assets/js/phaser.min.js"></script -->
        <script type="application/javascript" src="/js/pairs-client-fastopt.js"></script>
        <script type="application/javascript" src="/js/pairs-client-launcher.js"></script>
        <script>
          pairs.client.PairsClient().main(document.getElementById('canvas'));
        </script>
      </body>
    </html>
  }

  private val Json = {
    write(Seq(1,2,3))
  }

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    val port = Properties.envOrElse("PORT", "8080").toInt

    startServer("0.0.0.0", port = port) {
      get {
        pathSingleSlash {
          getFromResource("index.html")
        } ~
        getFromResourceDirectory("")
      } ~
      path("js" / Rest) { fileName =>
        getFromFile("../client/target/scala-2.11/" + fileName)
      } ~
      pathPrefix("assets") {
        getFromResourceDirectory("assets")
      } ~
      path("json") {
        complete {
          Json
        }
      }
    }
  }
}
