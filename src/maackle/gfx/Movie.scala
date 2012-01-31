package maackle.gfx

import maackle.util._
import org.lwjgl.opengl.GL11._

class Movie(val clips:Vector[Image]) {
   object StateType extends Enumeration {
      type statetype = Value
      val Stopped, Paused, Playing = Value
   }
   import StateType._
   var state:statetype = Stopped
   private var ix:Int = 0
   private var timer:Double = 0
   private var dir = 1
   var fps:Double = 0.0 // if 0, just update with the frames
   var loop = true
   def length = clips.size
   require(length>0, "Movie is empty")

   def width = clips(ix).width
   def height = clips(ix).height
   def advance(amount:Int = 1):Boolean = {
      ix += amount
      var done = false
      if(loop) {
         if(ix < 0) ix += length
         ix %= length
      }
      else {
         if(ix>=length) ix=0
         else if(ix< 0) ix=0
         done = true
      }
      if(done) state = Stopped
      !done
   }
   def select(i:Int) { ix = i % length }
   def play() { dir=1; state = Playing }
   def pause() { state = Paused}
   def reverse() { dir = -1; state = Playing }

   def update() {
      state match {
         case Playing => {
            if(fps==0 || getMilliseconds - timer >= 1000 / fps) {
               advance(dir)
               timer = getMilliseconds
            }
         }
         case _ =>
      }
   }

   def draw() {
      clips(ix).blit()
      clips(ix).draw()
   }
}

class Animation(clips:Vector[Image]) extends Movie(clips) {//} with Spritelike {
   var scale = 1f
   var angle = 0f
}