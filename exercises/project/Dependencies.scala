import com.lightbend.cinnamon.sbt.Cinnamon
import sbt._

object Version {
  val akkaVer         = "2.6.4"
  val logbackVer      = "1.2.3"
  val scalaVer        = "2.13.1"
  val scalaParsersVer = "1.1.2"
  val assertjVer      = "3.15.0"
  val junitVer        = "0.11"
  val quavaVer        = "23.0"
}

object Dependencies {
  val dependencies = Seq(
    "com.google.guava"         %  "guava"                      % Version.quavaVer,
    "com.typesafe.akka"        %% "akka-actor"                 % Version.akkaVer,
    "com.typesafe.akka"        %% "akka-slf4j"                 % Version.akkaVer,
    "ch.qos.logback"           %  "logback-classic"            % Version.logbackVer,
    "org.scala-lang.modules"   %% "scala-parser-combinators"   % Version.scalaParsersVer,
    "com.lightbend.akka" %% "akka-diagnostics" % "1.1.12",
    Cinnamon.library.cinnamonAkka,
    Cinnamon.library.cinnamonJvmMetricsProducer,
    Cinnamon.library.cinnamonPrometheus,
    Cinnamon.library.cinnamonPrometheusHttpServer,
    "com.typesafe.akka"        %% "akka-testkit"               % Version.akkaVer            % Test,
    "com.novocode"             %  "junit-interface"            % Version.junitVer           % Test,
    "org.assertj"              %  "assertj-core"               % Version.assertjVer         % "test"
  )
}
