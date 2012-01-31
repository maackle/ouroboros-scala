package ouroboros;

import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.Effect;

/*
For when Scala doesn't quite cut it...
(This only happens when people write ridiculous Java code in their libraries)
 */

public class Java {

    static public UnicodeFont uni;

    static public void addEffect(UnicodeFont uni, Effect fx) {
        uni.getEffects().add(fx);
    }
}
