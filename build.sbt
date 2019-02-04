name := "spark-test"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "2.4.0"
      exclude("org.slf4j", "slf4j-log4j12"),
  "org.apache.spark" %% "spark-mllib" % "2.4.0",
  "ch.qos.logback" % "logback-classic" % "1.1.7"
)

mainClass in (Compile, run) := Some("com.example.test.ParquetSimpleTest")