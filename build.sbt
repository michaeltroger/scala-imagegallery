name := "imagegallery"

version := "1.1"

scalaVersion := "2.11.8"

idePackagePrefix := Some("com.michaeltroger.imagegallery")

libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"
libraryDependencies += "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.0.8"
libraryDependencies += "com.typesafe.play" %% "play-ws-standalone-json" % "2.0.8"