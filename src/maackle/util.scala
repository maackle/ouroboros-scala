package maackle

import org.jbox2d.common.Vec2
import scala.math.{sin, cos}
import org.lwjgl.Sys
import org.newdawn.slick.util.ResourceLoader
import actors.{TIMEOUT, DaemonActor}
import math._

//import org.newdawn.slick.UnicodeFont

object util {

   object Random {
      val rand = new scala.util.Random()
      def uniform(lo:Double=0.0, hi:Double=1.0):Double = (hi-lo)*rand.nextDouble + lo
      def uniform(lo:Float, hi:Float):Float = (hi-lo)*rand.nextFloat + lo
      def gaussian(mean:Double, std:Double):Double = rand.nextGaussian*std + mean
   }

   def mano[T: Manifest](t: T): Manifest[T] = manifest[T]
   def rotateVec2(v:Vec2, rad:Double) {
      val ca = cos(rad).toFloat
      val sa = sin(rad).toFloat
      val (x, y) = ( v.x*ca - v.y*sa,
                     v.y*ca + v.x*sa )
      v.x = x; v.y = y;
   }

   def getStream(path:String) = ResourceLoader.getResourceAsStream(path)
   def getFile(path:String) = io.Source.fromInputStream(getStream(path))
   
   def getMilliseconds:Double = {
      ( (Sys.getTime * 1000) / Sys.getTimerResolution )
   }
   
   @inline
   def clamp(value: Float, low: Float, high: Float): Float = {
      if (value < low) low else if (value > high) high else value
   }

   def linspace(lo:Float, hi:Float, n:Int):Seq[Float] = {
      for(i <- 0 until n) yield {
         i / (n-1f) * (hi-lo) + lo
      }
   }

   def pairs[T](seq:Seq[T]) = {
      if (seq.size>1)
         seq.slice(0,seq.size-2).zip(seq.slice(1,seq.size-1))
      else
         throw new Exception("must have at least 2 elements to form pairs")
   }

   def pairwise[T](seq:Seq[T])(fn:(T,T)=>Unit) {
      seq.reduceLeft {
         (a:T, b:T) => {
            fn(a,b)
            b
         }
      }
   }

//   object schedule {
//      @inline
//      def apply(interval:Long)(fn: =>Unit) {
//         val n = new schedule(interval)(fn _)
//         n.start()
//      }
//   }

   def debug(x:Any) { println(x) }
   def info(x:Any) { println(x) }

   case class schedule(interval:Long)( fn: =>Unit ) extends DaemonActor {
//      def this (interval:Long)( fn: =>Unit ) = this(interval)(new {def update { fn } })
      def act() {
         val t = interval match {
            case _ => interval
         }
         assert(t>0)
         reactWithin(t) {
            case TIMEOUT => fn; act()
            case 'stop =>
         }
      }

   }

//   def schedule(time: Long)(f: => Unit) = {
//      import Actor._
//      def act() {
//         reactWithin(time) {
//            case TIMEOUT => f; act()
//            case 'stop =>
//         }
//      }
//      new Actor.actor(act _)
//   }


   object Radian {
      val pi = math.Pi
      val pi2 = math.Pi*2
      def clampS(in:Double)= {
         var a = in
         while (a  >  pi) a -= pi2
         while (a <= -pi) a += pi2
         a
      }
      def clampU(in:Double) = {
         var a = in
         while (a > pi2) a -= pi2
         while (a < 0)   a += pi2
         a
      }
      def diff(a:Double, b:Double) = {
         clampS(clampS(a) - clampS(b))
//         (a,b) = (clampS(a), clampS(b))
//         val d = b - a
//         if(d > pi) d - pi2
//         if(d < pi) d + pi2
      }
   }

   def xprint[T](value: =>T):T = xprint()(value)

   def xprint[T](lbl:String = "xprint")(value: =>T):T = {
      print("%s: ".format(lbl));
      val v:T = value;
      println(v);
      v
   }
   def yes[T](block: =>T) = { block }
   def no[T](block: =>T){ }

   def lerp[T : Numeric] (a:(T), b:(T), t:Float, p:Double=1.0):Float = {
      val imp = implicitly[Numeric[T]]
      val fa = imp.toFloat(a)
      val u = if(p==1.0) t else math.pow(t,p).toFloat
      fa + (imp.toFloat(b)-fa) * u
   }
   def invlerp[T : Numeric] (a:(T), b:(T), t:Float):Float = {
      val imp = implicitly[Numeric[T]]
      val fa = imp.toFloat(a)
      (t-fa)/(imp.toFloat(b)-fa)
   }

   def abspow(x:Double, p:Double):Double = {
      signum(x) * pow(abs(x), p)
   }

   trait Validates {
      def valid():Boolean
   }

   // pathPattern(dir, name, ext)
   val pathPattern = """(.*)/(.+?)\.(.+?)$""".r

   def validate[T](objs:Validates*)(block: =>T):T = {
      for ((o,i)<- objs.view.zipWithIndex)
         if(!o.valid) throw new Exception("maackle.util.validate check #%d failed at the beginning".format(i+1))
      val ret = block
      for ((o,i)<- objs.view.zipWithIndex)
         if(!o.valid) throw new Exception("maackle.util.validate check #%d failed at the end".format(i+1))
      ret
   }

   case class Countdown(val start:Int, val decrement:Int=1, looping:Boolean=false) {
      private var time = 0
      private var time2 = 0
      def set() {
         if(isExpired)
            forceSet()
      }
      def forceSet() {
         time = start
         time2 = start
      }
      def tick() {
         time2 = time
         time -= decrement
         if(looping && time < 0) {
            time = start
         }
      }

      def isExpired = time <= 0
      def justStarted = time2==start && time!=start
      @deprecated("Untested")
      def justLooped = time2<=0 && time>0
      def justEnded = time<=0 && time2>0
      def unit = {
         val r = time.toFloat / start.toFloat
         assert(r <= 1)
         clamp(r, 0, 1)
      }
      def t = time
   }

}