package com.michaeltroger.flickr

import javax.swing.ImageIcon

import akka.stream.ActorMaterializer
import play.api.libs.json._
import play.api.libs.ws.ahc.AhcWSClient
import play.api.libs.ws.{WSRequest, WSResponse}

import scala.concurrent.Future
import scala.swing.{FlowPanel, Label}

class RecentImages(var imagePanel: FlowPanel) extends UpdateImages {


  override var queryString = Array(
    "method" -> "flickr.photos.getRecent",
    "per_page" -> "10",
    "format" -> "json",
    "nojsoncallback" -> "1",
    "api_key" -> "aa3c1374cf9bc5d61bae62d08ad9cbba"
  )

}


