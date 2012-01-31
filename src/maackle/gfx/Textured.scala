package maackle.gfx

import maackle.GLX._
import maackle.GLX.Color._
import org.lwjgl.opengl.GL11._
import org.newdawn.slick.opengl.{TextureLoader, Texture}
import maackle.util._
import maackle.help._
import maackle.{Game, Draw, GLX, vec}



object Tex {
   def load(path:String) = {
      val reg = """.*\.(.+?)$""".r
      val reg(ext) = path
      val tex = ext match {
         case "png" | "gif" | "jpg" | "jpeg" => { TextureLoader.getTexture(ext, getStream(path)) }
         case _ => throw new Exception("image format '%s' is not recognized".format(ext))
      }
      tex
   }

}

class Tex(val path:String, var offset:vec=null) extends Textured with Image {
   val tex = Tex.load(path)
   val (iw, ih) = (tex.getImageWidth toFloat, tex.getImageHeight toFloat)
   val (rx, ry) = (iw/tex.getTextureWidth, ih/tex.getTextureHeight )
   val dim = vec(width, height)
   var scale:Float=1f
   if(offset==null)
      offset = vec(iw/2, ih/2)
   def width = iw
   def height = ih

   def blit() {
      GLX.fill(true)
      Color.white.bind()
      bind {
         glBegin(GL_QUADS)
         glTexCoord2f(0, 0)
         glVertex2f(0, ih)
         glTexCoord2f(rx, 0)
         glVertex2f(iw, ih)
         glTexCoord2f(rx, ry)
         glVertex2f(iw, 0)
         glTexCoord2f(0, ry)
         glVertex2f(0, 0)
         glEnd()
      }
   }
   def draw() {
      pushmatrix {
         GLX.scale(px(1))
         translate((-offset))
         blit()
      }
   }
   def render(pos:vec=vec.zero, angle:Float=0f, scale:Float=1f, color:Color=Color.white) {
      GLX.fill(true)
      color.bind()
      pushmatrix {
         translate(pos)
         rotate(angle)
         GLX.scale(px(scale))
         translate((-offset))
         blit()
      }
   }
   override def toString = "Tex(\"%s\")(%dx%d)".format(path, iw, ih)
}

//TODO: among many other things, make isBound_? check a static reference for self-similarity to do away with unbind()
trait Textured {
   protected def tex:Texture
   def bound = Textured.currentTexture == this
   protected var dbg_layer = 0
   def bind(andThenDo: =>Unit) {
//      glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE)
      if(!bound) {
         tex.bind()
         Textured.currentTexture = this
      }
      require(dbg_layer==0, "nested bind blocks in Textured instance")
      dbg_layer += 1
      texture2(andThenDo)
      dbg_layer -= 1
   }
}
object Textured {
   var currentTexture:Textured = null
}
