package uk.co.rogerstech.servalot;

import java.io.File;

public class Servalot {

	public static void main(String[] args){

        // Set filesDir to ~/.servalot/files
		File filesDir = new File(System.getProperty("user.home"),".servalot");
		filesDir = new File(filesDir,"files");

		System.out.println("Files Directory: "+filesDir.getAbsolutePath());

		// Start service manager
		ServiceManager serviceManager = new ServiceManager(filesDir, new File(filesDir,"services.tsv"));
		serviceManager.startAll();

        // Endless loop (service threads should be running)
		int cnt=0;
		while(true) {
			try {
				// Sleep for 10 seconds
				Thread.sleep(10000);
                cnt+=10;
				System.out.println(""+cnt);
				
			} catch(InterruptedException e) {
			}
		}
	}
}

