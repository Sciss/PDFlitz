/*
 *  Generate.scala
 *  (PDFlitz)
 *
 *  Copyright (c) 2013-2015 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is published under the GNU General Public License v3+
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package de.sciss.pdflitz

import java.awt.{Dimension, Graphics2D, RenderingHints}
import java.io.{File, FileOutputStream}

import com.itextpdf.awt.PdfGraphics2D
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.{Document => IDocument, Rectangle => IRectangle}
import javax.swing.JComponent

import scala.language.implicitConversions
import scala.swing.Component

object Generate {
  object Source {
    implicit def fromJava (peer     : JComponent): Source = Java (peer)
    implicit def fromScala(component:  Component): Source = Scala(component)
  }
  trait Source {
    // def peer: JComponent
    def render(g: Graphics2D): Unit
    def size: Dimension
    def preferredSize: Dimension
  }
  final case class Java(peer: JComponent) extends Source {
    def render(g: Graphics2D): Unit = peer.paint(g)
    def size: Dimension = peer.getSize
    def preferredSize: Dimension = peer.getPreferredSize
  }
  final case class Scala(component: Component) extends Source {
    // def peer = component.peer
    def render(g: Graphics2D): Unit = component.peer.paint(g)
    def size: Dimension = component.size
    def preferredSize: Dimension = component.preferredSize
  }
  object QuickDraw {
    def apply(size: => Dimension)(fun: Graphics2D => Unit): QuickDraw = new QuickDraw(size)(fun)
  }
  final class QuickDraw(size0: => Dimension)(fun: Graphics2D => Unit) extends Source {
    def size: Dimension = size0
    def render(g: Graphics2D): Unit = fun(g)
    def preferredSize: Dimension = size
  }

  /** Generates a PDF file from a given view source.
    *
    * @param file             the file to write to. throw an exception if it exists, unless you specify `overwrite = true`
    * @param view             the view to render
    * @param usePreferredSize when `true`, uses the view's preferred size for output, otherwise its current `size`
    * @param margin           additional page margin around drawing
    * @param overwrite        when `true`, allows to write to an existing file which will then be overwritten
    */
  def apply(file: File, view: Source, usePreferredSize: Boolean = false, margin: Int = 0,
            overwrite: Boolean = false): Unit = {
    require(!file.exists() || overwrite, s"The specified file $file already exists.")

    val viewSz    = if (usePreferredSize) view.preferredSize else view.size
    val width     = viewSz.width  + (margin << 1)
    val height    = viewSz.height + (margin << 1)
    val pageSize  = new IRectangle(0, 0, width, height)
    val doc       = new IDocument(pageSize, margin, margin, margin, margin)
    val stream    = new FileOutputStream(file)
    val writer    = PdfWriter.getInstance(doc, stream)

    doc.open()
    try {
      val cb = writer.getDirectContent
      val tp = cb.createTemplate(viewSz.width, viewSz.height)
      // use `onlyShapes = true` until we deal with the font-mapper!
      val g2 = new PdfGraphics2D(tp, viewSz.width, viewSz.height, true /*, fontMapper */)
      g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
      try {
        view.render(g2)
      } finally {
        g2.dispose()
      }
      cb.addTemplate(tp, margin, margin)
    } finally {
      doc.close()
    }
  }
}