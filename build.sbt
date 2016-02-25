import sbt.Keys._

//enablePlugins(SbtNativePackager)


name := "KafkaTest"

organization in ThisBuild := "com.chariotsolutions"
version      in ThisBuild := "1.0"
scalaVersion in ThisBuild := "2.11.7"

resolvers in ThisBuild ++=
  Seq(
    Resolver.typesafeRepo("releases"),
    Resolver.bintrayRepo("websudos", "oss-releases"),
    "krasserm at bintray"              at "http://dl.bintray.com/krasserm/maven",
    "andsel at bintray"                at  "http://dl.bintray.com/andsel/maven/",
    "Sonatype repo"                    at "https://oss.sonatype.org/content/groups/scala-tools/",
    "Sonatype releases"                at "https://oss.sonatype.org/content/repositories/releases",
    "Sonatype staging"                 at "http://oss.sonatype.org/content/repositories/staging",
    "Java.net Maven2 Repository"       at "http://download.java.net/maven/2/",
    "Twitter Repository"               at "http://maven.twttr.com"
  )

conflictWarning in ThisBuild := ConflictWarning.disable

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-language:postfixOps",
  "-deprecation",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture"
)

javacOptions in Compile in ThisBuild ++= Seq("-source", "1.6", "-target", "1.6")

fork in run := true

libraryDependencies ++= {
  val AkkaVersion                     = "2.4.2"
  val JodaVersion                     = "2.9.2"
  val JodaConvertVersion              = "1.8.1"
  val LogbackVersion                  = "1.1.5"
  val ScalaTestVersion                = "2.2.6"
  val ScalaLoggingVersion             = "3.1.0"
  val NettyVersion                    = "3.10.3.Final"
  val NettyHandlerVersion             = "4.0.34.Final"
  val MockitoVersion                  = "1.10.19"
  val ReactiveKafkaVersion = "0.10.0"

  Seq(
    "ch.qos.logback"                              % "logback-classic"                     % LogbackVersion,
    "com.softwaremill.reactivekafka"             %% "reactive-kafka-core"                 % ReactiveKafkaVersion,
    "com.typesafe.akka"                          %% "akka-actor"                          % AkkaVersion
      exclude("io.netty", "netty"),
    "com.typesafe.akka"                          %% "akka-cluster-sharding"               % AkkaVersion,
    "com.typesafe.akka"                          %% "akka-slf4j"                          % AkkaVersion,
    "com.typesafe.akka"                          %% "akka-remote"                         % AkkaVersion
      exclude ("io.netty", "netty"),
    "com.typesafe.akka"                          %% "akka-persistence-query-experimental" % AkkaVersion,
    "com.typesafe.akka"                          %% "akka-stream"                         % AkkaVersion,
    "com.typesafe.akka"                          %% "akka-http-spray-json-experimental"   % AkkaVersion,
    "com.typesafe.scala-logging"                 %% "scala-logging"                       % ScalaLoggingVersion,
    "joda-time" 				                          % "joda-time" 						              % JodaVersion,
    "org.joda"                                    % "joda-convert"                        % JodaConvertVersion,
    "io.netty"                                    % "netty"                               % NettyVersion,
    "io.netty"                                    % "netty-handler"                       % NettyHandlerVersion,
    "com.typesafe.akka"                          %% "akka-testkit"                        % AkkaVersion                       % "test, provided",
    "org.mockito"                                 % "mockito-core"                        % MockitoVersion                    % "test, provided",
    "org.scalatest"                              %% "scalatest"                           % ScalaTestVersion                  % "test, provided"
  )
}

