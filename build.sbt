name := "PDFlitz"

version := "1.0.2-SNAPSHOT"

organization := "de.sciss"

scalaVersion := "2.11.0-RC3"

crossScalaVersions := Seq("2.11.0-RC3", "2.10.3")

description := "A simple action to export GUI components as PDF files"

homepage <<= name { n => Some(url("https://github.com/Sciss/" + n)) }

licenses := Seq("GPL v3+" -> url("http://www.gnu.org/licenses/gpl-3.0.txt"))

initialCommands in console := """import de.sciss.pdflitz._"""

libraryDependencies in ThisBuild ++= Seq(
  "com.itextpdf" % "itextpdf" % "5.4.1"
)

libraryDependencies in ThisBuild += { val sv = scalaVersion.value
  if (sv startsWith "2.10")
    "org.scala-lang" % "scala-swing" % sv
  else
    "org.scala-lang.modules" %% "scala-swing" % "1.0.1"
}

// retrieveManaged := true

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

// ---- publishing ----

publishMavenStyle := true

publishTo :=
  Some(if (version.value endsWith "-SNAPSHOT")
    "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  else
    "Sonatype Releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
  )

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

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
