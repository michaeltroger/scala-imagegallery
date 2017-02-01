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


  var imagePanel : FlowPanel

  def updateImages(imageUrl: String, miniatureUrl: String, index: Int)
}
