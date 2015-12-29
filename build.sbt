// Plugins

enablePlugins(ScalaJSPlugin)
enablePlugins(SbtWeb)

// Meta

name := "aleph2_bucket_builder"

version := "0.1"

scalaVersion := "2.11.7" // or any other Scala version >= 2.10.2

// Scala deps:

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.8.0"

libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.3.6"

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "com.greencatsoft" %%% "scalajs-angular" % "0.7-SNAPSHOT"

// Angular:

jsDependencies += "org.webjars.bower" % "angular" % "1.3.15" / "angular.js" 

libraryDependencies += "org.webjars.bower" % "angular-route" % "1.3.15" exclude ("org.webjars.bower", "angular")
jsDependencies += "org.webjars.bower" % "angular-route" % "1.3.15" / "angular-route.js" dependsOn "angular.js"

libraryDependencies += "org.webjars.bower" % "angular-sanitize" % "1.3.15" exclude ("org.webjars.bower", "angular")
jsDependencies += "org.webjars.bower" % "angular-sanitize" % "1.3.15" / "angular-sanitize.js" dependsOn "angular.js"

// Bootstrap

libraryDependencies += "org.webjars.bower" % "bootstrap" % "3.3.6"
//(don't pull the JS for this since we only want its CSS, and its JS requires JQuery)

libraryDependencies += "org.webjars.bower" % "angular-bootstrap" % "0.13.0" exclude ("org.webjars.bower", "angular")
jsDependencies += "org.webjars.bower" % "angular-bootstrap" % "0.13.0" / "ui-bootstrap-tpls.js" dependsOn "angular.js"

// Components: form

jsDependencies += "org.webjars.bower" % "api-check" % "7.5.0" / "dist/api-check.js" dependsOn "angular.js"
	//(create fake dep to avoid multiple dependencies)
jsDependencies += "org.webjars.bower" % "angular-formly" % "7.3.0" / "formly.js" dependsOn "dist/api-check.js"

libraryDependencies += "org.webjars.bower" % "angular-formly-templates-bootstrap" % "6.1.5" exclude ("org.webjars.bower", "bootstrap")
jsDependencies += "org.webjars.bower" % "angular-formly-templates-bootstrap" % "6.1.5" / "angular-formly-templates-bootstrap.js" dependsOn "formly.js"

// Components: grid

libraryDependencies += "org.webjars.bower" % "angular-gridster" % "0.13.5" exclude ("org.webjars.bower", "angular")
jsDependencies += "org.webjars.bower" % "angular-gridster" % "0.13.5" / "angular-gridster.js" dependsOn "angular.js"

// Components: tree

libraryDependencies += "org.webjars.bower" % "angular-tree-control" % "0.2.9" exclude ("org.webjars.bower", "angular")
jsDependencies += "org.webjars.bower" % "angular-tree-control" % "0.2.9" / "angular-tree-control.js" dependsOn "angular.js"

// TESTING

libraryDependencies += "com.lihaoyi" %%% "utest" % "0.3.0" % "test"

testFrameworks += new TestFramework("utest.runner.Framework")

// Finally:

skip in packageJSDependencies := false

persistLauncher in Compile := true

persistLauncher in Test := false

