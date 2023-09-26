name := "spark-test"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "2.1.1" exclude("org.slf4j", "slf4j-log4j12"),
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.koddi" %% "geocoder" % "1.1.0",
  "net.liftweb" %% "lift-json" % "3.5.0",
  "org.web3j" %% "core" % "4.8.7"
)

mainClass in (Compile, run) := Some("com.example.test.ParquetSimpleTest")