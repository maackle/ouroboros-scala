package maackle.b2d

/*
import org.jbox2d.collision.shapes.{Shape, CircleShape}
import org.jbox2d.dynamics.{FixtureDef, Body, World, joints}

object shape {
   object circle {
      def apply(radius:Float)(implicit c:CircleShape) {
         c.m_radius = radius
         c
      }
   }
}
object fixture {
   def apply(shape:Shape,
             density:Float = -1f,
             restitution:Float = -1f,
             friction:Float = -1f)
   (implicit fd:FixtureDef) {
      
   }
}
object body {

   def apply(bodyA:Body, bodyB:Body,
             length:Float = -1,
             dampingRatio:Float = -1f,
             frequencyHz:Float = -1f
               )(implicit world:World, d:joints.DistanceJointDef) = {

   }
}
class body private (val d:joints.DistanceJointDef)(implicit world:World) {

   private val j = world.createJoint(d)
   def joint = j

   private var posed_? = true
   def posed = posed_?
   def pose() {
      if(posed) return
      posed_? = true
      world.createJoint(d)
   }
   def depose() {
      if(!posed) return
      posed_? = false
      world.destroyJoint(j)
   }

}

*/