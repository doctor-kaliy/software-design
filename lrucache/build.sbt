lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      scalaVersion := "2.13.6"
    )),
    name := "lrucache"
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % Test