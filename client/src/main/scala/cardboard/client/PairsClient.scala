package cardboard.client

import org.scalajs.dom.raw.{HTMLImageElement, CanvasRenderingContext2D}

import scala.scalajs.js
import js.annotation._
import js.JSConverters._
import org.scalajs.dom
import scala.collection.mutable
import dom.html

import scala.util.Random

object R {
  val rand = new Random()

  def nextDouble = rand.nextDouble()
  def nextInt(n:Int) = rand.nextInt(n)
}

object G {
  val margin = 20
  val cardHeight: Double = 0.2
  val mapHeight: Double = 0.8
  val mapWidth: Double = 0.8
  val statusWidth: Double = 0.2
  val tileDim = 100
  val mapRows = 15
  val mapCols = 15
  val startRow = 10
  val startCol = 10
  val nTreasures = 7
  val endTreasureStart = 5
  val islandSize = 3
  def canvasHeight = tileDim * mapRows
  def canvasWidth = tileDim * mapCols
}

case class World(tiles: Array[Array[Tile]], player: Player) {
  def create(renderer: CanvasRenderingContext2D, x: Int, y: Int): Unit = {
    //val maxRow = tiles.map(_.row).max
    //val maxCol = tiles.map(_.col).max
   // val tileDim = Math.min(height/maxRow, width/maxCol)
    //val tileDim = 100
    //val tileScale = tileDim / 100

    for (row <- 0 until tiles.length) {
      for (col <- 0 until tiles(row).length)  yield {
        val tile = tiles(row)(col)
        val (x, y) = (col * G.tileDim, row * G.tileDim)
        renderer.fillStyle = if (tile.treasure) "gold" else "blue"
        renderer.fillRect(x, y, G.tileDim, G.tileDim)
        renderer.drawImage(ImageLoader(tile.terrain.key), x, y, G.tileDim, G.tileDim)
        //val tileSprite:Sprite = game.add.sprite(x, y, key = tile.key)
        //tileSprite.scale.setTo(tileScale)
        //tile.sprite = Some(tileSprite)
      }
    }
  }
}

case class Tile(terrain: Terrain, treasure: Boolean = false)

/*
object Tile {
  def maxRow(t1: Tile, t2: Tile): Int = if (t1.row > t2.row) t1.row else t2.row
  def maxCol(t1: Tile, t2: Tile): Int = if (t1.row > t2.row) t1.row else t2.row
}
*/

case class Player(row: Int, col: Int, key: String)

abstract class Terrain() {
  val key: String
}
case class Water(key: String = "tile-water") extends Terrain
case class Hills(key: String = "tile-hill") extends Terrain
case class Mountains(key: String = "tile-mountain") extends Terrain
case class Forests(key: String = "tile-forest") extends Terrain
case class Plains(key: String = "tile-plains") extends Terrain
case class Swamps(key: String = "tile-swamp") extends Terrain

abstract class CardType
case class MovementCardType(f:Terrain => Int) extends CardType
case class StrengthCardType(amount: Int) extends CardType
case class StealthCardType(amount: Int) extends CardType

case class Card(sprite: String, cardType: CardType)
case class Deck(cards: Seq[Card])
case class Hand(cards: Seq[Card])

object ImageLoader {
  private val images = collection.mutable.Map[String, HTMLImageElement]()

  def load(key: String, src: String): Unit = {
    images(key) = dom.document.createElement("img").asInstanceOf[HTMLImageElement]
    images(key).src = src
  }

  def apply(key: String):HTMLImageElement = images(key)
}

class Game(val canvas: html.Canvas, val renderer: dom.CanvasRenderingContext2D) {
  //private val world: mutable.Seq[Tile] = mutable.Seq()
  private var world: World = null
  //private val TileWidth = 100
  //private val TileHeight = 100
  //private var zoom: Double = 0.5

  def preload(): Unit = {
    import dom.ext._
    import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
    Ajax.get("/json").onSuccess{ case xhr =>
    }

    ImageLoader.load("tile-water", "assets/tile-water.png")
    ImageLoader.load("tile-mountain", "assets/tile-mountain.png")
    ImageLoader.load("tile-hill", "assets/tile-hill.png")
    ImageLoader.load("tile-swamp", "assets/tile-swamp.png")
    ImageLoader.load("tile-plains", "assets/tile-plains.png")
    ImageLoader.load("tile-forest", "assets/tile-forest.png")

    //load.audio("beep", "assets/sound.wav")
    //load.image("tile-water", "assets/tile-water.png")
  }

