Nice.scalaProject

name := "bio4j-dist"
organization := "ohnosequences-bundles"
description := "A bundle for the Bio4j distributions"

publishBucketSuffix := "era7.com"

resolvers ++= Seq(
  "Era7 public maven releases"  at s3("releases.era7.com").toHttps(s3region.value.toString),
  "Era7 public maven snapshots" at s3("snapshots.era7.com").toHttps(s3region.value.toString)
)

libraryDependencies ++= Seq(
  "ohnosequences" %% "statika"         %  "2.0.0-M4",
  "ohnosequences" %% "aws-scala-tools" %  "0.14.0",
  "bio4j"         %  "bio4j"           %  "0.12.0-RC3",
  "bio4j"         %  "bio4j-titan"     %  "0.4.0-RC2"
)

dependencyOverrides ++= Set(
  "com.fasterxml.jackson.core" % "jackson-core"        % "2.3.2",
  "com.fasterxml.jackson.core" % "jackson-databind"    % "2.3.2",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.3.2",
  "commons-logging"            % "commons-logging"     % "1.1.3",
  "commons-codec"              % "commons-codec"       % "1.7",
  "org.apache.httpcomponents"  % "httpclient"          % "4.5",
  "org.slf4j"                  % "slf4j-api"           % "1.7.7"
)
