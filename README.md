# PDFlitz

[![Flattr this](http://api.flattr.com/button/flattr-badge-large.png)](https://flattr.com/submit/auto?user_id=sciss&url=https%3A%2F%2Fgithub.com%2FSciss%2FPDFlitz&title=PDFlitz%20Library&language=Scala&tags=github&category=software)
[![Build Status](https://travis-ci.org/Sciss/PDFlitz.svg?branch=master)](https://travis-ci.org/Sciss/PDFlitz)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sciss/pdflitz_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sciss/pdflitz_2.11)

## statement

PDFlitz is a small glue code to the iTextPDF library, allowing the export of any Java or Scala Swing component as PDF file. It is (C)opyright 2013&ndash;2015 by Hanns Holger Rutz. All rights reserved. PDFlitz is released under the [GNU General Public License](https://raw.github.com/Sciss/ScalaAudioFile/master/LICENSE) v3+ and comes with absolutely no warranties. To contact the author, send an email to `contact at sciss.de`. iTextPDF is covered by the GNU AGPL.

## requirements / installation

PDFlitz currently compiles against Scala 2.11, 2.10 using sbt 0.13.

To use the library in your project:

    "de.sciss" %% "pdflitz" % v

The current version `v` is `"1.2.1"`

## getting started

A simple demo can be run through `sbt test:run`.

## notes

- as of v1.2.1, we use `onlyShapes = true` which means that text is always rendered as glyph vectors. That way we do not have to deal with font mapping. The disadvantage is that text is not editable in the resulting PDF. A future version may provide font mapping and an option to disable `onlyShapes`
