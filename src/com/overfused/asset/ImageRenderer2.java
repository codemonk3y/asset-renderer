package com.overfused.asset;

import static com.overfused.asset.model.Constants.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.common.collect.Sets;
import com.motorola.asset.util.FileUtils;
import com.motorola.asset.util.ImageUtils;



public class ImageRenderer2 extends HttpServlet {
	
	/**
     * 
     */
	private static final long serialVersionUID = -5527256127762965467L;
	
	private static final Logger logger = Logger.getLogger(ImageRenderer2.class.getName());
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		PrintWriter out = resp.getWriter();
		File rootSubdir = new File(ROOT_DIR);
		String[] devices = FileUtils.getSubDirs(rootSubdir);
		
		for (String device : devices)
		{
			out.println("Processing device: " + device + "<br/>");
			String[] views = FileUtils.getSubDirs(new File(rootSubdir.getPath() + File.separator + device));
			for (String view : views)
			{
				out.println("Processing view: " + view + "<br/>");
				String partPath = rootSubdir.getPath() + File.separator + device + File.separator + view;
				String[] options = FileUtils.getSubDirsExcluding(new File(rootSubdir.getPath() + File.separator
				        + device + File.separator + view), "consistent");
				
				Map<String, String[]> optionValues = getOptionValues(device, view, options);
				List<Set<String>> sets = new ArrayList<Set<String>>();
				SortedSet<String> keys = new TreeSet<String>(optionValues.keySet());
			
				for (String key : keys)
				{
					logger.info(key);
					String[] partIDs = optionValues.get(key);
					Set<String> partIDsSet = new HashSet<String>(Arrays.asList(partIDs));
					sets.add(partIDsSet);
				}
				
				if (!sets.isEmpty())
				{
					processImages(device, view, partPath, sets);
				}
			}
		}
		
		out.println("Finished merging composite image...");
	}
	
	private static void processImages(String device, String view, String partPath, List<Set<String>> sets) {
		
		Set<List<String>> product = Sets.cartesianProduct(sets);
		for (List<String> productList : product)
		{
			List<String> images = populateImageList(partPath, productList);
			//Queue tasks here
			Queue queue = QueueFactory.getQueue("merge-write-queue");
			queue.add(TaskOptions.Builder.withUrl("/merge-write").param("device",device).param("view", view).param("partPath", partPath).param("images", parseImagePath(images)));
			//mergeAndWrite(device, view, partPath, images);
		}
	}

	private static String parseImagePath(List<String> images) {
	   
		StringBuilder sb = new StringBuilder();
		for(String imagePath : images)
		{
			if (sb.length() != 0) {
		        sb.append("$");
		    }
			sb.append(imagePath);
		}
	    return sb.toString();
    }

	private static void mergeAndWrite(String device, String view, String partPath, List<String> images) {
	    try
	    {
	    	
	    	String compositedImagePath = ImageUtils.getCompositedImagePath(images);
	    	Image newImage = ImageUtils.overlayImages(partPath, images);
	    	
	    	if (newImage != null)
	    	{
	    		String destDir = DEVICES + "_out" + File.separator + device + "_out" + File.separator + view
	    		        + "_out/full" + File.separator + compositedImagePath;
	    		GcsFilename outputFile = new GcsFilename(BUCKETNAME, destDir + "/" + "composited-image-00000.png");
	    		FileUtils.writeToFile(outputFile, newImage.getImageData());
	    		
	    	}
	    }
	    catch (Exception ex)
	    {
	    	logger.severe("Exception obtained. Cause: " + ex.getMessage());
	    }
    }
	
	private static List<String> populateImageList(String partPath, List<String> productList) {
		
		List<String> images = new ArrayList<String>();
		for (String productPartID : productList)
		{
			File imagePath = new File(partPath + File.separator + productPartID + File.separator);
			if (imagePath != null)
			{
				File[] imageFiles = imagePath.listFiles();
				if (imageFiles != null && imageFiles.length > 0)
				{
					images.add(imageFiles[0].getPath());
				}
			}
			
		}// end inner for
		
		return images;
	}
	
	private static Map<String, String[]> getOptionValues(String device, String view, String[] options) {
		
		Map<String, String[]> optVals = new HashMap<String, String[]>();
		
		for (String option : options)
		{
			String[] result = FileUtils.appendOptionToPath(
			    option,
			    FileUtils.getSubDirs(new File(ROOT_DIR + File.separator + device + File.separator + view + File.separator
			            + option)));
			optVals.put(option, result);
		}
		
		return optVals;
	}
	
}
