lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      scalaVersion := "2.13.6"
    )),
    name := "lrucache"
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % Test
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.6")