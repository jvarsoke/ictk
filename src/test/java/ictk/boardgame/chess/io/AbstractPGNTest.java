package ictk.boardgame.chess.io;

import java.net.URISyntaxException;
import java.net.URL;
import java.io.*;
import junit.framework.TestCase;

/**
 * Parent class for the PGN tests.
 */
public abstract class AbstractPGNTest extends TestCase {


   public AbstractPGNTest(String name) {
      super(name);
   }

   /*
   protected String dataDir = "ictk/boardgame/chess/io/";
   protected static Reader getReaderFromResource(String file) {
      BufferedReader reader;
      try {
         reader = new BufferedReader(new InputStreamReader(PGNReaderTest.class.getClassLoader().getResourceAsStream(file)));
      } catch (RuntimeException e) {
         throw new RuntimeException("Failed to load + '" + file + "'.", e);
      }
      return reader;
   }
   */


   //UTILITY//////////////////////////////////////////////////////////////////
   /** load the local test resource file by leveraging the classpath.
    *  This should grab the file from where the tests are being run.
    */
   public File getTestFile (String filename) throws URISyntaxException {
      File file = null;

      if (file == null || !file.exists()) {
	 URL r = this.getClass().getResource(filename);
         assertNotNull("Couldn't find '" + filename + "' on classpath", r);
	 file = new File(r.toURI());
      }

      assertNotNull("Null resource file: '" + filename + "'", file);
      assertTrue("Couldn't read resource: '" + filename + "'", file.exists());

      return file;
   }
}
