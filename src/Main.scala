import java.awt.Desktop
import java.net.URL
import javax.swing.ImageIcon

import akka.stream.ActorMaterializer

import scala.swing._
import scala.swing.event._
import play.api.libs.json._
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.libs.ws.ahc.AhcWSClient

import scala.concurrent.Future

object SwingApp extends SimpleSwingApplication  {
  implicit val actorSystem = akka.actor.ActorSystem()
  implicit val wsClient = AhcWSClient()(ActorMaterializer()(actorSystem))
  import scala.concurrent.ExecutionContext.Implicits.global

  var button = new Button {
    text = "Next images"
    reactions += {
      case b : ButtonClicked => fetchImage()
    }
  }

  var imagePanel = new FlowPanel {
    for (i <- 1 to 10) {
      contents.append(
        new Label{
          listenTo(mouse.clicks)
          reactions += {
            case e : MouseClicked => openWebPage(e.source.tooltip)
          }
        }
      )
    }

  }

  val s = new Dimension(800,400)

  val myPanel = new BoxPanel(Orientation.Vertical) {
    contents.append(button, imagePanel)
  }

  def top = new MainFrame {
    title = "Flickr"
    minimumSize = s
    preferredSize = s

    contents = myPanel

  }

  implicit val photoRead = Json.reads[Photo]
  implicit val photosReads = Json.reads[Photos]
  implicit val photoRootReads = Json.reads[PhotosRoot]

  def cleanUp() = { // TODO: not used yet
    println("cleaning up, closing wsClient and actorsystem")
    wsClient.close()
    actorSystem.terminate()
  }

  def fetchImage() : Unit = {
    val latestImagesListRequest: WSRequest =
      wsClient
      .url("https://api.flickr.com/services/rest/")
      .withQueryString(
        "method" -> "flickr.photos.getRecent",
        "per_page" -> "10",
        "format" -> "json",
        "nojsoncallback" -> "1",
        "api_key" -> "aa3c1374cf9bc5d61bae62d08ad9cbba"
      )

    val responseFuture: Future[WSResponse] = latestImagesListRequest.get()

    responseFuture.map {wsResponse =>
      val jsonString: JsValue = Json.parse(wsResponse.body)
      val photosRootFromJson: JsResult[PhotosRoot] = Json.fromJson[PhotosRoot](jsonString)

      var photosRoot : Option[PhotosRoot] = None
      photosRootFromJson match {
        case JsSuccess(r: PhotosRoot, path: JsPath) => photosRoot = Option(r)
        case e: JsError => println("Errors: " + JsError.toJson(e).toString())
      }

      if (photosRoot.isDefined) {
        for ((photo, i)  <- photosRoot.get.photos.photo.zipWithIndex) {
          val imageUrlWithoutFilending = "https://farm" + photo.farm + ".staticflickr.com/" + photo.server + "/" + photo.id + "_" + photo.secret
          val miniatureUrlWithoutFilending = imageUrlWithoutFilending + "_q"
          val imageUrl = imageUrlWithoutFilending + ".jpg"
          val miniatureUrl = miniatureUrlWithoutFilending + ".jpg"
          getAndDisplayImage(imageUrl, miniatureUrl, i)
        }

      }
    }


  }

  def getAndDisplayImage(imageUrl: String, miniatureUrl: String, index: Int)  {
    val imageRequest: WSRequest = wsClient.url(miniatureUrl)
    val imageResponseFuture: Future[WSResponse] = imageRequest.get()
    imageResponseFuture.map{ wsResponse1 =>
      val bytesString = wsResponse1.bodyAsBytes
      val img = new ImageIcon(bytesString.toArray)
      imagePanel.contents(index) match {
        case l : Label =>
          l.icon = img
          l.tooltip = imageUrl
      }
    }

  }

  def openWebPage(url: String): Unit = {
    Desktop.getDesktop().browse(new URL(url).toURI())
  }
}

case class PhotosRoot(photos: Photos, stat: String)
case class Photos(page: Int, pages: Int, perpage: Int, total: Int, photo: Array[Photo])
case class Photo(id: String, owner: String, secret: String, server: String, farm: Int, title: String, ispublic: Int, isfriend: Int, isfamily: Int)
