/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@it.pl>
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
 
package com.barteo.emulator.device.applet;

import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.microedition.lcdui.TextField;
import com.barteo.emulator.MIDletBridge;
import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.InputMethod;
import com.barteo.emulator.device.InputMethodEvent;


public class AppletInputMethod extends InputMethod implements Runnable
{
	private int lastButtonCharIndex = -1;
  private AppletButton lastButton = null;
	private boolean resetKey;

	private Thread t;
	private boolean cancel = false;


	public AppletInputMethod()
	{
		t = new Thread(this, "InputMethodThread");
		t.start();
	}
  
  
	void dispose()
	{
		  cancel = true;
	}


  private boolean commonKeyPressed(int keyCode)
  {
		String tmp;

    if (inputMethodListener == null) {
			MIDletBridge.getMIDletAccess().getDisplayAccess().keyPressed(keyCode);
			return true;
		}

    if (keyCode == KeyEvent.VK_MODECHANGE) {
			if (getInputMode() == InputMethod.INPUT_123) {
				setInputMode(InputMethod.INPUT_ABC_UPPER);
			} else if (getInputMode() == InputMethod.INPUT_ABC_UPPER) {
				setInputMode(InputMethod.INPUT_ABC_LOWER);
			} else if (getInputMode() == InputMethod.INPUT_ABC_LOWER) {
				setInputMode(InputMethod.INPUT_123);
			}
			synchronized (this) {
				if (lastButton != null) {
					caret++;
					lastButton = null;
					lastButtonCharIndex = -1;
				}
			}
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, text);
			inputMethodListener.caretPositionChanged(event);
			return true;
		}

