name               := "PDFlitz"

version            := "1.1.0"

organization       := "de.sciss"

scalaVersion       := "2.11.0"

crossScalaVersions := Seq("2.11.0", "2.10.4")

description        := "A simple action to export GUI components as PDF files"

homepage           := Some(url("https://github.com/Sciss/" + name.value))

licenses           := Seq("GPL v3+" -> url("http://www.gnu.org/licenses/gpl-3.0.txt"))

initialCommands in console := """import de.sciss.pdflitz._"""

libraryDependencies += "com.itextpdf" % "itextpdf" % "5.5.0"

libraryDependencies += { val sv = scalaVersion.value
  if (sv startsWith "2.10")
    "org.scala-lang" % "scala-swing" % sv
  else
    "org.scala-lang.modules" %% "scala-swing" % "1.0.1"
}

// retrieveManaged := true

scalacOptions     ++= Seq("-deprecation", "-unchecked", "-feature", "-Xfuture")

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
