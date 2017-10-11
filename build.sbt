name := "Twitter_snchange"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "org.twitter4j".%("twitter4j-core") % "4.0.4",
  "org.twitter4j".%("twitter4j-async") % "4.0.4",
  "org.twitter4j".%("twitter4j-stream") % "4.0.4",
  "org.scalikejdbc" %% "scalikejdbc" % "3.1.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalactic" %% "scalactic" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "mysql" % "mysql-connector-java" % "5.1.16"
)
