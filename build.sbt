name := """chatmenAPI"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
scalaVersion := "2.12.8"
libraryDependencies += guice

libraryDependencies ++= Seq(
  // --[ Private ]------------------------------------------
  "net.ixias"    %% "ixias"         % "1.1.11",
  "net.ixias"    %% "ixias-aws"     % "1.1.11",
  "net.ixias"    %% "ixias-play"    % "1.1.11",
  "chatmen-app"  %% "chatmen"       % "1.0.1-SNAPSHOT",

  // --[ OSS ]----------------------------------------------
  "mysql"                % "mysql-connector-java"     % "5.1.39"
  )
