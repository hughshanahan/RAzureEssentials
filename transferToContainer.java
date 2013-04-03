/*
 * transferToContainer.java
 * 
 * Copyright 2012 Hugh Philip Shanahan <hugh@hsmacbook.local>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 * 
 */
 

package containerIO;

import com.microsoft.windowsazure.services.core.storage.*;
import com.microsoft.windowsazure.services.blob.client.*;
import java.util.*;
import java.io.*;
import java.lang.*;



public class transferToContainer {
	

	public static void main (String[] args){
		
		CloudStorageAccount storageAccount;
		String storageConnectionString;
		
// Retrieve storage account from connection-string
		try { 
						
				if ( args.length != 2 ){
					System.out.println("Usage :- <transferToContainer> <mass store parameters file> <directory to transfer>");
				}

			
//      First read the mass store parameters file and create connectionString

				storageConnectionString = createConnectionString(args[0]);
				
// Set up connection to Mass store

				storageAccount = CloudStorageAccount.parse(storageConnectionString); 
			
				uploadContainer(args[1],storageAccount);
				
			}catch (Exception e)
			{//Catch exception if any
				System.err.println("Error: " + e.getMessage());

			}	
						
	}
	
	public static void uploadContainer (String dirPath, CloudStorageAccount storageAccount){
				
		try{		

			File fDir = new File(dirPath);
			
			String containerName = fDir.getName().toLowerCase();
			
		// Create the blob client

			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
	
			CloudBlobContainer container = blobClient.getContainerReference(containerName);

			container.createIfNotExist();		
			
		// Step through entries in directory
		// upload files as blobs
		// ignore sub-directories and other non-file entries

			for ( File thisFile : fDir.listFiles() ){
				if ( thisFile.isFile() ){
					CloudBlockBlob blob = container.getBlockBlobReference(thisFile.getName());

					blob.upload(new FileInputStream(thisFile), thisFile.length());
				}	
			    
			}	
		}
		catch(Exception e){
// If there is a failure delete what was downloaded
			System.out.println("uploadContainer : Error message is "+e);
		}

	}
	
	// Method for creating a connection string to Azure - it reads in the data from a file (storage name and key) and
// then makes the string.	
	private static String createConnectionString (String fileName){
			try {

				FileInputStream fstream = new FileInputStream(fileName);

			// Get the object of DataInputStream

				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				
			//Read Account Name followed by Key 
				
				String AccountName = br.readLine();
				String AccountKey = br.readLine();
			
				String storageConnectionString = 
					"DefaultEndpointsProtocol=http;" + 
					"AccountName="+ AccountName + ";" +
					"AccountKey=" + AccountKey;
	
				in.close();
			
				return(storageConnectionString);		
			}
			catch (Exception e){
				//Catch exception if any
				System.err.println("Error: " + e.getMessage());
				return("");		
			}
	}

	
}	

