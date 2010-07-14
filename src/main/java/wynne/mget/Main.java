package wynne.mget;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.CliFactory;
import uk.co.flamingpenguin.jewel.cli.Option;
import wynne.mget.core.M3uGetterCli;
import wynne.mget.gui.M3uGetterPanel;

public class Main {
	
	private static Logger log = Logger.getLogger(Main.class);
	
    public static void main(String[] args) {
    	if (args.length == 0) {
    		SwingUtilities.invokeLater(new M3uGetterPanel());
    	}
    	else {
    		try {
    			MainOpts opts = CliFactory.parseArguments(MainOpts.class, args);
				String url = opts.getUrl();
				String dir = opts.getDir();
				log.info(Main.class.getSimpleName() + " --url " + url + " --dir " + dir);
				new Thread( new M3uGetterCli(url, dir) ).start();
			} 
    		catch (ArgumentValidationException e) {
    			System.out.println(e.getMessage());
    		}
    	}
    }
    
    private interface MainOpts {
    	@Option (description = "Directory where the tunes will be downloaded", shortName="d", defaultValue=".")
    	String getDir();
    	
    	@Option (description = "URL of the m3u stream", shortName="u")
    	String getUrl();
    	
    	@Option(helpRequest = true, description = "display help", shortName = "h")
    	boolean getHelp();
    }
}
