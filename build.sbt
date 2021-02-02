lazy val baseName  = "PDFlitz"
lazy val baseNameL = baseName.toLowerCase

lazy val projectVersion = "1.5.0"
lazy val mimaVersion    = "1.5.0"

lazy val deps = new {
  val main = new {
    val itext       = "5.5.13.2"
    val swingPlus   = "0.5.0"
  }
}

// sonatype plugin requires that these are in global
ThisBuild / version      := projectVersion
ThisBuild / organization := "de.sciss"

lazy val root = project.withId(baseNameL).in(file("."))
  .settings(
    name               := baseName,
//    version            := projectVersion,
//    organization       := "de.sciss",
    scalaVersion       := "2.13.4",
    crossScalaVersions := Seq("3.0.0-M3", "2.13.4", "2.12.12"),
    description        := "A simple action to export GUI components as PDF files",
    homepage           := Some(url(s"https://git.iem.at/sciss/${name.value}")),
    licenses           := Seq("AGPL v3+" -> url("http://www.gnu.org/licenses/agpl-3.0.txt")),
    initialCommands in console := """import de.sciss.pdflitz._""",
    libraryDependencies ++= Seq(
      "com.itextpdf" %  "itextpdf"  % deps.main.itext,
      "de.sciss"     %% "swingplus" % deps.main.swingPlus
    ),
    scalacOptions     ++= Seq("-deprecation", "-unchecked", "-feature", "-encoding", "utf8", "-Xlint", "-Xsource:2.13"),
    mimaPreviousArtifacts := Set("de.sciss" %% baseNameL % mimaVersion)
  )
  .settings(publishSettings)

// ---- publishing ----
lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  developers := List(
    Developer(
      id    = "sciss",
      name  = "Hanns Holger Rutz",
      email = "contact@sciss.de",
      url   = url("https://www.sciss.de")
    )
  ),
  scmInfo := {
    val h = "git.iem.at"
    val a = s"sciss/${name.value}"
    Some(ScmInfo(url(s"https://$h/$a"), s"scm:git@$h:$a.git"))
  },
)

