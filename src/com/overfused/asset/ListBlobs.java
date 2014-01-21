package com.overfused.asset;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;

public class ListBlobs extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ImagesService imagesService = ImagesServiceFactory.getImagesService();
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		List<Composite> listComposites = new ArrayList<Composite>();
		//PrintWriter out = resp.getWriter();
		List<File> composites = new ArrayList<File>();
		displayIt(new File("WEB-INF/devices"), null, composites);
		
		for (File f : composites)
		{
			//out.println("Composite Name: " + f);
			byte[] f1b = imageToBytes(f);
			Image image1 = ImagesServiceFactory.makeImage(f1b);
			Composite aPaste = ImagesServiceFactory.makeComposite(image1, 0, 0, 1,
					Composite.Anchor.TOP_LEFT);
			listComposites.add(aPaste);
		}
		
//		out.println("size: " + listComposites.size());
		
		Image newImage = imagesService.composite(listComposites, 655, 740,
				0xFFFFFFFFL, ImagesService.OutputEncoding.PNG);
		
		resp.setContentType("image/png");
		
		resp.getOutputStream().write(newImage.getImageData());
		
	}
	
	private  void displayIt(File node, PrintWriter pw, List<File> composites) throws IOException{
		 
		if(node.isDirectory())
		{
			//pw.println("Directory = " + node.getName());
			String[] subNote = node.list();
			for(String filename : subNote)
			{
				displayIt(new File(node, filename), pw, composites);
			}
		}
		else
		{
			composites.add(node);
			//pw.println("File = " + node.getName());
		}
 
	}
	
	private byte[] imageToBytes(File file) throws FileNotFoundException {
	
		FileInputStream fis = new FileInputStream(file);
        //create FileInputStream which obtains input bytes from a file in a file system
        //FileInputStream is meant for reading streams of raw bytes such as image data. For reading streams of characters, consider using FileReader.
 
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                //Writes to this byte array output stream
                bos.write(buf, 0, readNum); 
            }
        } catch (IOException ex) {
            
        }
 
        return bos.toByteArray();
	
	}

}