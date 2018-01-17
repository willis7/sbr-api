/**
  * PROJECT DEF
  */
lazy val api = (project in file("."))
  .enablePlugins(BuildInfoPlugin, PlayScala)
  .disablePlugins(AssemblyPlugin)
  .settings(
    developers := List(Developer("Adrian Harris (Tech Lead)", "SBR", "ons-sbr-team@ons.gov.uk", new java.net.URL(s"https:///v1/home"))),
    moduleName := "sbr-api",
    organizationName := "ons",
    description := "<description>",
    version := (version in ThisBuild).value,
    name := s"${organizationName.value}-${moduleName.value}",
    licenses := Seq("MIT-License" -> url("https://github.com/ONSdigital/sbr-control-api/blob/master/LICENSE")),
    startYear := Some(2017),
    homepage := Some(url("https://SBR-UI-HOMEPAGE.gov.uk")),
//    routesGenerator := InjectedRoutesGenerator
    libraryDependencies ++= devDeps
  )

/**
  * SETTINGS AND CONFIGURATION
  */
buildInfoPackage := "controllers"

coverageMinimum := 55

lazy val devDeps = Seq(
  ws,
  filters,
  "io.lemonlabs"                 %%    "scala-uri"           %    "0.5.0"
    excludeAll ExclusionRule("commons-logging", "commons-logging")
)