Program:  M3U Getter (mget)

Prerequisites:  Java Runtime Environment 1.6

Description:  

  An M3U "stream" is simply a file containing a list of URLs pointing to 
  MP3 files.  Given a URL pointing to an M3U, this program will download 
  all the MP3s contained in the M3U file.

Installation:

  * Unzip mif-vers-all.zip, this will create the directory mif-vers/
  
GUI Usage:   
	
  1. Start the program by double-clicking the jar:  mif-vers-all.jar
  2. In a browser, locate a link pointing to an M3U.
  3. Right-click the link and choose the option: "save link as" (or 
     the corresponding option for your browser)
  4. Paste this link into the GUI text field labelled "URL for M3U"   
  5. Click the "Browse" button to choose a directory to download the 
     files
  6. Click "Get Tunes" to start the download   
  
Command line usage:	

  * The include mget bash script or the java -jar command can be 
    used. The help command will give the available options, eg:
    
    -> mget --help
    running command: java -jar /Users/wynne/local/mget/mget-0.4/mget-*-all.jar --help
    The options available are:
            --dir -d value : Directory where the tunes will be downloaded
            [--help -h] : display help
            --url -u value : URL of the m3u stream

  
      