
package Transmitable.toServer;

import java.io.Serializable;

public class ActionKey implements Serializable {
    
    public int keyCode;
    public boolean  pressed;
    
    public ActionKey(int keyCode, boolean state) {
        this.keyCode = keyCode;
        this.pressed = state;
    }    
}
