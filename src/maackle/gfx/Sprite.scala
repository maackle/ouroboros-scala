package maackle.gfx

import org.lwjgl.opengl.GL11._
import maackle.GLX._
import maackle.traits._
import org.newdawn.slick.opengl.Texture
import maackle.{GLX, Game, vec}

trait Image {
   def blit() // without offset
   def draw() // with offset
   def width:Float
   def height:Float
   def offset:vec
}
//
//
//trait Spritelike extends Image with Rendered {
//   def spritewise( fn: =>Unit ) {
//      glPushMatrix()
//      glScalef(Game.pxScale, Game.pxScale, 1)
//      translate(-offset)
//      fn
//      glPopMatrix()
//   }
//   def render() {
//      transformer { spritewise(blit _) }
//   }
//}
//
//trait Sprited extends Spritelike with Textured {
//   var tex:Texture = null
//   var offset:vec = null
//   var color:Color = Color(0xffffff)
//   def scale = Game.pxScale
//   def width = tex.getImageWidth
//   def height = tex.getImageHeight
//   private var (tx0, ty0, tx1, ty1) = (1f, 1f, 1f, 1f)
//   def setSprite(t:Texture, offset:vec = null) {
//      tex = t
//      if(offset==null) this.offset = vec(width/2, height/2)
//      else this.offset = offset
//      tx0 = 0
//      ty0 = height / tex.getTextureHeight
//      tx1 = width / tex.getTextureWidth
//      ty1 = 0
//   }
//   def setSprite(path:String) {
//      setSprite(Tex.load(path))
//   }
//   def blit() {
//      val inv = GLX.invertColor(false)
//      bind {
//         color.bind()
//         val (w, h) = (width, height)
//         glBegin(GL_QUADS)
//         glTexCoord2f(tx0, ty0)
//         glVertex2f(0, 0)
//         glTexCoord2f(tx1, ty0)
//         glVertex2f(w, 0)
//         glTexCoord2f(tx1, ty1)
//         glVertex2f(w, h)
//         glTexCoord2f(tx0, ty1)
//         glVertex2f(0, h)
//         glEnd()
//      }
//      GLX.invertColor(inv)
//   }
//}
//
//class SpriteFactory extends Sprited {
//
//   class SpriteRef {
//
//   }
//
//}

