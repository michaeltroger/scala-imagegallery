import scala.swing._
import scala.swing.event._

import play.api.libs.ws.ning.NingWSClient
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._

object SwingApp extends SimpleSwingApplication {

  def top = new MainFrame {
    title = "SwingApp"
    var numclicks = 0

    object label extends Label {
      val prefix = "Number of button clicks: "
      text = prefix + "0  "
      listenTo(button)
      reactions += {
        case ButtonClicked(button) =>
          numclicks = numclicks + 1
          text = prefix + numclicks
      }
    }

    object button extends Button {
      text = "I am a button"
    }

    contents = new FlowPanel {
      contents.append(button, label)
      border = Swing.EmptyBorder(5, 5, 5, 5)
    }
  }

  implicit val photoRead = Json.reads[Photo]
  implicit val photosReads = Json.reads[Photos]
  implicit val photoRootReads = Json.reads[PhotosRoot]


  val wsClient = NingWSClient()
  wsClient
    .url("https://api.flickr.com/services/rest/")
    .withQueryString(
      "method" -> "flickr.photos.getRecent",
      "per_page" -> "10",
      "format" -> "json",
      "nojsoncallback" -> "1",
      "api_key" -> "aa3c1374cf9bc5d61bae62d08ad9cbba"
    )
    //.withHeaders("Cache-Control" -> "no-cache")
    .get()
    .map { wsResponse =>
      if (! (200 to 299).contains(wsResponse.status)) {
        sys.error(s"Received unexpected status ${wsResponse.status} : ${wsResponse.body}")
      }
      println(s"OK, received ${wsResponse.body}")
      val jsonString: JsValue = Json.parse(wsResponse.body)
      val residentFromJson: JsResult[PhotosRoot] = Json.fromJson[PhotosRoot](jsonString)

      println(residentFromJson)
      residentFromJson match {
        case JsSuccess(r: PhotosRoot, path: JsPath) =>
          val id = r.photos.photo(0).id
          println("id: " + id)
        case e: JsError => println("Errors: " + JsError.toJson(e).toString())
      }
      
      //println(s"The response header Content-Length was ${wsResponse.header("Content-Length")}")
    }

}

case class PhotosRoot(photos: Photos, stat: String)
case class Photos(page: Int, pages: Int, perpage: Int, total: Int, photo: Array[Photo])
case class Photo(id: String, owner: String, secret: String, server: String, farm: Int, title: String, ispublic: Int, isfriend: Int, isfamily: Int)

