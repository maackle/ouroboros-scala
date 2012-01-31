package maackle

import org.jbox2d.common.Vec2
import scala.math._
import maackle.util._

object vec {
   implicit def Vec2_vec(v:Vec2):vec = new vec(v.x, v.y)
   implicit def vec_tuple(v:vec):(Float, Float) = (v.x, v.y)
   implicit def tuple_vec(x:(Double, Double)):vec = new vec(x._1, x._2)

   implicit def Double_Float(c:Double):Float = c.toFloat
//   implicit def Float_Double(c:Float):Double = c.toDouble
   def zero = vec(0,0)
   val eps:Float = 0.001f
   object polar {
      def apply(r:Float, t:Double) = vec(r*cos(t), r*sin(t))
      def apply(v:vec):vec = polar(v.length, v.angle)
      def random(r:Float) = polar(Random.uniform(0,r), Random.uniform(0,math.Pi*2))
   }

   def apply[T : Numeric](x: T, y: T) = {
      val n = implicitly[Numeric[T]]
      new vec(n.toFloat(x), n.toFloat(y))
   }
   def apply[T : Numeric](a: (T,T)) = {
      val n = implicitly[Numeric[T]]
      new vec(n.toFloat(a._1), n.toFloat(a._2))
   }
   def apply(v: Vec2) = {
      new vec(v.x, v.y)
   }
   def lerp(a:vec, b:vec, t:Float) = {
      if(0 <= t && t <= 1.000001) {}
      else printf("lerp() warning: t=%f out of range[0,1]", t)
      a*(1-t) + b*t
   }
   def pow(v:vec, p:Float) = {
      vec(math.pow(v.x, p), math.pow(v.y,p))
   }
}

class vec(xx:Float, yy:Float) extends Vec2(xx, yy) with maackle.util.Validates {
   def this() = this(0.0f, 0.0f)
   def this(v:Vec2) = this(v.x, v.y)

   def valid():Boolean = {
      val v = !x.isNaN && !y.isNaN
      v
   }

   def rotated(rad:Double):vec = {
      val ca = cos(rad).toFloat
      val sa = sin(rad).toFloat
      vec ( x*ca - y*sa,
            y*ca + x*sa )
   }
   def project(other:vec):vec = {
      val denom = (other dot other)
      if(denom < vec.eps) return vec.zero
      else return other * ((this dot other)/denom)
   }
   def unit = {
      val len = length
      if(len < vec.eps || len.isNaN) vec.zero
      else this / len
   }
   def limit(cap:Float):vec = {
      val len = length
      if (len > cap && !len.isNaN) {
         vec(
         x * cap/len,
         y * cap/len
         )
      }
      else this
   }
   def manhattan = math.abs(x) + math.abs(y)
   def angle:Double = {
      if(x!=0 || y!=0) atan2(y,x) else 0
   }

   def flipX = vec(-x, y)
   def flipY = vec(x, -y)

   def +(v:vec):vec = new vec(x+v.x, y+v.y)
   def -(v:vec):vec = new vec(x-v.x, y-v.y)
   def dot(v:vec):Float = (x*v.x + y*v.y)
//   def •(v:vec):Float = (x*v.x + y*v.y)
   def *(c:Float):vec = new vec(x*c, y*c)
   def *(c:Double):vec = new vec(x*c toFloat, y*c toFloat)
   def *(v:vec):vec = vec(x*v.x, y*v.y)
   def /(c:Float):vec = new vec(x/c, y/c)

   def +=(v:vec) { x+=v.x; y+=v.y }
   def -=(v:vec) { x-=v.x; y-=v.y }

   def polar = (length,angle)

   def *=(v:vec):vec = {
      x *= v.x
      y *= v.y
      this
   }
   def *=(c:Float):vec = {
      x *= c
      y *= c
      this
   }

   def unary_- : vec = new vec(-x, -y)
}


/*

case class vec(override val x:Float, override val y:Float) extends Vec2(x, y) {
   def this() = this(0.0f, 0.0f)
   def this(v:Vec2) = this(v.x, v.y)
   def rotate(rad:Double) {
      val ca = cos(rad).toFloat
      val sa = sin(rad).toFloat
      vec ( x*ca - y*sa,
            y*ca + x*sa )
   }
   def rotated(rad:Double):vec = {
      val ca = cos(rad).toFloat
      val sa = sin(rad).toFloat
      vec ( x*ca - y*sa,
            y*ca + x*sa )
   }
   def project(other:vec):vec = {
      val denom = (other*other)
      if(denom < vec.eps) return vec.zero
      else return other * ((this*other)/denom)
   }
   def unit = this / this.length
   def limit(cap:Float):vec = {
      val len = length
      if (len > cap) {
         vec(
         x * cap/len,
         y * cap/len
         )
      }
      else this
   }
   def manhattan = math.abs(x) + math.abs(y)
   def angle = atan2(y,x)
   def +(v:vec):vec = vec(x+v.x, y+v.y)
   def -(v:vec):vec = vec(x-v.x, y-v.y)
   def *(v:vec):Float = (x*v.x + y*v.y)
   def *(c:Float):vec = vec(x*c, y*c)
   def *(c:Double):vec = vec(x*c toFloat, y*c toFloat)
   def /(c:Float):vec = vec(x/c, y/c)

//   def *=(v:vec):vec = {
//      x *= v.x
//      y *= v.y
//      this
//   }
//   def *=(c:Float):vec = {
//      x *= c
//      y *= c
//      this
//   }

   def unary_- : vec = vec(-x, -y)
}

*/