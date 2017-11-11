name := """play-scalikejdb"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
//  .dependsOn(macroNorm)
//lazy val macroNorm = ProjectRef(uri("https://github.com/nischalbasnet/norm-scalikejdbc.git"), "norm-scalikejdbc")

scalaVersion := "2.12.4"

val scalikejdbcVersion = "3.1.0"

libraryDependencies ++= Seq(
  jdbc,
  ehcache,
  ws,
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "com.typesafe.play" %% "play-json-joda" % "2.6.6",

  "org.postgresql" % "postgresql" % "42.1.4",

  "org.scalikejdbc" %% "scalikejdbc" % scalikejdbcVersion,
  "org.scalikejdbc" %% "scalikejdbc-config" % scalikejdbcVersion,
  "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % scalikejdbcVersion,
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.6.0-scalikejdbc-3.1",

  "org.flywaydb" %% "flyway-play" % "4.0.0",

  "joda-time" % "joda-time" % "2.9.9",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.chuusai" %% "shapeless" % "2.3.2",
  "io.reactivex" %% "rxscala" % "0.26.5"
)

scalikejdbcSettings