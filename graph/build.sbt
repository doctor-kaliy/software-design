lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "",
      scalaVersion := "2.13.10"
    )),
    name := "graph"
  )
libraryDependencies += "org.openjfx" % "javafx-controls" % "17"
