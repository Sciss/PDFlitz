lazy val baseName  = "PDFlitz"
lazy val baseNameL = baseName.toLowerCase

lazy val projectVersion = "1.4.1"
lazy val mimaVersion    = "1.4.0"

lazy val deps = new {
  val main = new {
    val itext       = "5.5.13"
    val swingPlus   = "0.4.2"
  }
}

lazy val root = project.withId(baseNameL).in(file("."))
  .settings(
    name               := baseName,
    version            := projectVersion,
    organization       := "de.sciss",
    scalaVersion       := "2.12.8",
    crossScalaVersions := Seq("2.12.8", "2.11.12", "2.13.0-RC1"),
    description        := "A simple action to export GUI components as PDF files",
    homepage           := Some(url(s"https://git.iem.at/sciss/${name.value}")),
    licenses           := Seq("GPL v3+" -> url("http://www.gnu.org/licenses/gpl-3.0.txt")),
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
  publishTo := {
    Some(if (isSnapshot.value)
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    else
      "Sonatype Releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
    )
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  pomExtra := { val n = name.value
<scm>
  <url>git@git.iem.at:sciss/{n}.git</url>
  <connection>scm:git:git@git.iem.at:sciss/{n}.git</connection>
</scm>
<developers>
   <developer>
      <id>sciss</id>
      <name>Hanns Holger Rutz</name>
      <url>http://www.sciss.de</url>
   </developer>
</developers>
  }
)

