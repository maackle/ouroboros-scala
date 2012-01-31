package maackle.particles

import maackle.traits.Spatial
import maackle.GLX.Color
import maackle.vec
import maackle.util._
import collection.mutable.ArrayBuffer
import ouroboros.Draw

class Particle(

               ) extends Spatial {
   var alive = false
   var position:vec = null
   var radius:Float = 0.5f
   var color:Color = null
   var life:Float = 0
   var velocity:vec = vec.zero

   override def toString = "[Particle: pos=%s, life=%d]".format(position, life)

}

class ParticleEmitter() extends Spatial {
   private val pool = ArrayBuffer[Particle]()

   val number = 500
   var rate = 10
   val lifetime:Float = 50f
   var position = vec.zero
   var emitSpeed = 0.2f
   def emitSpeedJitter = emitSpeed / 3
   var emitAngle = 0.0
   var emitSpread = math.Pi/4
   var emitRadius = 0.5f
   var color = Color.gray
   var ix = 0
   var angle = 0f
   var spread = math.Pi*2
   var active = true

   for (i <- 0 until number) pool += new Particle()

   @inline
   def init(p:Particle) {
      p.position = vec(position)
      p.color = color.copy()
      p.velocity = vec.polar(emitSpeed+Random.uniform(-emitSpeedJitter, emitSpeedJitter), emitAngle+Random.uniform(-emitSpread/2, emitSpread/2))
      p.radius = emitRadius
      p.life = lifetime
      p.alive = true
   }

   @inline
   def move(p:Particle) {
      if(!p.alive) return
      p.position += p.velocity
      p.life -= 1
      if(p.life <= 0) p.alive = false 
   }

   def crank() {
      for((p, i) <- pool.view.zipWithIndex) {
         if(active && ix <= i && i < ix + rate) {
            init(p)
         }
         move(p)
      }
      if(active) {
         ix += rate
         if(ix >= number) ix = 0
      }
   }


   def render() {
      //      begin(GL_POINTS) {
         Draw.fill(true)
         for(p <- pool if p.alive) {
            p.color.copy(a=p.life / lifetime).bind()
            Draw.circle(p.position, p.radius, 5)
         }

//      }
   }

}