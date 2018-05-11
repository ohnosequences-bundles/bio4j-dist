name         := "bio4j-dist"
organization := "ohnosequences-bundles"
description  := "A bundle for the Bio4j distributions"

publishBucketSuffix := "era7.com"

crossScalaVersions := Seq("2.11.12", "2.12.6")
scalaVersion := crossScalaVersions.value.max

resolvers ++= Seq(
  "Era7 public maven releases" at s3("releases.era7.com").toHttps(s3region.value)
)

libraryDependencies ++= Seq(
  "ohnosequences" %% "statika"         % "3.0.0",
  "ohnosequences" %% "aws-scala-tools" % "0.21.0",
  "bio4j"         %  "bio4j"           % "0.12.0",
  "bio4j"         %  "bio4j-titan"     % "0.4.0"
)

dependencyOverrides ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-core"        % "2.6.7",
  "com.fasterxml.jackson.core" % "jackson-databind"    % "2.6.7",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.6.7",
  "commons-logging"            % "commons-logging"     % "1.1.3",
  "commons-codec"              % "commons-codec"       % "1.9",
  "org.apache.httpcomponents"  % "httpclient"          % "4.5.2",
  "com.google.guava"           % "guava"               % "14.0.1"
)
