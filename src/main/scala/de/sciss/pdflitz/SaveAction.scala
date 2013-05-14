/*
 *  Action.scala
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

import scala.swing.{Dialog, ScrollPane, ListView, Menu, MenuBar, MenuItem, Frame, Action}
import java.awt.{FileDialog, Graphics2D, Graphics, Component}
import java.io.File
import javax.swing.{JList, DefaultListCellRenderer, Icon, JMenuBar, JMenu, JFrame, JMenuItem}
import collection.breakOut

class SaveAction(views: => Iterable[Generate.Source]) extends Action("Save As PDF...") {
  /** May be overridden. */
  protected def prepare(view: Generate.Source) {}

  var usePreferredSize  = false
  var margin            = 0

  def setupMenu(f: JFrame) {
    val mi  = new JMenuItem(peer)
    val mb  = f.getJMenuBar
    val mb1 = if (mb != null) {
      for (i <- 0 until mb.getMenuCount) {
        val m = mb.getMenu(i)
        if (m.getName == "File") {
          var k = 0
          for (j <- 0 until m.getItemCount) {
            val mj = m.getItem(j)
            if (mj.getName.startsWith("Save")) k = j + 1
          }
          m.insert(mi, k)
          return
        }
      }
      mb
    } else {
      val mb2 = new JMenuBar()
      f.setJMenuBar(mb2)
      mb2
    }

    val m = new JMenu("File")
    m.add(mi)
    mb1.add(m, 0)
  }

  def setupMenu(f: Frame) {
    val mi  = new MenuItem(this)
    val mb  = f.menuBar
    val mb1 = if (mb != MenuBar.NoMenuBar) {
      mb.menus.find(_.name == "File").foreach { m =>
        val i = m.contents.lastIndexWhere(_.name.startsWith("Save")) + 1
        m.contents.insert(i, mi)
        return
      }
      mb
    } else {
      val mb2 = new MenuBar
      f.menuBar = mb2
      mb2
    }

    val m = new Menu("File")
    m.contents += mi
    mb1.contents.insert(0, m)
  }

  def apply() {
    val viewsL = views.toList
    val viewO: Option[Generate.Source] = viewsL match {
      case Nil      => None
      case v :: Nil => Some(v)
      case _ =>
        var w = 0
        var h = 0
        viewsL.foreach { view =>
          val p   = if (usePreferredSize) view.preferredSize else view.size
          val pw  = math.min(64, p.width  >> 3)
          val ph  = math.min(64, p.height >> 3)
          w       = math.max(w, pw)
          h       = math.max(h, ph)
        }
        val list  = new ListView((1 to viewsL.size).map(i => s"#$i"))
        val icons = viewsL.zipWithIndex.map({
          case (view, idx) =>
            new Icon {
              def getIconWidth  = w
              def getIconHeight = h

              def paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
                val g2        = g.asInstanceOf[Graphics2D]
                val atOrig    = g2.getTransform
                val clipOrig  = g2.getClip
                g2.clipRect(x, y, w, h)
                g2.translate(x, y)
                g2.scale(0.125, 0.125)
                prepare(view)
                view.render(g2)
                g2.setTransform(atOrig)
                g2.setClip(clipOrig)
              }
            }
        })(breakOut)
        val jr = new DefaultListCellRenderer {
          override def getListCellRendererComponent(list: JList, value: Any, index: Int, isSelected: Boolean,
                                                    cellHasFocus: Boolean): java.awt.Component = {
            val res = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
            setIcon(icons(index))
            res
          }
        }
        list.renderer = new ListView.Renderer.Wrapped(jr)
        list.selectIndices(0)
        list.selection.intervalMode = ListView.IntervalMode.Single
        val scroll  = new ScrollPane(list)
        val res     = Dialog.showConfirmation(message = scroll.peer, title = title, optionType = Dialog.Options.OkCancel,
                                              messageType = Dialog.Message.Plain)
        val selIdx  = list.selection.leadIndex
        if (res == Dialog.Result.Ok && selIdx >= 0) Some(viewsL(selIdx)) else None
    }
    viewO foreach { view =>
      val fDlg  = new FileDialog((null: java.awt.Frame), title, FileDialog.SAVE)
      fDlg.setVisible(true)
      val file  = fDlg.getFile
      val dir   = fDlg.getDirectory
      if (file == null) return
      val fileExt = if (file.endsWith(".pdf")) file else file + ".pdf"
      prepare(view)
      Generate(new File(dir, fileExt), view, usePreferredSize = usePreferredSize, margin = margin, overwrite = true)
    }
  }
}