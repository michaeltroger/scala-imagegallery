package com.michaeltroger.flickr

import javax.swing.ImageIcon

import akka.stream.ActorMaterializer
import play.api.libs.json._
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.libs.ws.ahc.AhcWSClient
import play.api.routing.sird._

import scala.concurrent.Future
import scala.swing.{FlowPanel, Label}

trait UpdateImages {
  implicit val actorSystem = akka.actor.ActorSystem()
  implicit val wsClient = AhcWSClient()(ActorMaterializer()(actorSystem))

  import scala.concurrent.ExecutionContext.Implicits.global

  var imagePanel : FlowPanel

  def updateImages(imageUrl: String, miniatureUrl: String, index: Int) : Unit = {
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
}
