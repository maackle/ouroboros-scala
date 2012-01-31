package maackle.archaic

import maackle.vec
import org.jbox2d.dynamics.{Body, World}
import maackle.traits.{TransformableTR, MutablePosition}


trait Posable extends TransformableTR with MutablePosition {

   protected object EarlyAccessException extends Exception("tried to access unposed Bodied property()")

   def posed: Boolean

   def pose()(implicit world: World)

   def depose()(implicit world: World)

   def active: Boolean

   def activate()

   def deactivate()

   def postPhysics() {}
}


trait MonoPosable extends Posable with MutablePosition {
   def createBody(world: World): Body

   //TODO: don't require var
   var pos: vec

   var body: Body = null

   def posed = body != null

   def pose()(implicit world: World) {
      require(!posed, "Tried to pose twice")
      body = createBody(world)
      require(body != null, "MonoBodied::body is null")
   }

   def depose()(implicit world: World) {
      require(posed, "Tried to depose nonexistent body")
      world.destroyBody(body)
      body = null
   }

   def active = posed && body.isActive

   def activate() {
      body.setActive(true)
   }

   def deactivate() {
      body.setActive(false)
   }

   override def position = {
      if (posed) body.getPosition
      else pos
   }

   def position_=(pos: vec) {
      object UnposedException extends Exception("Can't directly change position of posed body")
      if (posed) {
         body.setLinearVelocity(pos - position)
         body.setTransform(pos, angle)
      }
      else {
         this.pos = pos
      }
   }

   def velocity: vec = {
      try (return body.getLinearVelocity)
      catch {
         case e: NullPointerException => vec.zero
      }
   }

   def angle: Float = {
      try (return body.getAngle)
      catch {
         case e: NullPointerException => 0f
      }
   }

   def angle_=(ang: Float) {
      body.setTransform(position, ang)
   }
}

trait PolyPosable extends Posable {
   def createBodies(world: World): Seq[Body]

   var bodies: Seq[Body] = null
   private var active_? = false

   def posed = bodies != null

   def pose()(implicit world: World) {
      require(!posed, "Tried to pose twice")
      active_? = true
      bodies = createBodies(world)
      require(posed, "createBodies failed")
   }

   def depose()(implicit world: World) {
      require(posed, "Tried to depose nonexistent body")
      bodies.foreach(world.destroyBody(_))
      bodies = null
   }

   def active = posed && active_?

   def activate() {
      bodies.foreach(_.setActive(true))
      active_? = true
   }

   def deactivate() {
      bodies.foreach(_.setActive(false))
      active_? = false
   }
}
