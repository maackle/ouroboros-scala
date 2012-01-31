package maackle

import java.awt
import awt.Font
import maackle.util._
import maackle.GLX._
import maackle.GLX.Color
import org.newdawn.slick
import org.newdawn.slick.{UnicodeFont, TrueTypeFont}
import org.newdawn.slick.font.effects.ColorEffect
import ouroboros.Java
import org.lwjgl.opengl.GL11._


object FontStyle extends Enumeration {
   val Plain, Bold, Italic = Value
   val BoldItalic = Value(4)
}

case class TTF(font:Font, size:Int, style:FontStyle.Plain.type) {

//   protected val ttf = new TrueTypeFont(font.deriveFont(FontStyle.Plain.id, size), false)

   // anchor: (-1, -1) == upper
   var anchor:vec = vec(0f, 0f)
   protected val uni = new UnicodeFont(font, size, false, false)
   uni.addNeheGlyphs()
//   uni.addGlyphs(0x21ba, 0x21bb)
   Java.addEffect(uni, (new ColorEffect(java.awt.Color.WHITE)))
   uni.loadGlyphs()

   //TODO: offsets
   def drawString(what:String, pos:vec, color:Color, anchor:vec=anchor) {
      Draw.fill(true)
      val w = uni.getWidth(what)
      val h = uni.getHeight(what)
      val x = pos.x - (anchor.x/2 + .5f) * w
      val y = pos.y - (anchor.y/2 + .5f) * h
      val offset = vec(
         (anchor.x/2 + .5f) * -w,
         (anchor.y/2 + .5f) * h
      )
      pushmatrix {
         translate(pos)
         translate(offset)
         scale(1,-1)
         uni.drawString(0, 0, what, color.toSlick )
      }
      glDisable(GL_TEXTURE_2D)
   }

}

object TTF {
   def apply(path:String, size:Int, style:FontStyle.Plain.type=FontStyle.Plain):TTF = {
      var font:Font = null
      try {
         font = Font.createFont(Font.TRUETYPE_FONT, getStream(path))
      }
      catch {
         case e:java.lang.RuntimeException =>
            font = new Font(path, Font.PLAIN, size)
      }
      new TTF(font, size, style)
   }
}