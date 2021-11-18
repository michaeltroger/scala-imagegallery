package com.michaeltroger.imagegallery

import scala.swing.FlowPanel

class SearchImages(val imagePanel: FlowPanel, val removeImagesBeforeInsertingNew : Boolean) extends UpdateImages {

  override val queryString = Array(
    "method" -> "flickr.photos.search",
    "per_page" -> "10",
    "format" -> "json",
    "nojsoncallback" -> "1",
    "api_key" -> "aa3c1374cf9bc5d61bae62d08ad9cbba",
    "sort" -> "relevance"
  )

}

object SearchImages {
  def apply(imagePanel: FlowPanel, removeImagesBeforeInsertingNew : Boolean): SearchImages = new SearchImages(imagePanel, removeImagesBeforeInsertingNew)
}
