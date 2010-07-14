package wynne.mget.gui;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.log4j.Level;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

public class TextAreaAppender extends WriterAppender {
	
	static private JTextArea jTextArea = null;
	
	public TextAreaAppender(JTextArea jTextArea) {
		TextAreaAppender.jTextArea = jTextArea;
	}
	
	/**
	 * Format and then append the loggingEvent to the stored
	 * JTextArea.
	 */
	public void append(LoggingEvent loggingEvent) {
		SwingUtilities.invokeLater(new AppendRunnable(loggingEvent));
	}
	
	private static class AppendRunnable implements Runnable {
		private LoggingEvent event = null;
		
		static final String NEWLINE = System.getProperty("line.separator");
		
		public AppendRunnable(LoggingEvent event) {
			this.event = event;
		}
		
		public void run() {
			String msg = event.getMessage().toString();
			jTextArea.append(msg + NEWLINE);
			Level level = event.getLevel();
			if (level.equals(Level.ERROR)) {
				JOptionPane.showMessageDialog(jTextArea.getParent(), msg, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
