package com.overfused.asset.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

public class FileUtils {
	
	private static final Logger logger = Logger.getLogger(FileUtils.class.getName());
	
	private static final GcsService gcsService = GcsServiceFactory.createGcsService();
	
	public static void writeToFile(GcsFilename fileName, byte[] content) throws IOException {
		
		GcsOutputChannel outputChannel = gcsService.createOrReplace(fileName,
		    new GcsFileOptions.Builder().mimeType("image/x-png").build());
		outputChannel.write(ByteBuffer.wrap(content));
		
		outputChannel.close();
	}
	
	public static String[] appendOptionToPath(String option, String[] subDirs) {
		
		String[] result = new String[subDirs.length];
		for (int i = 0; i < subDirs.length; i++)
		{
			result[i] = option + File.separator + subDirs[i];
		}
		return result;
	}
	
	public static String[] getSubDirs(File file) {
		return file.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return dir.isDirectory();
			}
		});
	}
	
	public static String[] getSubDirsExcluding(File file, final String excludeDir) {
		return file.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return (dir.isDirectory() && name.indexOf(excludeDir) == -1);
			}
			
		});
	}
}
