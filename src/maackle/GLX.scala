package maackle

import org.newdawn.slick
import org.lwjgl.opengl.GL11._
import org.jbox2d.common.Color3f
import scala.collection.mutable
import math._
import util._
object GLX {
//   implicit def Color_Color3f(col:Color):Color3f = new Color3f(col.r, col.g, col.b)
//   implicit def Color3f_Color(col:Color3f):Color = new Color(col.x, col.y, col.z)

   def pushmatrix(fn: =>Unit) {
      glPushMatrix()
      fn
      glPopMatrix()
   }

   def begin(what:Int)(fn: =>Unit) {
      def cancel() { glEnd() }
      glBegin(what)
      fn
      glEnd()
   }

   def enable(what:Int)(fn: =>Unit) {
      def cancel() { glEnd() }
      glEnable(what)
      fn
      glDisable(what)
   }

   def texture2 = enable(GL_TEXTURE_2D) _

   @inline
   def fill(yes:Boolean) {
      if (yes) glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)
      else glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
   }

   @inline
   def clear(color:Color) {
      glClearColor(color.r, color.g, color.b, 1)
      glClear(GL_COLOR_BUFFER_BIT)
   }

   @inline def lineWidth(w:Float) { glLineWidth(w) }

   object Color {
      var store = mutable.Map[Int,Color]()
      def tuple2hex(r:Float, g:Float, b:Float, a:Float=1.0f):Int = {
         ( (a*255).toInt << 6 ) +
         ( (r*255).toInt << 4 ) +
         ( (g*255).toInt << 2 ) +
         ( (b*255).toInt )
      }
//      def apply(r:Int, g:Int, b:Int) = {
//         store.getOrElseUpdate(tuple2hex(r,g,b), new Color(r.toFloat/255f, g.toFloat/255f, b.toFloat/255f, 1.0f))
//      }
      def apply(r:Float, g:Float, b:Float) = {
         store.getOrElseUpdate(tuple2hex(r,g,b), new Color(r,g,b))
      }
      def apply(h:Int) = {
         new Color(((h&0xff0000)>>16)/255f, ((h&0xff00)>>8)/255f, ((h&0xff))/255f, 1.0f)
      }
      def lerp(c1:Color, c2:Color, amt:Float):Color = {
         val k = clamp(amt, 0, 1)
         c1*(1-k) + c2*k
      }
      def alerp(c1:Color, c2:Color, amt:Float):Color = {
         val k = clamp(amt, 0, 1)
         c1*(1-k) + c2*k
      }
      private val h = 0.5f
      val white = Color(1,1,1)
      val gray = Color(0.5f,0.5f,0.5f)
      val black = Color(0,0,0)

      val cyan =     Color(0,1,1)
      val magenta =  Color(1,0,1)
      val yellow =   Color(1,1,0)
      val red =      Color(1,0,0)
      val green =    Color(0,1,0)
      val blue =     Color(0,0,1)

      val purple =   Color(h, 0, h)
      val orange =   Color(1, h, 0)
      
   }
   case class Color (r:Float, g:Float, b:Float, a:Float=1.0f) {

      @inline
      def bind() { glColor4f(this.r, this.g, this.b, this.a) }
      @inline
      def apply() { bind() }

      //NOTE: what to do about alpha values?
      def +(o:Color) = Color(r+o.r, g+o.g, b+o.b, (a+o.a)/2)
      def -(o:Color) = Color(r-o.r, g-o.g, b-o.b, (a+o.a)/2)
      def *(v:Float) = Color(clamp(r*v, 0, 1), clamp(g*v, 0, 1), clamp(b*v, 0, 1), a)
      def **(v:Float) = Color(1-r/v, 1-g/v, 1-b/v, a)
      def /(v:Float) = Color(r/v, g/v, b/v, a)
      override def toString = "[Color (r=%f, g=%f, b=%f, a=%f)]".format(r,g,b,a)
      def toAWT = new java.awt.Color(r,g,b,a)
      def toSlick = new slick.Color(r,g,b,a)
   }
   @deprecated("Use Color.bind()")
   def glColor(color:Color) {
      glColor4f(color.r, color.g, color.b, color.a)
   }

   def glColorB2(color:Color3f) {
      glColor3f(color.x, color.y, color.z)
   }
   def glColor[T:Numeric] (t:(T,T,T)) {
      val n = implicitly[Numeric[T]]
      glColor3f(n toFloat t._1, n toFloat t._2, n toFloat t._3)
   }
   def glColor[T:Numeric] (t:(T,T,T,T)) {
      val n = implicitly[Numeric[T]]
      glColor4f(n toFloat t._1, n toFloat t._2, n toFloat t._3, n toFloat t._4)
   }
   @inline def translate(v:vec) { glTranslatef(v.x, v.y, 0) }
   @inline def translate(x:Float, y:Float) { glTranslatef(x, y, 0) }
   @inline def scale(s:Float) { glScalef(s,s,1) }
   @inline def scale(sx:Float, sy:Float) { glScalef(sx,sy,1) }
   @inline def rotate(t:Float) { glRotatef(t.toDegrees, 0,0,-1) }
   @inline def vertex(v:vec) { glVertex2f(v.x, v.y)}
   @inline def vertex(x:Float, y:Float) { glVertex2f(x, y)}

   @inline
   @deprecated("use vertex()")
   def glVertex(v:vec) { glVertex2f(v.x, v.y) }

   private var colorsInverted = false
   def invertColor(on:Boolean):Boolean = {
      return false
      if(on && !colorsInverted) {
         glEnable(GL_COLOR_LOGIC_OP)
         glLogicOp(GL_COPY_INVERTED)
         colorsInverted = true
         false
      }
      else if (!on && colorsInverted) {
         glDisable(GL_COLOR_LOGIC_OP)
         colorsInverted = false
         true
      }
      else on
   }
   def invert(block: =>Unit) {
      invertColor(true)
      block
      invertColor(false)
   }
}