 
## Shared files

Shared files should be those files which are common code between MicroServices while the code in the MicroService
should be the code which is specific to the service.

Shared files here should be synchronized by a file synchronization mechanism between JumpMicro projects to keep the code the same.

### Why?
  
The reason for this is that it is common that some code in one OSGi bundle should be used in another OSGi bundle but we don't want to have to update some shared OSGi bundle every time we need to share code as it may undergo frequent changes.
  
For example the model classes which are stored in the graph database do need to be shared across OSGi bundles but models will change frequently. Eventually when the changes to models have settled down we can place them in some shared OSGi bundle.
 
 Eventually the files in here should be pushed to a shared OSGi bundle as a shared library to be used.
 