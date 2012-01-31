package maackle.gfx

import collection.immutable.ListMap
import maackle.util._
import collection.{mutable, breakOut}
import maackle.GLX._
import org.lwjgl.opengl.GL11._
import org.newdawn.slick.opengl.{Texture, TextureLoader}
import maackle.{help, GLX, vec}

/*
 * path : path of spritesheet image
 * (nw, nh) : number of tiles in each dimension
 * offset : center of each image
 */
class SpriteSheet(path:String, nw:Int, nh:Int, var offset:vec=null, yflip:Boolean=true) extends Textured {
   type refParamType = (Float, Float, vec, Float, Float, Float, Float)
   type tm = ListMap[String, refParamType]
   private class SpriteSheetException(msg:String) extends Exception(msg)

   protected val (tex:Texture, refList:Vector[ImageRef], sw, sh) = {
      val reg = """.*\.(.+?)$""".r
      val reg(ext) = path
      val tex = ext match {
         case "png" | "gif" | "jpg" | "jpeg" => TextureLoader.getTexture("png", getStream(path))
         case _ => throw new SpriteSheetException("image format '%s' is not recognized".format(ext))
      }
      val (iw, ih) = (tex.getImageWidth, tex.getImageHeight)
      val (tw, th) = (tex.getTextureWidth, tex.getTextureHeight)
      val (sw, sh) = (iw/nw, ih/nh)
      if(offset==null)
         offset = vec(sw/2, sh/2)
      if (iw % sw != 0 || ih % sh != 0) throw new SpriteSheetException("%dx%d sheet (%s) is not evenly divisible into %dx%d sprites".format(tw,th,path,sw,sh))
      var paramList = mutable.ListBuffer[refParamType]()

      val refList:Vector[ImageRef] = for(
           y <- Vector.range(0, ih, sh);
           x <- Vector.range(0, iw, sw);
           val w = sw.toFloat;
           val h = sh.toFloat;
           val params = {
               if (yflip) (w, h, offset, x.toFloat/tw, (y+h)/th, (x+w)/tw, y.toFloat/th)
               else       (w, h, offset, x.toFloat/tw, y.toFloat/th, (x+w)/tw, (y+h)/th)
           }
      ) yield {
         new ImageRef(this, params)
      }
      (tex, refList, sw, sh)
   }

   val (columns, rows) = (tex.getImageWidth / sw, tex.getImageHeight / sh)

   //NOTE: must be immutable since many copies may get passed around.
   // consider making case class
   class ImageRef  (ss:SpriteSheet, params:refParamType) extends Image {
      val (w, h, offset, tx1, ty1, tx2, ty2) = params
      def blit() {
         GLX.fill(true)
         Color.white.bind()
         bind {
            glBegin(GL_QUADS)
            glTexCoord2f(tx1, ty1)
            glVertex2f(0, 0)
            glTexCoord2f(tx2, ty1)
            glVertex2f(w, 0)
            glTexCoord2f(tx2, ty2)
            glVertex2f(w, h)
            glTexCoord2f(tx1, ty2)
            glVertex2f(0, h)
            glEnd()
         }
      }
      def draw() {
         help.pixelwise {
            translate(-offset)
            blit()
         }
      }
      def drawAABB(color:Color) {
         help.pixelwise {
            GLX.fill(false)
            color.bind()
            begin(GL_QUADS) {
               glVertex2f(0, 0)
               glVertex2f(w, 0)
               glVertex2f(w, h)
               glVertex2f(0, h)
            }
         }
      }

      def width = w
      def height = h
   }

   @inline def getRef(index:Int):ImageRef = refList(index)
   @inline def getRef(x:Int, y:Int):ImageRef = getRef(x + y*columns)
   @inline def images = refList
   def images(coords:Seq[(Int,Int)]):Vector[Image] = {
//      for(c <- coords.asInstanceOf[Vector[(Int,Int)]]) yield getImageRef(c._1, c._2)
      coords.map(tup=>getRef(tup._1, tup._2))(breakOut):Vector[Image]
   }

   @inline def apply(x:Int, y:Int):ImageRef = getRef(x + y*columns)
   def apply(coords:Seq[(Int,Int)]):Vector[Image] = images(coords)

}
