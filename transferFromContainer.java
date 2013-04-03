/*
 * transferFromContainer.java
 * 
 * Copyright 2013 Hugh Shanahan <hugh@cssbws17>
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

public class transferFromContainer {
	

	public static void main (String[] args){
		
		CloudStorageAccount storageAccount;
		String storageConnectionString;
		
// Retrieve storage account from connection-string
		try { 
						
				if ( args.length != 2 ){
					System.out.println("Usage :- <transferFromContainer> <mass store parameters file> <container ID>");
				}

			
//      First read the mass store parameters file and create connectionString

				storageConnectionString = createConnectionString(args[0]);
				
// Set up connection to Mass store

				storageAccount = CloudStorageAccount.parse(storageConnectionString); 
				
				downloadContainer(args[1],storageAccount);
				
			}catch (Exception e)
			{//Catch exception if any
				System.err.println("Error: " + e.getMessage());

			}						
	
	}

	
	public static void downloadContainer (String gse, CloudStorageAccount storageAccount){
				
		try{		

		// Create the blob client

			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
	
			CloudBlobContainer container = blobClient.getContainerReference(gse);
		
		// Make a directory and save data from each container in it

			new File(gse).mkdir();
			
			for (ListBlobItem blobItem : container.listBlobs()) {
					if ( blobItem instanceof CloudBlob ){
						CloudBlob blob = (CloudBlob) blobItem;
						String filePath = gse + File.separator + blob.getName();
						blob.download(new FileOutputStream(filePath));
					}        
			}	
		}
		catch(Exception e){
// If there is a failure delete what was downloaded
			System.out.println("Cannot access: "+gse+" Error message is: "+e);
			System.out.println("Deleting data");
			deleteDir(new File(gse));
			System.out.println("Finished cleaning up");
		}

	}

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
	
// Deletes all files and subdirectories under dir.
// Returns true if all deletions were successful.
// If a deletion fails, the method stops attempting to delete and returns false.	
	private static boolean deleteDir(File dir) {

		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		
		return true;
	}
		
}

