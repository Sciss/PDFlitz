/*
 *  Action.scala
 *  (PDFlitz)
 *
 *  Copyright (c) 2013-2020 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is published under the GNU Affero General Public License v3+
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package de.sciss.pdflitz

import java.awt.{Component, FileDialog, Graphics, Graphics2D}
import java.io.File

import de.sciss.swingplus.ListView
import javax.swing.{Icon, JFrame, JMenu, JMenuBar, JMenuItem}

import scala.swing.{Action, Dialog, Frame, Menu, MenuBar, MenuItem, ScrollPane}

object SaveAction {
  def apply(views: => Iterable[Generate.Source]): SaveAction = new SaveAction(views)
}
class SaveAction(views: => Iterable[Generate.Source]) extends Action("Save As PDF...") {
  /** May be overridden. */
  protected def prepare(view: Generate.Source): Unit = ()

  var usePreferredSize  = false
  var margin            = 0

  def setupMenu(f: JFrame): Unit = {
    val mi  = new JMenuItem(peer)
    val mb  = f.getJMenuBar
    val mb1 = if (mb != null) {
      var i = 0
      while (i < mb.getMenuCount) {
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
        i += 1
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

  def setupMenu(f: Frame): Unit = {
    val mi  = new MenuItem(this)
    val mb  = f.menuBar
    val mb1 = if (mb != MenuBar.NoMenuBar) {
      mb.menus.find(_.name == "File") match {
        case Some(_m) =>
          val i = _m.contents.lastIndexWhere(_.name.startsWith("Save")) + 1
          _m.contents.insert(i, mi)
          return
        case None =>
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

  def apply(): Unit = {
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
        val icons = viewsL.iterator.map { view =>
          new Icon {
            def getIconWidth  : Int = w
            def getIconHeight : Int = h

            def paintIcon(c: Component, g: Graphics, x: Int, y: Int): Unit = {
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
        } .toIndexedSeq
        val lr = new ListView.LabelRenderer[String] {
          def configure(list: ListView[_], isSelected: Boolean, focused: Boolean, a: String, index: Int): Unit =
            component.icon = icons(index)
        }
        list.renderer = lr
        list.selectIndices(0)
        list.selection.intervalMode = ListView.IntervalMode.Single
        val scroll  = new ScrollPane(list)
        val res     = Dialog.showConfirmation(message = scroll.peer, title = title, optionType = Dialog.Options.OkCancel,
                                              messageType = Dialog.Message.Plain)
        val selIdx  = list.selection.leadIndex
        if (res == Dialog.Result.Ok && selIdx >= 0) Some(viewsL(selIdx)) else None
    }
    viewO match {
      case Some(view) =>
        val fDlg  = new FileDialog(null: java.awt.Frame, title, FileDialog.SAVE)
        fDlg.setVisible(true)
        val file  = fDlg.getFile
        val dir   = fDlg.getDirectory
        if (file == null) return
        val fileExt = if (file.endsWith(".pdf")) file else file + ".pdf"
        prepare(view)
        Generate(new File(dir, fileExt), view, usePreferredSize = usePreferredSize, margin = margin, overwrite = true)

      case None =>
    }
  }
}