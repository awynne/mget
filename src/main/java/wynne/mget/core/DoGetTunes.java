package wynne.mget.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class DoGetTunes implements Runnable {
	
	public static final int BUFFER_SIZE = 1024 * 4;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	private String destPath = null;

	private M3uGetter msGetter = null;
	
	private int totalFiles = 0;

	public DoGetTunes(String destPath, M3uGetter msGetter) {
		this.destPath = destPath;
		this.msGetter = msGetter;
		if (msGetter.getAppender() != null) {
			log.addAppender(msGetter.getAppender());
		}
	}

	@Override
	public void run() {
		log.info("---  Getting Tunes  ---");
		msGetter.setGettingTunes(true);
		try {
			String fullPath = msGetter.getBaseFolder() + M3uGetter.FILE_SEP + destPath;
			String m3u = wgetM3u(fullPath);
			getTunes(m3u, fullPath);
			msGetter.setGettingTunes(false);
		} 
		catch (Throwable e) {
			String err = e.getMessage();
			log.error("Problem getting tunes. " + err);
			e.printStackTrace();
			msGetter.updateProgress(0);
		}
		finally {
			msGetter.setGettingTunes(false);
		}
	}
	
	private String wgetM3u(String destPath) throws IOException {
		File destFile = new File(destPath);
		if (destFile.exists()) {
			throw new RuntimeException("Directory already exists: " + destPath);
		}
		if ( !(destFile.mkdir()) ) {
			throw new RuntimeException("Could not create directory: " + destPath);
		}
		
		String m3uDestFile = destPath + M3uGetter.FILE_SEP + "list.m3u";
		String urlStr = msGetter.getM3uUrl();
		
		log.debug("getting: " + urlStr);
		Utils.getFile(urlStr, m3uDestFile);
		log.debug("got m3u: " + m3uDestFile);
		
		return m3uDestFile;
	}
	
	private void getTunes(String m3uFile, String tunesDir) throws IOException {
		BufferedReader reader = new BufferedReader( new FileReader(m3uFile) );
		String line = null;
		ArrayList<String> urls = new ArrayList<String>();
		while ((line = reader.readLine()) != null) {
			urls.add(line);
		}
		totalFiles = urls.size();
		
		int count = 0;
		for (String url : urls) {
			if (msGetter.isStopRequested()) {
				msGetter.setGettingTunes(false);
				break;
			}
			String file = Utils.extractMp3Name(url);
			String filePath = tunesDir + M3uGetter.FILE_SEP + file;
			log.info("getting file (" + ++count + " of " + urls.size() + "): " + file);
			int percent = (int) (((double) count / (double) totalFiles) * 100);
			if (percent >= 100) { percent = 99; }
			msGetter.updateProgress(percent);
			try {
				getMp3(url, filePath);
			}
			catch (MalformedURLException e) {
				String err = "Found a non-URL: " + url + ".  Aborting download";
				throw new RuntimeException(err, e);
			}
		}
		msGetter.updateProgress(100);
		log.info("Download complete, got (" + count + " of " + urls.size() + ")");
		msGetter.updateProgress(0);
	}
	
	public String getMp3(String urlString, String destFile) throws IOException {
		FileOutputStream outStream = new FileOutputStream(destFile);
		
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		conn.connect();
		int totalSize = conn.getContentLength();
		int readSize = 0;
		String type = conn.getContentType();
		
		InputStream inStream = conn.getInputStream();
		int step = (100 / (int) ((1.0/(double)totalFiles)*100) );
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytes;
		int lastPercent = 0;
		
		while ((bytes = inStream.read(buffer)) > 0) {
			outStream.write(buffer, 0, bytes);
			readSize += bytes;
			int percent = (int)( ((double)readSize / (double)totalSize) * 100 );
			if (percent - lastPercent > 0 && ((percent % step) == 0)) {
				int totalPercent = msGetter.getProgress();
				if (++totalPercent >= 100) {
					totalPercent = 99;
				}
				msGetter.updateProgress(totalPercent);
			}
			lastPercent = percent;
		}
		inStream.close();
		return type;
	}
}
