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
    text = "Next image"
  }

  var imageLabel = new Label {
    listenTo(button)
    reactions += {
      case ButtonClicked(b) => fetchImage()
    }
  }

  var imageUrlLabel = new Label

  val s = new Dimension(640,480)

  val myPanel = new BoxPanel(Orientation.Vertical) {
    contents.append(button, imageUrlLabel, imageLabel)
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
      if (! (200 to 299).contains(wsResponse.status)) {
        sys.error(s"Received unexpected status ${wsResponse.status} : ${wsResponse.body}")
      }
      val jsonString: JsValue = Json.parse(wsResponse.body)
      val photosRootFromJson: JsResult[PhotosRoot] = Json.fromJson[PhotosRoot](jsonString)

      var photosRoot : Option[PhotosRoot] = None
      photosRootFromJson match {
        case JsSuccess(r: PhotosRoot, path: JsPath) => photosRoot = Option(r)
        case e: JsError => println("Errors: " + JsError.toJson(e).toString())
      }

      if (photosRoot.isDefined) {
        val firstPhoto = photosRoot.get.photos.photo(0)
        val imageUrl = "https://farm" + firstPhoto.farm + ".staticflickr.com/" + firstPhoto.server + "/" + firstPhoto.id + "_" + firstPhoto.secret + ".jpg"
        imageUrlLabel.text = imageUrl
        getAndDisplayImage(imageUrl)
      }
    }
  }

  def getAndDisplayImage(imageUrl: String) {
    val imageRequest: WSRequest = wsClient.url(imageUrl)
    val imageResponseFuture: Future[WSResponse] = imageRequest.get()

    imageResponseFuture.map{wsResponse1 =>
      if (! (200 to 299).contains(wsResponse1.status)) {
        sys.error(s"Received unexpected status ${wsResponse1.status} : ${wsResponse1.body}")
      }
      val bytesString = wsResponse1.bodyAsBytes
      val img = new ImageIcon(bytesString.toArray)
      imageLabel.icon = img
    }
  }
}

case class PhotosRoot(photos: Photos, stat: String)
case class Photos(page: Int, pages: Int, perpage: Int, total: Int, photo: Array[Photo])
case class Photo(id: String, owner: String, secret: String, server: String, farm: Int, title: String, ispublic: Int, isfriend: Int, isfamily: Int)
