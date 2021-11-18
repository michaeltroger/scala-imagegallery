package com.michaeltroger.imagegallery

import akka.actor.ActorSystem

import javax.swing.ImageIcon
import akka.stream.ActorMaterializer
import play.api.libs.json._
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.collection.mutable.ListBuffer
import scala.swing.{FlowPanel, Label}

trait UpdateImages {
  import scala.concurrent.ExecutionContext.Implicits.global

  private[this] implicit val actorSystem: ActorSystem = akka.actor.ActorSystem()
  private[this] implicit val wsClient: StandaloneAhcWSClient = StandaloneAhcWSClient()(ActorMaterializer()(actorSystem))

  private[this] implicit val photoRead: Reads[Photo] = Json.reads[Photo]
  private[this] implicit val photosReads: Reads[Photos] = Json.reads[Photos]
  private[this] implicit val photoRootReads: Reads[PhotosRoot] = Json.reads[PhotosRoot]

  private[this] val FLICKR_REST_URL : String = "https://api.flickr.com/services/rest/"
  val imagePanel : FlowPanel
  val queryString : Array[(String, String)]
  val removeImagesBeforeInsertingNew : Boolean

  def loadImages(additionalParam: (String,String) = ("", "")): Unit = {
    val queryStringsExtended : ListBuffer[(String, String)] = queryString.to(ListBuffer)
    queryStringsExtended += additionalParam
    if (removeImagesBeforeInsertingNew) {
      imagePanel.contents.foreach{ case l : Label => l.icon = null }
    }
    val latestImagesListRequest = wsClient.url(FLICKR_REST_URL).withQueryStringParameters(queryStringsExtended.toSeq: _*)
    val responseFuture = latestImagesListRequest.get()

    responseFuture.map {wsResponse =>
      val jsonString: JsValue = Json.parse(wsResponse.body)
      val photosRootFromJson: JsResult[PhotosRoot] = Json.fromJson[PhotosRoot](jsonString)

      var photosRoot : Option[PhotosRoot] = None
      photosRootFromJson match {
        case JsSuccess(r: PhotosRoot, _: JsPath) => photosRoot = Option(r)
        case e: JsError => println("Errors: " + JsError.toJson(e).toString())
      }

      if (photosRoot.isDefined) {
        for ((photo, i)  <- photosRoot.get.photos.photo.zipWithIndex) {
          val imageUrlWithoutFilending = "https://live.staticflickr.com/" + photo.server + "/" + photo.id + "_" + photo.secret
          val imageUrl = imageUrlWithoutFilending + "_b.jpg"
          val miniatureUrl = imageUrlWithoutFilending + "_q.jpg"
          requestAndDisplayImageInPanel(imageUrl, miniatureUrl, i)
        }

      }
    }
  }

  private[this] def requestAndDisplayImageInPanel(imageUrl: String, miniatureUrl: String, index: Int) : Unit = {
    val imageRequest = wsClient.url(miniatureUrl)
    val imageResponseFuture = imageRequest.get()

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
}

case class PhotosRoot(photos: Photos, stat: String)
case class Photos(page: Int, pages: Int, perpage: Int, photo: Array[Photo]) // "total" left out -> sometimes int sometimes string
case class Photo(id: String, owner: String, secret: String, server: String, title: String)
