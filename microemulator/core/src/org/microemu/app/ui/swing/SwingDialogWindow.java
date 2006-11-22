/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.microemu.app.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Uniwersalna klasa sluzaca do wyswietlania okienek dialogowych
 */

public class SwingDialogWindow
{

  /**
   * Metoda wywolujaca modalne okienko dialogowe
   *
   * @param title tytul okienka
   * @param panel wnetrze okienka dialogowego
   * @return true jesli zamkniecie okna zostalo wywolane przez przycisk OK
   */
  public static boolean show(JFrame parent, String title, final SwingDialogPanel panel)
  {
    final JDialog dialog = new JDialog(parent, title, true);
    dialog.getContentPane().setLayout(new BorderLayout());
    dialog.getContentPane().add(panel, BorderLayout.CENTER);

    JPanel actionPanel = new JPanel();
    actionPanel.add(panel.btOk);
    actionPanel.add(panel.btCancel);
    final JButton extraButton = panel.getExtraButton();
    if (extraButton != null) {
    	actionPanel.add(extraButton);
    }
    dialog.getContentPane().add(actionPanel, BorderLayout.SOUTH);
    dialog.pack();
    
    Dimension frameSize = dialog.getSize();
    int x = parent.getLocation().x + ((parent.getWidth() - frameSize.width) / 2);
    if (x < 0) {
    	x = 0;
    }
    int y = parent.getLocation().y + ((parent.getHeight() - frameSize.height) / 2);
    if (y < 0) {
    	y = 0;
    }
    dialog.setLocation(x, y);

    ActionListener closeListener = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
			panel.extra = false;
			if (source == panel.btOk || source == extraButton) {
				if (panel.check(true)) {
					if (source == extraButton) {
						panel.extra = true;
					}
					panel.state = true;
					dialog.setVisible(false);
					panel.hideNotify();
				}
			} else {
				panel.state = false;
				dialog.setVisible(false);
				panel.hideNotify();
			}
		}
	};
    
    WindowAdapter windowAdapter = new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        panel.state = false;
        panel.hideNotify();
      }
    };

    dialog.addWindowListener(windowAdapter);
    panel.btOk.addActionListener(closeListener);
    panel.btCancel.addActionListener(closeListener);
    if (extraButton != null) {
    	extraButton.addActionListener(closeListener);
    }
    panel.showNotify();
    dialog.setVisible(true);
    panel.btOk.removeActionListener(closeListener);
    panel.btCancel.removeActionListener(closeListener);
    if (extraButton != null) {
    	extraButton.removeActionListener(closeListener);
    }

    return panel.state;
  }

}

