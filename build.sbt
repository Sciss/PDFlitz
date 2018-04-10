lazy val baseName  = "PDFlitz"
lazy val baseNameL = baseName.toLowerCase

lazy val projectVersion = "1.3.0-SNAPSHOT"
lazy val mimaVersion    = "1.3.0"

lazy val itextVersion       = "5.5.13"
lazy val swingPlusVersion   = "0.3.0-SNAPSHOT"

lazy val root = project.withId(baseNameL).in(file("."))
  .settings(
    name               := baseName,
    version            := projectVersion,
    organization       := "de.sciss",
    scalaVersion       := "2.12.5",
    crossScalaVersions := Seq("2.12.5", "2.11.12"),
    description        := "A simple action to export GUI components as PDF files",
    homepage           := Some(url(s"https://github.com/Sciss/${name.value}")),
    licenses           := Seq("GPL v3+" -> url("http://www.gnu.org/licenses/gpl-3.0.txt")),
    initialCommands in console := """import de.sciss.pdflitz._""",
    libraryDependencies ++= Seq(
      "com.itextpdf" %  "itextpdf"  % itextVersion,
      "de.sciss"     %% "swingplus" % swingPlusVersion
    ),
    scalacOptions     ++= Seq("-deprecation", "-unchecked", "-feature", "-encoding", "utf8", "-Xfuture", "-Xlint")
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
  <url>git@github.com:Sciss/{n}.git</url>
  <connection>scm:git:git@github.com:Sciss/{n}.git</connection>
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

