package com.michaeltroger.flickr

import java.awt.Desktop
import java.net.URL
import scala.swing._
import scala.swing.event._

object SwingApp extends SimpleSwingApplication  {
  val menuPanel = createMenuPanel()
  val imagePanel = createImagePanel()

  val recentImages = RecentImages(imagePanel)
  val searchImages = SearchImages(imagePanel)
  recentImages.loadImages()

  val mainPanel = new BoxPanel(Orientation.Vertical) {
    contents.append(menuPanel, imagePanel)
  }

  override def top = new MainFrame {
    val s = new Dimension(800,400)

    title = "Flickr"
    minimumSize = s
    preferredSize = s

    contents = mainPanel
  }

  def openWebPage(url: String): Unit = {
    Desktop.getDesktop.browse(new URL(url).toURI)
  }

  def createMenuPanel(): FlowPanel = {
    val searchField = new TextField {
      listenTo(keys)
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

    recentImagesButton.reactions += {
      case b : ButtonClicked => recentImages.loadImages()
    }
    searchButton.reactions += {
      case b : ButtonClicked =>
        searchImages.loadImages(("text", searchField.text))
        searchField.text = ""
    }
    searchField.reactions += {
      case KeyPressed(_, Key.Enter, _, _) =>
        searchImages.loadImages(("text", searchField.text))
        searchField.text = ""
    }

    menuPanel
  }

  def createImagePanel(): FlowPanel = {
    val imagePanel = new FlowPanel {
      for (i <- 1 to 10) {
        contents.append(new Label{
          listenTo(mouse.clicks)
          reactions += {case e : MouseClicked => openWebPage(e.source.tooltip)}
        })
      }
    }

    imagePanel
  }
}

