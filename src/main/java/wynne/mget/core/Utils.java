package wynne.mget.core;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	
	public static final int BUFFER_SIZE = 1024 * 4;

	public static String getFile(String urlString, String destFile) throws IOException {
		FileOutputStream outStream = new FileOutputStream(destFile);
		return doGetFile(urlString, outStream);
	}
	
	public static byte[] getFile(String urlString) throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		doGetFile(urlString, outStream);
		return outStream.toByteArray();
	}
	
	private static String doGetFile(String urlString, OutputStream outStream) throws IOException {
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		conn.connect();
		String type = conn.getContentType();
		
		InputStream inStream = conn.getInputStream();

		byte[] buffer = new byte[BUFFER_SIZE];
		int bytes;
		while ((bytes = inStream.read(buffer)) > 0) {
			outStream.write(buffer, 0, bytes);
		}
		inStream.close();
		return type;
	}
	
	public static String extractMp3Name(String url) {
		String patternStr = "/([-\\w\\d\\._]+.mp3)";
		return extract(url, patternStr);
	}
	
	public static String extractDestFolder(String url) {
		String patternStr = "/([-\\w\\d\\._]+).m3u";
		return extract(url, patternStr);
	}
	
	private static String extract(String str, String patternStr) {
		String result = null;
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(str);
		
		if (matcher.find()) {
			result = matcher.group(1);
		}
		return result;
	}
}
