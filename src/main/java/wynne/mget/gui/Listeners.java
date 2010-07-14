package wynne.mget.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.validator.UrlValidator;
import org.apache.log4j.Logger;

import wynne.mget.core.DoGetTunes;
import wynne.mget.core.M3uGetter;
import wynne.mget.core.Utils;

public class Listeners {
	
	public static abstract class AbstractMainPanelListener implements ActionListener {
		
		protected Logger log = Logger.getLogger(this.getClass());
		
		protected M3uGetter getter = null;
		
		protected JPanel panel = null;
		
		public AbstractMainPanelListener(M3uGetter getter) {
			if ( !(getter instanceof JPanel) ) {
				throw new RuntimeException("GUI required but M3uGetter is not a JPanel");
			}
			this.panel = (JPanel) getter;
			this.getter = getter;
		}
	}

	public static class BrowseListener extends AbstractMainPanelListener {
		
		public BrowseListener(M3uGetter mainPanel) {
			super(mainPanel);
		}

		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser(); 
			chooser.setCurrentDirectory(new File(getter.getBaseFolder()));
			chooser.setDialogTitle("Choose a directory");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setFileHidingEnabled(true);
			chooser.setAcceptAllFileFilterUsed(true);
			
			int option = chooser.showOpenDialog(panel);
			if (option == JFileChooser.APPROVE_OPTION) { 
				getter.setBaseFolder(chooser.getSelectedFile().toString());
			}
			else if (option == JFileChooser.CANCEL_OPTION) { 
				if ( ! (new File(getter.getBaseFolder()).exists()) ) {
					getter.setBaseFolder(chooser.getCurrentDirectory().toString());
				}
			}
		}
	}
	

	public static class ExitListener extends AbstractMainPanelListener {
		
		public ExitListener(M3uGetter mainPanel) {
			super(mainPanel);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (getter.isGettingTunes()) {
				getter.requestStop();
			}
			else {
				System.exit(0);
			}
		}
	}
	
	public static class GetTunesListener extends AbstractMainPanelListener {
		
		public GetTunesListener(M3uGetter mainPanel) {
			super(mainPanel);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			UrlValidator validator = new UrlValidator();
			String urlStr = getter.getM3uUrl().trim();
			String baseFolder = getter.getBaseFolder();
			
			if (!validator.isValid(urlStr)) {
				JOptionPane.showMessageDialog(panel, "Invalid URL: " + urlStr, "Invalid URL", JOptionPane.ERROR_MESSAGE);
			}
			else if ( !(new File(baseFolder).canWrite()) ) {
				JOptionPane.showMessageDialog(panel, "Can't write to folder: " + baseFolder, "Permission denied", JOptionPane.ERROR_MESSAGE);
			}
			// TODO: check for valid m3u file
			/*
			else if (!Utils.isValidM3uFile(urlStr)) {
				JOptionPane.showMessageDialog(panel, "Not a M3U file: " + urlStr, "Invalid URL", JOptionPane.ERROR_MESSAGE);
			}
			*/
			// TODO: in this case, we should choose a folder automatically.  eg, tunes-1 or tunes-2, etc
			else {
				String destPath = Utils.extractDestFolder(urlStr);
				if (destPath == null) {
					destPath = JOptionPane.showInputDialog(panel, "Could not determine show from url.\nPlease specify destination folder", "tunes");
				}
				
				if (destPath != null && !destPath.equals("")) {
					Thread getterThread = new Thread(new DoGetTunes(destPath, getter));
					getterThread.start();
				}
			}
		}
	}

}
