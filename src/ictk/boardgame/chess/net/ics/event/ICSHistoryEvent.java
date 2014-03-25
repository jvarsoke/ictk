/*
 * ictk - Internet Chess ToolKit
 * More information is available at http://jvarsoke.github.io/ictk
 * Copyright (c) 1997-2014 J. Varsoke <ictk.jvarsoke [at] neverbox.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ictk.boardgame.chess.net.ics.event;
import ictk.boardgame.chess.net.ics.*;

import java.io.IOException;

/* ICSHistoryEvent **********************************************************/
/** A history of games played by a particular user.  This is the result
 *  of the "history" command.
 */
public class ICSHistoryEvent extends ICSEvent {
   //static///////////////////////////////////////////////////////////////
   protected static final int HISTORY_EVENT = ICSEvent.HISTORY_EVENT;


   //instance/////////////////////////////////////////////////////////////
   ICSGameInfo[] list;
   String player;
   
   //constructors/////////////////////////////////////////////////////////
   public ICSHistoryEvent (ICSProtocolHandler server) {
      super(server, HISTORY_EVENT);
   }

   public String getPlayer () { return player; }
   public void setPlayer (String name) { player = name; }

   public ICSGameInfo[] getHistoryList () { return list; }
   public void setHistoryList (ICSGameInfo[] l) { list = l; }

   /** doesn't do much yet.
    */
   public String getReadable () {
      return "History List";
   }
}
