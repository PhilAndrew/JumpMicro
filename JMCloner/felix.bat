call sbt deployLauncher:package
copy jumpmicro.conf target\launcher
cd target\launcher
call java -Dorg.osgi.framework.bootdelegation=sun.misc -jar lib/org.apache.felix.main-5.0.0.jar
cd ..
cd ..

