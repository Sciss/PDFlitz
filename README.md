# PDFlitz

[![Build Status](https://travis-ci.org/Sciss/PDFlitz.svg?branch=main)](https://travis-ci.org/Sciss/PDFlitz)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sciss/pdflitz_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sciss/pdflitz_2.13)

## statement

PDFlitz is a small glue code to the iTextPDF library, allowing the export of any Java or Scala Swing component as PDF file. It is (C)opyright 2013&ndash;2019 by Hanns Holger Rutz. All rights reserved. PDFlitz is released under the [GNU Affero General Public License](https://raw.github.com/Sciss/PDFlitz/main/LICENSE) v3+ and comes with absolutely no warranties. To contact the author, send an e-mail to `contact at sciss.de`.

## requirements / installation

The project builds with sbt against Scala 2.13, 2.12, Dotty. The last version to support Scala 2.11 was 1.4.1.

To use the library in your project:

    "de.sciss" %% "pdflitz" % v

The current version `v` is `"1.5.0"`

## getting started

A simple demo can be run through `sbt test:run`.

## notes

- as of v1.2.1, we use `onlyShapes = true` which means that text is always rendered as glyph vectors. That way we do not have to deal with font mapping. The disadvantage is that text is not editable in the resulting PDF. A future version may provide font mapping and an option to disable `onlyShapes`
