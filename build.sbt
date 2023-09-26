ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := "2.12.13"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "3.3.0" exclude("org.slf4j", "slf4j-log4j12"),
  "ch.qos.logback" % "logback-classic" % "1.4.11"
)

lazy val root = (project in file("."))
  .settings(
    name := "spark-test"
  )
