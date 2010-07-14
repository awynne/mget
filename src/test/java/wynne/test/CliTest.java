package wynne.test;

import wynne.mget.Main;

public class CliTest {
	
	public static void main(String args[]) {
		//Main.main(new String[]{"-u", "http://www.archive.org/download/gd1979-11-23.fob.nak700.holwein.motb-0119.107405.flac16/gd1979-11-23.fob.nak700.holwein.motb-0119.107405.flac16_vbr.m3u", "-d", "/tmp"});
		Main.main(new String[]{"-u", 
				"http://www.archive.org/download/gd1979-11-23.fob.nak700.holwein.motb-0119.107405.flac16/" +
				"gd1979-11-23.fob.nak700.holwein.motb-0119.107405.flac16_vbr.m3u"});
	}

}
