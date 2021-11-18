package com.michaeltroger.imagegallery

import java.awt.Desktop
import java.net.URL
import scala.swing._
import scala.swing.event._

object Main extends SimpleSwingApplication  {
  private val menuPanel = createMenuPanel()
  private val imagePanel = createImagePanel()

  private val searchImages = SearchImages(imagePanel, removeImagesBeforeInsertingNew = false)
  searchImages.loadImages("text", "cat")

  private val mainPanel = new BoxPanel(Orientation.Vertical) {
    contents.appendAll(List(menuPanel, imagePanel))
  }

  override def top: MainFrame = new MainFrame {
    val s = new Dimension(800,400)

    title = "Image Gallery"
    minimumSize = s
    preferredSize = s

    contents = mainPanel
  }

  private def createMenuPanel(): FlowPanel = {
    val searchField = new TextField {
      listenTo(keys)
      columns = 10
    }

    val searchButton = new Button {
      text = "Search"
    }

    val menuPanel: FlowPanel = new FlowPanel{
      contents.appendAll(List(searchField, searchButton))
      val s = new Dimension(800,10)
      maximumSize = s
    }

    searchButton.reactions += {
      case _: ButtonClicked =>
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

  private def createImagePanel(): FlowPanel = {
    val imagePanel = new FlowPanel {
      for (_ <- 1 to 10) {
        contents.append(new MyLabel {
           listenTo(mouse.clicks)
           reactions += {case _: MouseClicked => openWebPage(imageUrl)}
        })
      }
    }

    imagePanel
  }

  private def openWebPage(url: String): Unit = {
    Desktop.getDesktop.browse(new URL(url).toURI)
  }
}

