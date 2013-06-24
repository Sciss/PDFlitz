/*
 *  Generate.scala
 *  (PDFlitz)
 *
 *  Copyright (c) 2013 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either
 *  version 3, june 2007 of the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License (gpl.txt) along with this software; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package de.sciss.pdflitz

import java.io.{FileOutputStream, File}
import javax.swing.JComponent
import com.itextpdf.text.{Document => IDocument, Rectangle => IRectangle}
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.awt.PdfGraphics2D
import scala.swing.Component
import java.awt.{Graphics2D, Dimension}
import language.implicitConversions

object Generate {
  object Source {
    implicit def fromJava (peer     : JComponent): Source = Java (peer)
    implicit def fromScala(component:  Component): Source = Scala(component)
  }
  sealed trait Source {
    // def peer: JComponent
    def render(g: Graphics2D): Unit
    def size: Dimension
    def preferredSize: Dimension
  }
  final case class Java(peer: JComponent) extends Source {
    def render(g: Graphics2D) { peer.paint(g) }
    def size: Dimension = peer.getSize
    def preferredSize: Dimension = peer.getPreferredSize
  }
  final case class Scala(component: Component) extends Source {
    // def peer = component.peer
    def render(g: Graphics2D) { component.peer.paint(g) }
    def size: Dimension = component.size
    def preferredSize: Dimension = component.preferredSize
  }
  final case class QuickDraw(size: Dimension)(fun: Graphics2D => Unit) extends Source {
    def render(g: Graphics2D) { fun(g) }
    def preferredSize = size
  }

  /** Generates a PDF file from a given view source.
    *
    * @param file             the file to write to. throw an exception if it exists, unless you specify `overwrite = true`
    * @param view             the view to render
    * @param usePreferredSize when `true`, uses the view's preferred size for output, otherwise its current `size`
    * @param margin           additional page margin around drawing
    * @param overwrite        when `true`, allows to write to an existing file which will then be overwritten
    */
  def apply(file: File, view: Source, usePreferredSize: Boolean = false, margin: Int = 0, overwrite: Boolean = false) {
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
      val g2 = new PdfGraphics2D(tp, viewSz.width, viewSz.height /*, fontMapper */)
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