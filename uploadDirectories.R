# This R file allows one to upload a set of directories to mass storage


# It is assumed that this will be running in a windows environment locally 
# (rather than on a cloud service).
# It's assumed that the microsoft jar file will be in the same directory. 


# We take as input for this function the directory data will be stored in (workdir)
# the file with key data to access the particular Azure mass store (keyfile)
# and the container name to be downloaded (container.name)
copy.from.storage <- function(workdir,keyfile,container.name){
  
  my.java.exe <- myJavaExe(workdir)

	my.args <- c("-classpath", ".;microsoft-windowsazure-api-0.4.0.jar", 
								"containerIO.transferFromContainer", 
               keyfile, container.name)
	system2(command=my.java.exe,args=my.args)
#  return if the directory exists or not
	there <- file.exists(container.name)
	if (there){
		my.dir <- dir(container.name)
		print(paste("container contents =",container.name,my.dir))
	}
	there
}

# We take as input for this function the directory the function will be run from (workdir)
# the file with key data to access the particular Azure mass store (keyfile)
# and a sub-directory of workdir where the data will be copied from (sub.dir)
# Note :- only files in sub.dir will be copied from, not further sub sirectories
copy.to.storage <- function(workdir,keyfile,sub.dir){
  
  # my.java.exe is configured for a VM where java is not installed a priori  
  # If you are running this locally then my.java.exe can be replaced with 
  # my.java.exe <- "java"  
  my.java.exe <- myJavaExe(workdir)
  
  my.args <- c("-classpath", ".;microsoft-windowsazure-api-0.4.0.jar", 
               "containerIO.transferToContainer", keyfile, sub.dir)
  system2(command=my.java.exe,args=my.args)
  #  return if the directory exists or not
  there <- file.exists(sub.dir)
  if (there){
    my.dir <- dir(sub.dir)
    print(paste("container contents =",my.dir))
  }
  there
}

# my.java.exe is configured for a VM where java is not installed a priori  
# If you are running this locally then my.java.exe can be replaced with 
# my.java.exe <- "java"  
myJavaExe <- function(workdir,is.local=TRUE){
  if (is.local){
   "java"
  }
  else{
    paste(workdir, "jre6", "bin", "java.exe",sep=.Platform$file.sep)
  }
}



# Pass a vector of directories and the name of a key so that 
# the directories get uploaded as containers. 
# This
# The main script assumes that three arguments are passed to it 
# args[1] <- filename in csv format with list of work units stored in mass store
# args[2] <- output log file 
# args[3] <- filename with key data to access mass store
# It can also be assumed that data or extra scripts can be in the same directory where this 
# script is run when they are bundled into a zip file and uploaded on submission. 

uploadDirectories <- function(dirs,key.filename){
  

  for (this.dir in dirs) {
 
# Now upload output directory
    if ( copy.to.storage(getwd(),key.filename,this.dir) ){
      print(paste("Stored data for",this.dir,"to mass store"))
    }
# Note - do not stop execution if there is a failure to store    
    else{
      print(paste("Was not able to store data on",work.item))
    }
  }

}


