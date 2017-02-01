package com.michaeltroger.flickr

import scala.swing.FlowPanel

class RecentImages(var imagePanel: FlowPanel) extends UpdateImages {

  override var queryString = Array(
    "method" -> "flickr.photos.getRecent",
    "per_page" -> "10",
    "format" -> "json",
    "nojsoncallback" -> "1",
    "api_key" -> "aa3c1374cf9bc5d61bae62d08ad9cbba"
  )

}


