package com.michaeltroger.imagegallery

import scala.swing.FlowPanel

class SearchImages(val imagePanel: FlowPanel, val removeImagesBeforeInsertingNew : Boolean) extends UpdateImages {

  override val queryString = Array(
    "method" -> "flickr.photos.search",
    "per_page" -> "10",
    "format" -> "json",
    "nojsoncallback" -> "1",
    "api_key" -> sys.env("FLICKR_API_KEY"),
    "sort" -> "relevance",
    "license" -> "10" // Public Domain Mark https://creativecommons.org/publicdomain/mark/1.0/
  )

}

object SearchImages {
  def apply(imagePanel: FlowPanel, removeImagesBeforeInsertingNew : Boolean): SearchImages = new SearchImages(imagePanel, removeImagesBeforeInsertingNew)
}
