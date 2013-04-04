# Example R script to download data from mass storage 
# do something trivial and then store it elsewhere.

# It is assumed that this will be running in a windows environment


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
  there <- file.exists(container.name)
  if (there){
    my.dir <- dir(container.name)
    print(paste("container contents =",container.name,my.dir))
  }
  there
}

# my.java.exe is configured for a VM where java is not installed a priori  
# If you are running this locally then my.java.exe can be replaced with 
# my.java.exe <- "java"  
myJavaExe <- function(workdir,is.local=FALSE){
  if (is.local){
   "java"
  }
  else{
    paste(workdir, "jre6", "bin", "java.exe",sep=.Platform$file.sep)
  }
}



# The main script assumes that three arguments are passed to it 
# args[1] <- filename in csv format with list of work units stored in mass store
# args[2] <- output log file 
# args[3] <- filename with key data to access mass store
# It can also be assumed that data or extra scripts can be in the same directory where this 
# script is run when they are bundled into a zip file and uploaded on submission. 

args <- commandArgs(TRUE)
csv.filename <- args[1]
sink(file=args[2])
key.filename <- args[3]

# Print out some diagnostics
date()
my.dir <- dir()
print(my.dir)
workdir <- getwd()
print(workdir)

# load scripts, data from initial script here.
#load("Gstack.affy.ids.Rdata")
#source("histbox1.txt")
#source("makeallplots1_hgu133a.txt")

# We assume that the csv file will have an arbitrary list of work items to process
work.items <- read.table(csv.filename, header=FALSE, sep=",")

print(work.items)

# Loop through work items - we will do something fairly trivial here 
# (compute size of files and store their sum)
# with each work item, but this is primarily where the changes need to be made
for (i in 1:length(work.items[,1])) {
  work.item <- work.items[i,1]

  print(paste("This is what we are downloading", work.item))
# Try copying data  
  if ( copy.from.storage(workdir,key.filename,as.vector(work.item)) ) {
  	item.dir <- paste(workdir, .Platform$file.sep, work.item, sep="")
	  print(item.dir)
	  setwd(item.dir)
    my.files <- list.files()
    size.of.files <- sum(file.info(my.files)[,"size"])
    print(paste("Size of files in",item.dir,"is",size.of.files))  
# Save results in new directory
    setwd(my.dir)
    results.dir <- paste(work.item,"Results",sep="")
    dir.create(results.dir)
    setwd(work.item)
    save(size.of.files="asizes.Rdata")
    setwd(my.dir)
# Now upload output directory (log files are also stored)
    if ( copy.to.storage(workdir,keyfilename,work.item) ){
      print(paste("Stored data for",work.item,"to mass store"))
    }
# Note - do not stop execution if there is a failure to store    
    else{
      print(paste("Was not able to store data on",work.item))
    }

# Delete data from mass store at end of loop so things are 
# clear for next iteration
    unlink(work.item,recursive=TRUE)
    unlink(results.dir,recursive=TRUE)

  }
# Give error message if failure to download data  
  else{
    print(paste("Could not download",work.item,"from mass store"))
  }

}


