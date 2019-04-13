name := "AprendizadoVirtualAPI"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava, LauncherJarPlugin)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

scalaVersion := "2.12.2"

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  guice,
  javaJpa,
  ehcache,
  javaWs,
  filters,
  "org.hibernate" % "hibernate-entitymanager" % "5.0.5.Final",
  "mysql" % "mysql-connector-java" % "5.1.18",
  "com.fasterxml.jackson.module" % "jackson-module-jaxb-annotations" % "2.7.3",
  "net.logstash.logback" % "logstash-logback-encoder" % "4.11",
  "javax.mail" % "mail" % "1.4.1"

)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

PlayKeys.externalizeResources := false
//Disable API Documentation generation
sources in (Compile, doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false

