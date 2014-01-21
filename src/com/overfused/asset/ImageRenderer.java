package com.overfused.asset;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

public class ImageRenderer extends HttpServlet{
	
	public static final String BUCKETNAME = "mot_assets";
	public static final String FILENAME = "consistant.png";
	public static final String FILENAME2 = "accents.png";
	public static final String FILENAME3 = "backplate.png";
	public static final String FILENAME4 = "front.png";
	public static final String FILENAME5 = "wallpaper.png";
	
	GcsService gcsService = GcsServiceFactory.createGcsService();
	ImagesService imagesService = ImagesServiceFactory.getImagesService();
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		GcsFilename filename = new GcsFilename(BUCKETNAME, FILENAME);
		GcsFilename filename2 = new GcsFilename(BUCKETNAME, FILENAME2);
		GcsFilename filename3 = new GcsFilename(BUCKETNAME, FILENAME3);
		GcsFilename filename4 = new GcsFilename(BUCKETNAME, FILENAME4);
		GcsFilename filename5 = new GcsFilename(BUCKETNAME, FILENAME5);

		/*
		 *   ArrayList<Composite> comps = new ArrayList<Composite>();
            comps.add(ImagesServiceFactory.makeComposite(getImageFromStaticFile("imgs/odontogram/dente_colore/dente_colore_11.png"), 0, 0, 1, Anchor.TOP_LEFT));
            comps.add(ImagesServiceFactory.makeComposite(getImageFromStaticFile("imgs/odontogram/dente_colore/dente_colore_12.png"), 10, 0, 1, Anchor.TOP_LEFT));
            long color = 0xFFFFFFFFL;
            Image resImg = ImagesServiceFactory.getImagesService().composite(comps, 429, 189, color);
            response.getOutputStream().write(resImg.getImageData());
		 */

		byte[] f1b = readFromFile(filename);
		byte[] f2b = readFromFile(filename2);
		byte[] f3b = readFromFile(filename3);
		byte[] f4b = readFromFile(filename4);
		byte[] f5b = readFromFile(filename5);

		List<Composite> listComposites = new ArrayList<Composite>();

		Image image1 = ImagesServiceFactory.makeImage(f1b);
		Image image2 = ImagesServiceFactory.makeImage(f2b);
		Image image3 = ImagesServiceFactory.makeImage(f3b);
		Image image4 = ImagesServiceFactory.makeImage(f4b);
		Image image5 = ImagesServiceFactory.makeImage(f5b);

		Composite aPaste = ImagesServiceFactory.makeComposite(image1, 0, 0, 1,
				Composite.Anchor.TOP_LEFT);
		listComposites.add(aPaste);

		Composite bPaste = ImagesServiceFactory.makeComposite(image2, 0, 0,
				1, Composite.Anchor.TOP_LEFT);
		listComposites.add(bPaste);
		
		Composite cPaste = ImagesServiceFactory.makeComposite(image3, 0, 0,
				1, Composite.Anchor.TOP_LEFT);
		listComposites.add(cPaste);
		
		Composite dPaste = ImagesServiceFactory.makeComposite(image4, 0, 0,
				1, Composite.Anchor.TOP_LEFT);
		listComposites.add(dPaste);
		
		Composite ePaste = ImagesServiceFactory.makeComposite(image5, 0, 0,
				1, Composite.Anchor.TOP_LEFT);
		listComposites.add(ePaste);

		//resp.getWriter().println("size: " + listComposites.size());
		
		Image newImage = imagesService.composite(listComposites, 655, 740,
				0xFFFFFFFFL, ImagesService.OutputEncoding.PNG);
		
		resp.setContentType("image/png");
		
		resp.getOutputStream().write(newImage.getImageData());
		
		//resp.getWriter().println("Finished merging composite image...");
		
		//resp.getWriter().println("Image:");
		
		
	//	GcsFilename outputFile = new GcsFilename(BUCKETNAME, "merged.png");
	//	writeToFile(outputFile, newImage.getImageData());
	//	resp.getWriter().println("Finished merging composite image...");
		
//	    GcsFileOptions options = new GcsFileOptions.Builder().mimeType("image/png").build();
//	    GcsOutputChannel writeChannel = gcsService.createOrReplace(filename2, options);

		

	}

	/*
	 * @Override public void doGet(HttpServletRequest req, HttpServletResponse
	 * resp) throws IOException { resp.setContentType("text/plain");
	 * resp.getWriter().println("Hello, world from java"); GcsService gcsService
	 * = GcsServiceFactory.createGcsService(); GcsFilename filename = new
	 * GcsFilename(BUCKETNAME, FILENAME); GcsFileOptions options = new
	 * GcsFileOptions.Builder() .mimeType("text/html").acl("public-read")
	 * .addUserMetadata("myfield1", "my field value").build(); GcsOutputChannel
	 * writeChannel = gcsService.createOrReplace(filename, options);
	 * 
	 * // You can write to the channel using the standard Java methods. // Here
	 * we use a PrintWriter: PrintWriter out = new
	 * PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
	 * out.println("The woods are lovely dark and deep.");
	 * out.println("But I have promises to keep."); out.flush();
	 * 
	 * // Note that the writeChannel is Serializable, so it is possible to //
	 * store it somewhere and write // more to the file in a separate request.
	 * To make the object as small // as possible call:
	 * writeChannel.waitForOutstandingWrites();
	 * 
	 * // This time we write to the channel directly
	 * writeChannel.write(ByteBuffer.wrap("And miles to go before I sleep."
	 * .getBytes()));
	 * 
	 * // Now finalize writeChannel.close();
	 * resp.getWriter().println("Done writing...");
	 * 
	 * // At this point, the file is visible to anybody on the Internet through
	 * // Cloud Storage as: //
	 * (http://storage.googleapis.com/BUCKETNAME/FILENAME)
	 * 
	 * // We can now read the file through the API: GcsInputChannel readChannel
	 * = gcsService.openReadChannel(filename, 0); // Again, different standard
	 * Java ways of reading from the channel. BufferedReader reader = new
	 * BufferedReader(Channels.newReader( readChannel, "UTF8")); String line; //
	 * Prints "The woods are lovely, dark, and deep." //
	 * "But I have promises to keep." // "And miles to go before I sleep." while
	 * ((line = reader.readLine()) != null) { resp.getWriter().println("READ:" +
	 * line); } readChannel.close(); }
	 */

	private byte[] readFromFile(GcsFilename fileName) throws IOException {
		int fileSize = (int) gcsService.getMetadata(fileName).getLength();
		ByteBuffer result = ByteBuffer.allocate(fileSize);
		GcsInputChannel readChannel = gcsService.openReadChannel(fileName, 0);
		try {
			readChannel.read(result);
		} finally {
			readChannel.close();
		}
		return result.array();
	}

	private void writeToFile(GcsFilename fileName, byte[] content)
			throws IOException {
	
		GcsOutputChannel outputChannel = gcsService.createOrReplace(fileName,
				 new GcsFileOptions.Builder()
        .mimeType("image/png").build());
		outputChannel.write(ByteBuffer.wrap(content));
		
		outputChannel.close();
	}

}
