package com.michaeltroger.imagegallery

import scala.swing.FlowPanel

class RecentImages(val imagePanel : FlowPanel, val removeImagesBeforeInsertingNew : Boolean) extends UpdateImages {

  override val queryString = Array(
    "method" -> "flickr.photos.getRecent",
    "per_page" -> "10",
    "format" -> "json",
    "nojsoncallback" -> "1",
    "api_key" -> sys.env("FLICKR_API_KEY")
  )

}

object RecentImages {
  def apply(imagePanel : FlowPanel, removeImagesBeforeInsertingNew : Boolean) : RecentImages = new RecentImages(imagePanel, removeImagesBeforeInsertingNew)
}