  def create(): Unit = {
    world = WorldCreator.create
    //val beep = game.add.audio("beep")
    //world.create(renderer, 0, 0, (dom.window.innerHeight - 20)*mapHeight, (dom.window.innerWidth - 20)*mapWidth)
    world.create(renderer, 0, 0)

  }

  def render(): Unit = {
  }

}

@JSExport
object PairsClient {
  @JSExport
  def main(canvas: html.Canvas): Unit = {
    canvas.width = G.canvasWidth
    canvas.height = G.canvasHeight
    //canvas.width = ((dom.window.innerWidth - 20) * mapWidth).toInt
    //canvas.height = ((dom.window.innerHeight - 20) * mapHeight).toInt
    val renderer = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
    val game = new Game(canvas = canvas, renderer = renderer)
    game.preload()
    dom.window.onload = (e: dom.Event) => {
      game.create()
    }

    //val game = new Game(width = (dom.window.innerWidth - margin), height = (dom.window.innerHeight - margin), parent = "game-container")
    //game.state.add("game", new GameState)
    //game.state.start("game")
  }
}

object WorldCreator {
  val terrains = List(Water(), Swamps(), Forests(), Mountains(), Hills(), Plains())

  def create: World = {
    // Fill with water to start
    val tiles:Array[Array[Tile]] = Array.fill[Tile](G.mapRows, G.mapCols)(Tile(Water()))

    // Then place one treasure per terrain
    for (i <- 0 until terrains.length) {
      var randRow = R.nextInt(G.mapRows)
      var randCol = R.nextInt(G.mapCols)
      while (tiles(randRow)(randCol).treasure) {
        randRow = R.nextInt(G.mapRows)
        randCol = R.nextInt(G.mapCols)
      }
      tiles(randRow)(randCol) = Tile(terrains(i), treasure = true)

      // Then place related tiles around each adventure location
      for (j <- (-1 * G.islandSize) to G.islandSize;
           k <- (-1 * G.islandSize) to G.islandSize) {
        val islandRow = j + randRow
        val islandCol = k + randCol

        if (islandRow > 0 &&
          islandCol > 0 &&
          islandRow < G.mapRows &&
          islandCol < G.mapCols &&
          !tiles(islandRow)(islandCol).treasure) {
          val tier = Math.max(Math.abs(j), Math.abs(k))
          val range = (-1 * tier) to tier
          val diff = if (R.nextDouble < 0.5) 0 else range(R.nextInt(range.length))
          val terrain = if (i + diff < 0) terrains.length + diff else (i + diff) % terrains.length
          tiles(islandRow)(islandCol) = Tile(terrains(terrain))
          /*
          Math.max(Math.abs(j), Math.abs(k)) match {
            case 1 =>
              if (R.nextDouble < 0.9)
                  tiles(j)(k) = Tile(terrains(i))
              else
                tiles(j)(k) = Tile(terrains(i + ))
          }
          */
        }
      }
    }
    World(
      tiles = tiles,
      player = Player(G.startRow, G.startCol, "player"))
  }


  /*
  def width:Int = G.mapCols * G.tileDim
  def height:Int = G.mapRows * G.tileDim
  def apply(): World = {
    val tiles = for (row <- 0 until G.mapRows; col <- 0 until G.mapCols) yield terrains(R.nextInt(terrains.length)) match {
      case _:Water => Tile(Water(), "tile-water")
      case _:Hills => Tile(Hills(), "tile-hill")
      case _:Mountains => Tile(Mountains(), "tile-mountain")
      case _:Plains => Tile(Plains(), "tile-plains")
      case _:Swamps => Tile(Swamps(), "tile-swamp")
      case _:Forests => Tile(Forests(), "tile-forest")
    }
  }
    */
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
    Card("card-default", MovementCardType(adventurerMovement())),
    Card("card-default", MovementCardType(adventurerMovement()))
  ))
}
