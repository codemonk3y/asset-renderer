package com.overfused.asset.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;

public class ImageUtils {
	
	private static final Logger logger = Logger.getLogger(ImageUtils.class.getName());
	
	private static final ImagesService imagesService = ImagesServiceFactory.getImagesService();
	
	public static byte[] imageToBytes(File file) throws FileNotFoundException {
		
		FileInputStream fis = new FileInputStream(file);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		
		try
		{
			for (int readNum; (readNum = fis.read(buf)) != -1;)
			{
				// Writes to this byte array output stream
				bos.write(buf, 0, readNum);
			}
		}
		catch (IOException ex)
		{
			logger.severe("Exception obtained while converting image to byes. Cause: " + ex.getMessage());
		}
		finally
		{
			try
			{
				fis.close();
			}
			catch (IOException e)
			{
				logger.severe("Error obtained while trying to close the file input stream. Cause: " + e.getMessage());
			}
		}
		return bos.toByteArray();
	}
	
	public static String getCompositedImagePath(List<String> images) {
		StringBuilder sb = new StringBuilder();
		String accentPart = "";
		
		for (String image : images)
		{
			String[] optionPath = image.split(File.separator);
			if (optionPath != null && optionPath.length > 0)
			{
				logger.info("Composing path: " + optionPath[optionPath.length - 2]);
				
				if("rear-color".equalsIgnoreCase(optionPath[optionPath.length - 3]))
				{
					accentPart = optionPath[optionPath.length - 2];
					continue;
				}
				logger.info("Appending.. " + optionPath[optionPath.length - 2]);
				sb.append(optionPath[optionPath.length - 2]);
			}
			
			/*
			 * Hack to get the dynamic image path to  display in the correct order
			 */
		}
		
		if (accentPart != null && accentPart != "")
		{
			sb.append(accentPart);
		}
		logger.info("Full composited path= " + sb.toString());
		return sb.toString();
	}
	
	public static Image overlayImages(String partPath, List<String> images) throws FileNotFoundException {
		
		List<Composite> listComposites = new ArrayList<Composite>();
		File backgroundFile = new File(partPath + File.separator + "consistent" + File.separator + "consistant.png");
		byte[] background = ImageUtils.imageToBytes(backgroundFile);
		Image bgImg = ImagesServiceFactory.makeImage(background);
		Composite cbg = ImagesServiceFactory.makeComposite(bgImg, 0, 0, 1, Composite.Anchor.TOP_LEFT);
		listComposites.add(cbg);
		
		for (String imagePath : images)
		{
			byte[] f1b = ImageUtils.imageToBytes(new File(imagePath));
			Image image1 = ImagesServiceFactory.makeImage(f1b);
			Composite aPaste = ImagesServiceFactory.makeComposite(image1, 0, 0, 1.0F, Composite.Anchor.TOP_LEFT);
			listComposites.add(aPaste);
		}
		
		Image newImage = imagesService.composite(listComposites, bgImg.getWidth(), bgImg.getHeight(), 0,
		    ImagesService.OutputEncoding.PNG);
		
		return newImage;
	}
}
