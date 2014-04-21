package de.sciss.pdflitz

import scala.swing.{Action, CheckMenuItem, Menu, GridPanel, Swing, Component, Frame, SimpleSwingApplication}
import java.awt.{Font, RenderingHints, Color, LinearGradientPaint, Graphics2D}
import Swing._
import javax.swing.WindowConstants

object TestApp extends SimpleSwingApplication {
  lazy val top = new Frame {
    val comp = Seq(new Foo("PDF"), new Foo("litz"))
    contents = new GridPanel(1, 2) {
      contents ++= comp
    }
    val a = new SaveAction(comp.map(c => c: Generate.Source))
    a.margin = 32
    a.setupMenu(this)
    menuBar.contents += new Menu("Options") {
      contents += new CheckMenuItem(null) {
        action = Action("Use Preferred Size") {
          a.usePreferredSize = selected
        }
      }
    }
    pack()
    peer.setLocationRelativeTo(null)
    peer.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    open()
  }

  class Foo(name: String) extends Component {
    val rnd   = new util.Random()
    val c1    = new Color(rnd.nextInt()) // | 0xFF
    val c2    = new Color(rnd.nextInt() | 0xFF000000)

    preferredSize = (256, 256)

    foreground  = Color.white

    override protected def paintComponent(g: Graphics2D): Unit = {
      val d = size
      g.setPaint(new LinearGradientPaint(0f, 0f, d.width, d.height, Array(0f, 1f), Array(c1, c2)))
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g.fillOval(0, 0, d.width, d.height)
      val f = new Font(Font.SANS_SERIF, Font.PLAIN, 64)
      g.setFont(f)
      val fm = g.getFontMetrics(f)
      val sz = fm.stringWidth(name)
      g.setColor(foreground)
      val atOrig = g.getTransform
      // g.translate((d.width - sz) * 0.5f, (d.height + fm.getAscent) * 0.5f)
      g.translate(d.width * 0.5f, d.height * 0.5f)
      g.scale(d.width / 256.0, d.height / 256.0)
      g.translate(-sz * 0.5f, (fm.getAscent - fm.getDescent) * 0.5f)
      g.drawString(name, 0f, 0f)
      g.setTransform(atOrig)
    }
  }
}