package cardboard.client

import org.scalajs.dom.raw.{HTMLImageElement, CanvasRenderingContext2D}

import scala.scalajs.js
import js.annotation._
import org.scalajs.dom
import dom.html
import scala.util.Random

object R {
  val rand = new Random()

  def nextDouble = rand.nextDouble()
  def nextInt(n:Int) = rand.nextInt(n)
}

// Global variables here
object G {
  val margin = 20
  val cardHeight: Double = 0.2
  val mapHeight: Double = 0.8
  val mapWidth: Double = 0.8
  val statusWidth: Double = 0.2
  val tileDim = 150
  val mapRows = 15
  val mapCols = 15
  val startRow = 10
  val startCol = 10
  def canvasHeight = tileDim * mapRows
  def canvasWidth = tileDim * mapCols
}

// Maps keys to loaded images
object ImageLoader {
  private val images = collection.mutable.Map[String, HTMLImageElement]()

  def load(key: String, src: String): Unit = {
    images(key) = dom.document.createElement("img").asInstanceOf[HTMLImageElement]
    images(key).src = src
  }

  def apply(key: String):HTMLImageElement = images(key)
}

// The World Map
case class World(tiles: Array[Array[Tile]], player: Player) {
  def create(renderer: CanvasRenderingContext2D, x: Int, y: Int): Unit = {

    for (row <- 0 until tiles.length) {
      for (col <- 0 until tiles(row).length)  yield {
        val tile = tiles(row)(col)
        val (x, y) = (col * G.tileDim, row * G.tileDim)
        renderer.fillRect(x, y, G.tileDim, G.tileDim)
        renderer.drawImage(ImageLoader(tile.terrain.image), x, y, G.tileDim, G.tileDim)
      }
    }
  }
}

case class Tile(terrain: Terrain)
case class Player(row: Int, col: Int, key: String)

abstract class Terrain() {
  val image: String
}
case class Water(image: String = "no") extends Terrain
case class Hills(image: String = "no") extends Terrain
case class Mountains(image: String = "no") extends Terrain
case class Forests(image: String = "no") extends Terrain
case class Plains(image: String = "no") extends Terrain
case class Swamps(image: String = "no") extends Terrain

abstract class CardType
case class MovementCardType(f:Terrain => Int) extends CardType
case class AttackCardType(amount: Int) extends CardType
case class MagicCardType(amount: Int) extends CardType

case class Card(title: String = "", image: String, cardType: CardType)
case class Deck(cards: Seq[Card]) {
  def deal(n: Int):Seq[Card] = {
    val shuffledCards = Random.shuffle(cards)
    shuffledCards take n
  }
}
case class Hand(cards: Seq[Card]) {

  def create(div: html.Div):Unit = {
    var html = ""
    cards foreach {card => html = html + "<span class='card'>" + card.title + "</span>"}
    div.innerHTML = html
  }
}


class Game(val canvas: html.Canvas, val renderer: dom.CanvasRenderingContext2D, val cardDiv: html.Div, val statusDiv: html.Div) {
  private var world: World = null
  private var deck: Deck = null
  private var hand: Hand = null

  def preload(): Unit = {
    import dom.ext._
    import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

    // Testing out possible client<->server communication
    Ajax.get("/json").onSuccess{ case xhr =>
    }

    ImageLoader.load("no", "assets/no.png")
  }

  def create(): Unit = {
    world = WorldCreator.create
    world.create(renderer, 0, 0)

    deck = DeckBuilder.adventurerDeck
    hand = Hand(deck.deal(5))
    hand.create(cardDiv)

    statusDiv.innerHTML = "<h2>Status:</h2><p>Status text goes here.</p>"
  }

  def render(): Unit = {
  }

}

@JSExport
object CardboardClient {
  @JSExport
  def main(canvas: html.Canvas, cardDiv: html.Div, statusDiv: html.Div): Unit = {
    canvas.width = G.canvasWidth
    canvas.height = G.canvasHeight
    val renderer = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
    val game = new Game(canvas = canvas, renderer = renderer, cardDiv = cardDiv, statusDiv = statusDiv)
    game.preload()
    dom.window.onload = (e: dom.Event) => {
      game.create()
    }
  }
}

object WorldCreator {
  val terrains = List(Water(), Swamps(), Forests(), Mountains(), Hills(), Plains())

  def create: World = {
    // Fill with water to start
    val tiles:Array[Array[Tile]] = Array.fill[Tile](G.mapRows, G.mapCols)(Tile(Water()))

    // Put tile-placing code here
    World(
      tiles = tiles,
      player = Player(G.startRow, G.startCol, "player"))
  }
}

object DeckBuilder {
  def adventurerMovement()(terrain: Terrain):Int = {
    terrain match {
      case t:Water => 0
      case t:Hills => 2
      case t:Mountains => 2
      case t:Forests => 2
      case t:Plains => 2
      case t:Swamps => 2
    }
  }
  val adventurerDeck: Deck = Deck(Seq(
    Card("March", "no", MovementCardType(adventurerMovement())),
    Card("Skip", "no", MovementCardType(adventurerMovement())),
    Card("Hike", "no", MovementCardType(adventurerMovement())),
    Card("Slide", "no", MovementCardType(adventurerMovement())),
    Card("Prance", "no", MovementCardType(adventurerMovement())),
    Card("Strut", "no", MovementCardType(adventurerMovement())),
    Card("Smack", "no", AttackCardType(1)),
    Card("Whack", "no", AttackCardType(2)),
    Card("Slap", "no", AttackCardType(3)),
    Card("Wedgie", "no", AttackCardType(4)),
    Card("Poke", "no", AttackCardType(1)),
    Card("Strike", "no", AttackCardType(3)),
    Card("Cantrip", "no", MagicCardType(1)),
    Card("Ensorcell", "no", MagicCardType(2)),
    Card("Enchant", "no", MagicCardType(3)),
    Card("Conjure", "no", MagicCardType(4)),
    Card("Abjure", "no", MagicCardType(5)),
    Card("Invoke", "no", MagicCardType(6))

  ))
}
