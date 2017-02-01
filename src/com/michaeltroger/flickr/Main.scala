package com.michaeltroger.flickr

import java.awt.Desktop
import java.net.URL
import scala.swing._
import scala.swing.event._

object SwingApp extends SimpleSwingApplication  {

  var searchField = new TextField {
    columns = 10
  }

  var searchButton = new Button {
    text = "Search"
  }

  var recentImagesButton = new Button {
    text = "Recent images"
  }

  var menuPanel = new FlowPanel{
    contents.append(searchField, searchButton, recentImagesButton)
    val s = new Dimension(800,10)
    maximumSize = s
  }

  var imagePanel = new FlowPanel {
    for (i <- 1 to 10) {
      contents.append(
        new Label{
          listenTo(mouse.clicks)
          reactions += {
            case e : MouseClicked => openWebPage(e.source.tooltip)
          }
        }
      )
    }

  }





  val recentImages = new RecentImages(imagePanel)
  val searchImages = new SearchImages(imagePanel)

  recentImagesButton.reactions += {
    case b : ButtonClicked => recentImages.getImageUrls
  }
  searchButton.reactions += {
    case b : ButtonClicked =>
      searchImages.getImageUrls(searchField.text)
      searchField.text = ""
  }

  val myPanel = new BoxPanel(Orientation.Vertical) {
    contents.append(menuPanel, imagePanel)
  }

  override def top = new MainFrame {
    val s = new Dimension(800,400)

    title = "Flickr"
    minimumSize = s
    preferredSize = s

    contents = myPanel
  }

  def openWebPage(url: String): Unit = {
    Desktop.getDesktop().browse(new URL(url).toURI())
  }
}

