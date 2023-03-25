ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "kantan-csv-generic-scala3",
    libraryDependencies ++= Seq(
      ("com.nrinaudo" %% "kantan.csv" % "0.7.0")
        .cross(CrossVersion.for3Use2_13),
      "org.typelevel" %% "shapeless3-deriving" % "3.3.0",
      "org.scalameta" %% "munit" % "0.7.29" % "test"
    )
  )
