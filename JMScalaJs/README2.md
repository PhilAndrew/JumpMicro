
* OLD README

Refer to https://github.com/doolse/sbt-osgi-felix

Can run it with Felix embedded with:

sbt run

Can create OSGi bundles with the following which creates a file in :

sbt osgiDeploy

If you want to run inside Felix, after osgiDeploy, copy all the jar files from .\target\launcher\bundle\* to Felix bundle directory.
Also change felix running to include sun.misc package with the Java option, for example:

java -Dorg.osgi.framework.bootdelegation=sun.misc -jar bin\felix.jar


