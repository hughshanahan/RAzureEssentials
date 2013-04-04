This is a set of useful executables and scripts to run R in Azure with the
Generic Worker.
The java code allows one to download or update data to a container on the
Azure mass storage. To access your mass storage, you will need to construct a
key file, which is a simple text file which takes the form
<mass store name>
<primary key> 
which you can find from your azure manager pages. 

To compile the java you will also need Microsoft's SDK jar file, a copy of
which is in the git directory. You  can also download this directly from
Microsoft (easily found is you so a search for Azure Java SDK). 

If you need to compile the classes again you can do so by typing

javac -d . -cp microsoft-windowsazure-api-0.4.0.jar transferFromContainer.java
javac -d . -cp microsoft-windowsazure-api-0.4.0.jar transferToContainer.java

and running them by typing
java -classpath microsoft-windowsazure-api-0.4.0.jar containerIO.transferFromContainer <key file name> <container ID> 
to download a container (using the key file you've made)
and 
java -classpath microsoft-windowsazure-api-0.4.0.jar containerIO.transferToContainer <key file name> <sub directory>  
to transefer a directory to container storage.

exampleScript.R is an example of an R script that can be run in GWydiR to make
use of all this.

