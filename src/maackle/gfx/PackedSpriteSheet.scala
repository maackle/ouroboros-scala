package maackle.gfx

import collection.immutable.ListMap
import maackle.util._
import org.newdawn.slick.opengl.TextureLoader
import collection.mutable
import maackle.GLX._
import org.lwjgl.opengl.GL11._
import maackle.vec
import maackle.traits.Affine

@deprecated("Needs to be re-created in the image of SpriteSheet")
class PackedSpriteSheet(path:String, yflip:Boolean=true) {
   type paramlist = (Float, Float, vec, Float, Float, Float, Float)
   type tm = ListMap[String, paramlist]

   private val (tex, reflist, refmap) = {
      val reg = """(.*)/(.+?)\.(.+?)$""".r
      val reg(dir, file, ext) = path
      val lines = getFile(path).getLines()
      val imfile = lines.next()
      val filestr = lines.reduce(_+" "+_)
      val items = filestr.split( """\}\s*\{|\{|\}""" )
      val rxLine = """\s*?(\w+)\s*?(\d+)\s*?(\d+)\s*?(\d+)\s*?(\d+)\s*?(\d+)\s*?(\d+)\s*?(\d+)\s*?(\d+)\s*?""".r

      val tex = TextureLoader.getTexture("png", getStream(dir+"/"+imfile))
      val (tw, th) = (tex.getImageWidth.toFloat, tex.getImageHeight.toFloat)
      val offset = vec.zero //TODO: implement
      
      var texmap = Map[String, paramlist]()
      var idmap = Map[String, Int]()
      var reflist = mutable.ListBuffer[paramlist]()
      var refmap = Map[String, paramlist]()
      for( (item, index) <- items.view.zipWithIndex) {
         item match {
            case rxLine(id, xx, yy, ww, hh, d1, d2, d3, d4) => {
               val (x, y, w, h) = (xx toFloat, yy toFloat, ww toFloat, hh toFloat)
               val params:paramlist = {
                  if (yflip) (w, h, offset, (x+w)/tw, (y+h)/th, x/tw, (y)/th)
                  else       (w, h, offset, x/tw, y/th, (x+w)/tw, (y+h)/th)
               }
               texmap += id -> params
               idmap += id -> index
               reflist += params
               refmap += id -> params
            }
            case _ =>
         }
      }
      (tex, reflist toList, refmap toMap)
   }


   class ImageRef  (ss:PackedSpriteSheet, params:paramlist) extends Image {
      val (w, h, offset, tx1, ty1, tx2, ty2) = params
      def blit() {
         Color.white.bind()
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
      def draw() {
         // TODO ( easy )
      }
      def width = w
      def height = h
   }

   class SpriteRef (var position:vec, ss:PackedSpriteSheet, params:paramlist) extends ImageRef(ss:PackedSpriteSheet, params:paramlist) with Affine {
      var scale = 1f
      var angle = 0f
      override def width:Float = params._1
      override def height:Float = params._2
   }

   def getRef(id:String, pos:vec) = new SpriteRef(pos, this, refmap(id))
   def getRef(index:Int, pos:vec) = new SpriteRef(pos, this, reflist(index))
   def getImageRef(id:String, pos:vec) = new ImageRef(this, refmap(id))
   def getImageRef(index:Int, pos:vec) = new ImageRef(this, reflist(index))

   def start() {
      glEnable(GL_TEXTURE_2D)
//      glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE)
      tex.bind()
   }
   def end() {
      tex.release()
      glDisable(GL_TEXTURE_2D)
   }

   def draw(id:String) {
      val (vx, vy) = (1f, 10f)
      val (w, h, offset, x1, y1, x2, y2) = refmap(id)
//      glColor(Color(0xffffff))
      glBegin(GL_QUADS)
      glTexCoord2f(x1, y1)
      glVertex2f(0, 0)
      glTexCoord2f(x2, y1)
      glVertex2f(w, 0)
      glTexCoord2f(x2, y2)
      glVertex2f(w, h)
      glTexCoord2f(x1, y2)
      glVertex2f(0, h)
      glEnd()
   }
}
