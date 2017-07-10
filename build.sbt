name := """play-scalikejdb"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,

  "org.postgresql" % "postgresql" % "9.4.1212",

  "org.scalikejdbc" %% "scalikejdbc" % "2.5.2",
  "org.scalikejdbc" %% "scalikejdbc-config" % "2.5.2",
  "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % "2.5.2",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.5.1",

  "org.flywaydb" %% "flyway-play" % "3.1.0",

  "joda-time" % "joda-time" % "2.9.9",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.chuusai" %% "shapeless" % "2.3.2",
  "io.reactivex" %% "rxscala" % "0.26.4"
)

addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M9" cross CrossVersion.full)
scalacOptions += "-Xplugin-require:macroparadise"
scalacOptions in (Compile, console) ~= (_ filterNot (_ contains "paradise")) // macroparadise plugin doesn't work in repl yet.

scalikejdbcSettings