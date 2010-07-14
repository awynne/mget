package wynne.mget.core;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

public class M3uGetterCli implements M3uGetter, Runnable {
	
	private String baseFolder = null;
	private String m3uUrl = null;
	private boolean gettingTunes = false;
	private boolean stopRequested = false;
	private int progress = 0;

	private Logger log = Logger.getLogger(getClass());
	
	public M3uGetterCli(String url, String directory) {
		m3uUrl = url;
		baseFolder = directory;
	}

	@Override
	public String getBaseFolder() {
		return baseFolder;
	}

	@Override
	public String getM3uUrl() {
		return m3uUrl;
	}

	@Override
	public boolean isGettingTunes() {
		return gettingTunes;
	}

	@Override
	public boolean isStopRequested() {
		return stopRequested;
	}

	@Override
	public void requestStop() {
		stopRequested = true;
		log.info("Cancel requested");
	}

	@Override
	public void setBaseFolder(String value) {
		baseFolder = value;
	}

	@Override
	public void setGettingTunes(boolean value) {
		gettingTunes = value;
	}

	@Override
	public void updateProgress(int percent) {
		progress = percent;
	}
	
	public int getProgress() {
		return progress;
	}

	@Override
	public void run() {
		String destPath = Utils.extractDestFolder(m3uUrl);
		Runnable runnable = new DoGetTunes(destPath, this);
		runnable.run();
	}

	@Override
	public Appender getAppender() {
		return null;
	}
}
