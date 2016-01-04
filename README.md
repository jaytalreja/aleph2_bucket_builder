# Aleph2 bucket builder

Enables buckets (or in fact any complex JSON object) to be built up by adding cards to a grid and using automatically-generated forms to configure them to build JSON based on a set of uploaded templates.

# Install

* Clone/download this project
* Install the following:
   * node.js
   * sbt
   * ant
* Run `sbt fullOptJS` then `ant` from the project root
* Copy the `dist/aleph2_bucket_builder.war` file onto the web server(s)

For development:
* Install an eclipse with Scala support, eg "Scala IDE"
* Install "sbteclipse":

Create/append to a file `~/.sbt/0.13/plugins/plugins.sbt`:
```
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "4.0.0")
```

* Install a static web server, eg (from the root of the project):

```
npm install node-static
static -p 8181
```

* Now set up the project by navigating to the project root, running `sbt` and executing:
 
```
eclipse
fastOptJS
```

Now you can modify the code in eclipse, use `fastOptJS` in sbt to compile it, and to test browse to `localhost:8181/assets/html/index-dev.html#/home`

Note there is currently no test suite for the webapp, this is forthcoming


