package com.michaeltroger.flickr

import java.awt.Desktop
import java.net.URL
import scala.swing._
import scala.swing.event._

object SwingApp extends SimpleSwingApplication  {

  val searchField = new TextField {
    columns = 10
  }

  val searchButton = new Button {
    text = "Search"
  }

  val recentImagesButton = new Button {
    text = "Recent images"
  }

  val menuPanel = new FlowPanel{
    contents.append(searchField, searchButton, recentImagesButton)
    val s = new Dimension(800,10)
    maximumSize = s
  }

  val imagePanel = new FlowPanel {
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

  val recentImages = RecentImages(imagePanel)
  val searchImages = SearchImages(imagePanel)
  recentImages.getImageUrls()

  recentImagesButton.reactions += {
    case b : ButtonClicked => recentImages.getImageUrls()
  }
  searchButton.reactions += {
    case b : ButtonClicked =>
      searchImages.getImageUrls(("text", searchField.text))
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
    Desktop.getDesktop.browse(new URL(url).toURI)
  }
}

