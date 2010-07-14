package wynne.mget.core;

import java.io.File;

import org.apache.log4j.Appender;

public interface M3uGetter {
	
	public static final String BASEDIR_DEFAULT =  System.getProperty("user.home");
	public static final String PREFS_FILE_NAME = System.getProperty("java.io.tmpdir") + File.separator + "mget.data";
	
	public static final String FILE_SEP = File.separator;
	public static final String LINE_SEP = System.getProperty("line.separator");
	
	public String getBaseFolder();
	public void setBaseFolder(String value);
	public Appender getAppender();
	public void updateProgress(int percent);
	public int getProgress();
	
	public void setGettingTunes(boolean value);
	public boolean isGettingTunes();
	
	public String getM3uUrl();
	
	public boolean isStopRequested();
	public void requestStop();

}
