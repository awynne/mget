package wynne.mget.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import wynne.mget.core.M3uGetter;
import wynne.mget.gui.Listeners.BrowseListener;
import wynne.mget.gui.Listeners.ExitListener;
import wynne.mget.gui.Listeners.GetTunesListener;

public class M3uGetterPanel extends JPanel implements M3uGetter, Runnable {
	
	// constants
	private static final long serialVersionUID = -7100484471952897696L;

	// members
	
    private JButton getTunesButton = null;
    private JButton exitButton = null;
	
	private JTextField baseFolderTextField = null;
	
	private JTextField urlTextField = null;
	private JLabel progressLabel = null;
	
    private JButton baseFolderBrowseButton = null;
	
	private JProgressBar progressBar = null;
	
	protected Logger log = Logger.getLogger(this.getClass());
	
	private Appender appender = null;
	
	protected boolean gettingTunes = false;
	
	protected boolean stopRequested = false;
	
	public M3uGetterPanel() {
		super(new GridBagLayout());
		addComponents();
	}
	
    private void addComponents() {
        addUrl(0);
        addBaseFolder(1);
        addLog(2);
        addProgressBar(3);
    }
    
	private GridBagConstraints newGridBagConstraints()	{
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        return c;
	}
	
	private void addBaseFolder(int row) {
        JLabel baseFolderLabel = new JLabel("Download to: ");
        GridBagConstraints c = newGridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.insets = new Insets(0,10,0,0);  
        add(baseFolderLabel, c);
        
        baseFolderTextField = new JTextField( readBaseFolder() );
        baseFolderTextField.setEditable(false);
        c = newGridBagConstraints();
        c.gridx = 1;
        c.gridy = row;
        c.ipadx = 300;
        add(baseFolderTextField, c);
        
        baseFolderBrowseButton = new JButton("Browse");
        c = newGridBagConstraints();
        c.gridx = 2;
        c.gridy = row;
        c.anchor = GridBagConstraints.WEST;
        baseFolderBrowseButton.addActionListener(new BrowseListener(this));
        add(baseFolderBrowseButton, c);
	}
	
	private void addUrl(int row) {
        JLabel urlLabel = new JLabel("URL for m3u: ");
        GridBagConstraints c = newGridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.insets = new Insets(0,10,0,0);  
        add(urlLabel, c);
        
        urlTextField = new JTextField("http://");
        c = newGridBagConstraints();
        c.gridx = 1;
        c.gridy = row;
        c.gridwidth = 1;
        c.ipadx = 300;
        add(urlTextField, c);
        
        getTunesButton = new JButton("Get Tunes");
        c = newGridBagConstraints();
        c.gridx = 2;
        c.gridy = row;
        c.anchor = GridBagConstraints.WEST;
        getTunesButton.addActionListener(new GetTunesListener(this));
        add(getTunesButton, c);
	}
	
	private void addLog(int row) {
        JLabel logLabel = new JLabel("log: ");
        GridBagConstraints c = newGridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.insets = new Insets(0,10,0,0);  
        c.anchor = GridBagConstraints.NORTH;
        add(logLabel, c);
        
        JTextArea logTextArea = new JTextArea(5,30);
        c = newGridBagConstraints();
        TextAreaAppender appender = new TextAreaAppender(logTextArea);
        appender.setThreshold(Level.INFO);
        this.appender = appender;
        JScrollPane logScrollPane = new JScrollPane(logTextArea);
        logTextArea.setEditable(false);	
        c.gridx = 1;
        c.gridy = row;
        c.insets = new Insets(0,5,0,5);  
        c.gridwidth = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(logScrollPane, c);
	}
	
	private void addProgressBar(int row) {
        GridBagConstraints c = newGridBagConstraints();
        
        progressLabel = new JLabel("0%");
        c.gridx = 0;
        c.gridy = row;
        c.insets = new Insets(0,10,0,0);  
        add(progressLabel, c);
        
        c = newGridBagConstraints();
        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        c.gridx = 1;
        c.gridy = row;
        c.insets = new Insets(0,5,0,5);  
        add(progressBar, c);
        
        exitButton = new JButton("Exit");
        c = newGridBagConstraints();
        c.gridx = 2;
        c.gridy = row;
        exitButton.addActionListener(new ExitListener(this));
        add(exitButton, c);
	}

	public String getBaseFolder() {
		return baseFolderTextField.getText();
	}
	
	private String readBaseFolder() {
		BufferedReader reader;
		String folder = M3uGetter.BASEDIR_DEFAULT;
		try {
			File prefsFile = new File(M3uGetter.PREFS_FILE_NAME);
			if (prefsFile.canRead()) {
				reader = new BufferedReader( new FileReader(prefsFile));
				folder = reader.readLine();
			}
			else {
				folder = M3uGetter.BASEDIR_DEFAULT;
			}
		} 
		catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Problem reading base folder from prefs file", "Read error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return folder;
	}
	
	public void setBaseFolder(String baseFolder) {
		baseFolderTextField.setText(baseFolder);
		storeBaseFolder(baseFolder);
	}
	
	private void storeBaseFolder(String baseFolder) {
		File tempFile = new File(M3uGetter.PREFS_FILE_NAME);
		if (tempFile.exists()) {
			tempFile.delete();
		}
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter(M3uGetter.PREFS_FILE_NAME) );
			writer.write(baseFolder);
			writer.close();
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(this, "Problem storing base folder in prefs file", "Write error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	public Logger getLogger() {
		return log;
	}
	
	public void setGettingTunes(boolean getting) {
		gettingTunes = getting;
		if (gettingTunes) {
			getTunesButton.setEnabled(false);
			urlTextField.setEnabled(false);
			baseFolderTextField.setEnabled(false);
			baseFolderBrowseButton.setEnabled(false);
			exitButton.setText("Cancel");
		}
		else {
			stopRequested = false;
			getTunesButton.setEnabled(true);
			urlTextField.setEnabled(true);
			baseFolderTextField.setEnabled(true);
			baseFolderBrowseButton.setEnabled(true);
			exitButton.setEnabled(true);
			exitButton.setText("Exit");
		}
	}
	
	public boolean isGettingTunes() {
		return gettingTunes;
	}
	
	public String getM3uUrl() {
		return urlTextField.getText();
	}
	
	public JPanel getPanel() {
		return this;
	}
	
	public void updateProgress(int percent) {
		progressLabel.setText(percent + "%");
		progressBar.setValue(percent);
	}
	public int getProgress() {
		return progressBar.getValue();
	}
	
	public boolean isStopRequested() {
		return stopRequested;
	}
	
	public void requestStop() {
		log.info("Cancel requested...");
		exitButton.setEnabled(false);
		stopRequested = true;
	}

	@Override
	public void run() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("M3U Stream Getter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
	}

	@Override
	public Appender getAppender() {
		return appender;
	}

}