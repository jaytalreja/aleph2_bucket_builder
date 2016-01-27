# Aleph2 bucket builder

Enables buckets (or in fact any complex JSON object) to be built up by adding cards to a grid and using automatically-generated forms to configure them to build JSON based on a set of uploaded templates.

![screenshot](https://raw.githubusercontent.com/Alex-Ikanow/aleph2_bucket_builder/master/aleph2_bucket_builder.png)

Live demos:
* [Bucket builder](http://alex-ikanow.github.io/aleph2_bucket_builder/assets/html/sample_index.html#/home)
* [Form builder](http://alex-ikanow.github.io/aleph2_bucket_builder/assets/html/form_builder.html#/home)

The [wiki](https://github.com/Alex-Ikanow/aleph2_bucket_builder/wiki) contains the user documentation for the builder.

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


