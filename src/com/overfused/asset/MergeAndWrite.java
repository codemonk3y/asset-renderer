package com.overfused.asset;

import static com.motorola.asset.model.Constants.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.images.Image;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.motorola.asset.util.FileUtils;
import com.motorola.asset.util.ImageUtils;

public class MergeAndWrite extends HttpServlet {
	
	private static final Logger logger = Logger.getLogger(MergeAndWrite.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		try
		{
			
			String device = req.getParameter("device");
			String view = req.getParameter("view");
			String partPath = req.getParameter("partPath");
			String [] imagePaths = req.getParameter("images").split("\\$");
			
			List<String> images = new ArrayList<String>();
			if (imagePaths != null && imagePaths.length > 0)
			{
				for(String imagePath : imagePaths)
				{
					logger.info("Inside MergeAndWrite using IMagePath: " + imagePath);
					images.add(imagePath);
				}
			}
			
			String compositedImagePath = ImageUtils.getCompositedImagePath(images);
			Image newImage = ImageUtils.overlayImages(partPath, images);
			
			if (newImage != null)
			{
				String destDir = DEVICES + "_test_out" + File.separator + device + "_out" + File.separator + view
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
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
