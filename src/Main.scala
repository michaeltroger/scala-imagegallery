import javax.swing.ImageIcon

import scala.swing._
import scala.swing.event._
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._

object SwingApp extends SimpleSwingApplication {

  var button = new Button {
    text = "Next image"
  }

  var imageLabel = new Label {
    listenTo(button)
    reactions += {
      case ButtonClicked(button) =>
        fetchImage()
    }
  }
  val s = new Dimension(640,480)
  def top = new MainFrame {
    title = "SwingApp"
    minimumSize = s
    maximumSize = s
    preferredSize = s
    var numclicks = 0



    contents = new FlowPanel {
      contents.append(button, imageLabel)
      border = Swing.EmptyBorder(5, 5, 5, 5)
    }
  }

  implicit val photoRead = Json.reads[Photo]
  implicit val photosReads = Json.reads[Photos]
  implicit val photoRootReads = Json.reads[PhotosRoot]


  def fetchImage() : Unit = {
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
        //println(s"OK, received ${wsResponse.body}")
        val jsonString: JsValue = Json.parse(wsResponse.body)
        val residentFromJson: JsResult[PhotosRoot] = Json.fromJson[PhotosRoot](jsonString)

        //println(residentFromJson)
        residentFromJson match {
          case JsSuccess(r: PhotosRoot, path: JsPath) =>
            val firstPhoto = r.photos.photo(0)
            val imageUrl = "https://farm" + firstPhoto.farm + ".staticflickr.com/" + firstPhoto.server + "/" + firstPhoto.id + "_" + firstPhoto.secret + ".jpg"
            println(imageUrl)
            wsClient.url(imageUrl)
              .get()
              .map{wsResponse1 =>
                if (! (200 to 299).contains(wsResponse1.status)) {
                  sys.error(s"Received unexpected status ${wsResponse1.status} : ${wsResponse1.body}")
                }
                //println(s"OK, received ${wsResponse1.body}")

                println("my:")
                println(wsResponse1.bodyAsBytes)
                val bytes = wsResponse1.bodyAsBytes
                val img = new ImageIcon(bytes)
                imageLabel.icon = img
                //Dialog.showMessage(message = null, icon = img)

                //val out = new FileOutputStream("/home/m/Projects/flickr-scala/the-file-name")
                //out.write(bytes)
                //out.close()


                //val img1 : BufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));
                //Dialog.showMessage(message = null, icon = img1)
              }

          case e: JsError => println("Errors: " + JsError.toJson(e).toString())
        }

        //println(s"The response header Content-Length was ${wsResponse.header("Content-Length")}")
      }
  }



}

case class PhotosRoot(photos: Photos, stat: String)
case class Photos(page: Int, pages: Int, perpage: Int, total: Int, photo: Array[Photo])
case class Photo(id: String, owner: String, secret: String, server: String, farm: Int, title: String, ispublic: Int, isfriend: Int, isfamily: Int)