		if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT) {
			synchronized (this) {
				if (keyCode == KeyEvent.VK_LEFT && caret > 0) {
					caret--;
				}
				if (keyCode == KeyEvent.VK_RIGHT && caret < text.length()) {
					caret++;
				}
				lastButton = null;
				lastButtonCharIndex = -1;
			}
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, text);
			inputMethodListener.caretPositionChanged(event);
			return true;
		}

		if (keyCode == KeyEvent.VK_BACK_SPACE) {
			synchronized (this) {
				if (lastButton != null) {
					caret++;
					lastButton = null;
					lastButtonCharIndex = -1;
				}
				if (caret > 0) {
					caret--;
					tmp = "";
					if (caret > 0) {
						tmp += text.substring(0, caret);
					}
					if (caret < text.length() - 1) {
						tmp += text.substring(caret + 1);
					}
					text = tmp;
				}
			}
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, caret, text);
			inputMethodListener.inputMethodTextChanged(event);
			event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, text);
			inputMethodListener.caretPositionChanged(event);
			return true;
		}
    
    return false;
  }


  public void keyboardKeyPressed(KeyEvent ev)
  {
    if (commonKeyPressed(ev.getKeyCode())) {
      return; 
    }
    
    if (text.length() < maxSize && ev.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
      char[] test = new char[1];
      test[0] = ev.getKeyChar();
      test = filterConstraints(test);
      if (test.length > 0) {
  			synchronized (this) {
  				if (lastButton != null) {
  					caret++;
  					lastButton = null;
  					lastButtonCharIndex = -1;
  				}
  				String tmp = "";
  				if (caret > 0) {
  					tmp += text.substring(0, caret);
  				}
  				tmp += ev.getKeyChar();
					if (caret < text.length()) {
						tmp += text.substring(caret);
  				}
  				text = tmp;
    			caret++;
        }
  			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, caret, text);
  			inputMethodListener.inputMethodTextChanged(event);
  			event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, text);
  			inputMethodListener.caretPositionChanged(event);
      }
    }
  }


	public void keyPressed(int keyCode)
	{
		String tmp;
    
    if (commonKeyPressed(keyCode)) {
      return;
    }

		if (text.length() < maxSize) {
      for (Enumeration e = ((AppletDevice) DeviceFactory.getDevice()).getButtons().elements(); e.hasMoreElements(); ) {
        AppletButton button = (AppletButton) e.nextElement();
        if (keyCode == button.getKey()) {
          synchronized (this) {
						lastButtonCharIndex++;
            char[] buttonChars = filterConstraints(filterInputMode(button.getChars()));
            if (buttonChars.length > 0) {
              if (lastButtonCharIndex == buttonChars.length) {
  							if (buttonChars.length == 1) {
  								if (lastButton != null) {
  									caret++;
  								}
  								lastButton = null;
  							} else {
  								lastButtonCharIndex = 0;
  							}
  						}
  						if (lastButton != button) {
  							if (lastButton != null) {
  								caret++;
  							}
  							tmp = "";
  							if (caret > 0) {
  								tmp += text.substring(0, caret);
  							}
  							tmp += buttonChars[0];
  							if (caret < text.length()) {
  								tmp += text.substring(caret);
  							}
  							text = tmp;
     						lastButton = button;
  							lastButtonCharIndex = 0;
  						} else {
  							tmp = "";
  							if (caret > 0) {
  								tmp += text.substring(0, caret);
  							}
							tmp += buttonChars[lastButtonCharIndex];								
  							if (caret < text.length() - 1) {
  								tmp += text.substring(caret + 1);
  							}
  							text = tmp;
     						lastButton = button;
  						}
            } else {
  						lastButton = null;
              lastButtonCharIndex = -1;
            }
						resetKey = false;
						notify();
          }

					InputMethodEvent event = new InputMethodEvent(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, caret, text);
					inputMethodListener.inputMethodTextChanged(event);
					break;
				}
			}
		}
	}
  
  
  public void keyboardKeyReleased(KeyEvent ev)
  {
		MIDletBridge.getMIDletAccess().getDisplayAccess().keyReleased(ev.getKeyCode());
  }


	public void keyReleased(int keyCode)
	{
		MIDletBridge.getMIDletAccess().getDisplayAccess().keyReleased(keyCode);
  }


	public void run()
	{
		while (!cancel) {
			try {
				resetKey = true;
				synchronized (this) {
					wait(1500);
				}
			} catch (InterruptedException ex) {}
			synchronized (this) {
				if (resetKey && lastButton != null) {
					caret++;
					lastButton = null;
					lastButtonCharIndex = -1;
					if (inputMethodListener != null) {
						InputMethodEvent event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, text);
						inputMethodListener.caretPositionChanged(event);
					}
				}
			}
		}
	}
  
  
  private char[] filterConstraints(char[] chars)
  {
    char[] result = new char[chars.length];
    int i, j;

    for (i = 0, j = 0; i < chars.length; i++) {
      if (constraints == TextField.NUMERIC) {
        if (Character.isDigit(chars[i]) || chars[i] == '.') {
          result[j] = chars[i];
          j++;
        }
      } else {
        result[j] = chars[i];
        j++;
      }
    }
    if (i != j) {
      char[] newresult = new char[j];
      System.arraycopy(result, 0, newresult, 0, j);
      result = newresult;
    }

    return result;
  }
  
  
  private char[] filterInputMode(char[] chars)
  {
    int inputMode = getInputMode();
    char[] result = new char[chars.length];
    int i, j;
    
    for (i = 0, j = 0; i < chars.length; i++) {
      if (inputMode == InputMethod.INPUT_ABC_UPPER) {
        result[j] = Character.toUpperCase(chars[i]);
        j++;
      } else if (inputMode == InputMethod.INPUT_ABC_LOWER) {
        result[j] = Character.toLowerCase(chars[i]);
        j++;
      } else if (inputMode == InputMethod.INPUT_123) {
        if (Character.isDigit(chars[i])) {
          result[j] = chars[i];
          j++;
        }
      }
    }
    if (i != j) {
      char[] newresult = new char[j];
      System.arraycopy(result, 0, newresult, 0, j);
      result = newresult;
    }

    return result;
  }

}
