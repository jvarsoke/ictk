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

/* CommandInput ***********************************************************/
/**
 * This is a no frills Command input window.
 * @author  jvarsoke
 */
import java.awt.*;
import java.awt.event.*;
import ictk.boardgame.chess.net.ics.ICSProtocolHandler;

public class CommandInput extends Frame implements ActionListener {
   TextField tfCommand;
   ICSProtocolHandler ics;

   /** Creates new form JCommandInput */
    public CommandInput(String title, ICSProtocolHandler ics) {
        super(title);
	this.ics = ics;
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
       tfCommand = new TextField();
       tfCommand.addActionListener(this);
       
       addWindowListener(new java.awt.event.WindowAdapter() {
          public void windowClosing(java.awt.event.WindowEvent evt) {
             ics.disconnect();
	     System.exit(0);
          }
       });
       
       tfCommand.setFont(new Font("Lucida Sans Typewriter", 0, 12));
       tfCommand.addActionListener(this);
       
       add(tfCommand, BorderLayout.CENTER);
       
       pack();
       java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
       setSize(new java.awt.Dimension(500, 18));
       //setLocation((screenSize.width-500)/2,(screenSize.height-18)/2);
       setLocation(27,(screenSize.height-60));
    }

    public void actionPerformed (ActionEvent evt) {
       ics.sendCommand(tfCommand.getText());
       tfCommand.setText("");
    }
}
