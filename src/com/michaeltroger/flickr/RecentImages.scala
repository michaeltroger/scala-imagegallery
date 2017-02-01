package com.michaeltroger.flickr

import javax.swing.ImageIcon

import akka.stream.ActorMaterializer
import play.api.libs.json._
import play.api.libs.ws.ahc.AhcWSClient
import play.api.libs.ws.{WSRequest, WSResponse}

import scala.concurrent.Future
import scala.swing.{FlowPanel, Label}

class RecentImages(var imagePanel: FlowPanel) extends UpdateImages {
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val photoRead = Json.reads[Photo]
  implicit val photosReads = Json.reads[Photos]
  implicit val photoRootReads = Json.reads[PhotosRoot]

  var recentQueryString = Array(
    "method" -> "flickr.photos.getRecent",
    "per_page" -> "10",
    "format" -> "json",
    "nojsoncallback" -> "1",
    "api_key" -> "aa3c1374cf9bc5d61bae62d08ad9cbba"
  )

  def getImageUrls: Unit = {
    //imagePanel.contents.foreach{ case l : Label => l.icon = null } // optionally remove images before inserting the new
    val latestImagesListRequest: WSRequest =
    wsClient
      .url("https://api.flickr.com/services/rest/")
      .withQueryString(recentQueryString: _*)

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
          updateImages(imageUrl, miniatureUrl, i)
        }

      }
    }
  }


  case class PhotosRoot(photos: Photos, stat: String)
  case class Photos(page: Int, pages: Int, perpage: Int, total: Int, photo: Array[Photo])
  case class Photo(id: String, owner: String, secret: String, server: String, farm: Int, title: String, ispublic: Int, isfriend: Int, isfamily: Int)

}


