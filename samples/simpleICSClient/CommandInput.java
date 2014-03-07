/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: CommandInput.java,v 1.1 2003/08/29 08:42:21 jvarsoke Exp $
 *
 *  This file is part of ICTK.
 *
 *  ICTK is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  ICTK is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with ICTK; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
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
