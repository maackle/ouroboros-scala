package maackle

import org.lwjgl.opengl.GL11._
import collection.mutable.Queue
import org.jbox2d.dynamics.{World, Body}
import misc._

package object traits {

   trait Spatial {
      def position: vec
   }

   trait MutablePosition extends Spatial {
      def position_=(v: vec)
   }

   trait Rotatable {
      def angle: Float
   }

   //TODO: find a way to make this a def with default val.  too risky otherwise, might accidentally set it to 0.
   trait Scalable {
      def scale: Float

      //      require(scale!=0, "scale is 0!")
   }

   trait Affine extends Spatial with Rotatable with Scalable {

   }

   trait Rendered {
      def render()

      def transformer(fn: => Unit) {
         fn
      }
   }

   trait Updated {
      def update()
   }

   trait MaackleBase extends Rendered with Updated with Cloneable {

   }


   trait TransformableAffine extends MaackleBase with Affine {
      override def transformer(fn: => Unit) {
         glPushMatrix()
         glTranslatef(position.x, position.y, 0)
         glRotatef(angle * 180 / math.Pi toFloat, 0, 0, 1)
         glScalef(scale, scale, 1)
         fn
         glPopMatrix()
      }
   }

   trait TransformableTR extends MaackleBase with Spatial with Rotatable {
      override def transformer(fn: => Unit) {
         glPushMatrix()
         glTranslatef(position.x, position.y, 0)
         glRotatef(angle * 180 / math.Pi toFloat, 0, 0, 1)
         fn
         glPopMatrix()
      }
   }

   trait Bodied extends TransformableTR {

      protected object EarlyAccessException extends Exception("tried to access unposed Bodied property()")

      // Deactivating a body removes it from physical interaction
      def active: Boolean
      def activate()
      def deactivate()

      // Creates the body(ies) and adds it to the world
      def pose()(implicit world: World)
      def posed:Boolean
      
      def postPhysics() {}
   }

   trait MonoBodied extends Bodied with MutablePosition {

      private var _body:Body = null
      def body:Body = _body
      def posed = body!=null
      def pose()(implicit world: World) {
         assert(!posed)
         _body = createBody(world)
      }
      def depose()(implicit world: World) {
         assert(posed)
         world.destroyBody(_body)
      }

      // Gets called when you pose()
      def createBody(world: World): Body

      def active = body.isActive

      def activate() {
         require(posed)
         body.setActive(true)
      }

      def deactivate() {
         require(posed)
         body.setActive(false)
      }

      override def position = {
         require(posed)
         body.getPosition
      }

      def position_=(pos: vec) {
         require(posed)
         body.setLinearVelocity(pos - position)
         body.setTransform(pos, angle)
      }

      def velocity: vec = {
         require(posed)
         try (return body.getLinearVelocity)
         catch {
            case e: NullPointerException => vec.zero
         }
      }

      def angle: Float = {
         require(posed)
         body.getAngle
      }

      def angle_=(ang: Float) {
         require(posed)
         body.setTransform(position, ang)
      }

      def mass:Float = body.getMass
   }


   trait PolyBodied extends Bodied {
      type Bodies = List[Body]
      private var _bodies:Bodies = null
      def bodies = _bodies
      def posed = bodies!=null
      def pose()(implicit world: World) {
         assert(posed)
         _bodies = createBodies(world)
      }

      // Gets called when you pose()
      def createBodies(world: World):Bodies

      private var _isActive = true
      def active = _isActive

      def activate() {
         require(posed)
         _isActive = true
         bodies.foreach(_.setActive(true))
      }

      def deactivate() {
         require(posed)
         _isActive = true
         bodies.foreach(_.setActive(false))
      }
   }

   trait CircularBody {
      def body:Body
      lazy val b2shape = body.getFixtureList.getShape
      def radius = b2shape.m_radius
      def radius_=(r:Float) { b2shape.m_radius = r }
   }


   trait ActiveSwitch {
      private var active_? = true
      def active = active_?
      def activate() {
         if(active_?) return
         active_? = true
      }
      def deactivate() {
         if(!active_?) return
         active_? = false
      }
   }

}