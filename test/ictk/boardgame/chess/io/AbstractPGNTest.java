package ictk.boardgame.chess.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

/**
 * Parent class for the PGN tests.
 */
public abstract class AbstractPGNTest extends TestCase {

   protected String dataDir = "ictk/boardgame/chess/io/";

   public AbstractPGNTest(String name) {
      super(name);
   }

   protected static Reader getReaderFromResource(String file) {
      BufferedReader reader;
      try {
         reader = new BufferedReader(new InputStreamReader(PGNReaderTest.class.getClassLoader().getResourceAsStream(file)));
      } catch (RuntimeException e) {
         throw new RuntimeException("Failed to load + '" + file + "'.", e);
      }
      return reader;
   }
}
