package maackle.b2d



import org.jbox2d.dynamics.{Body, World, joints}
import joints.{DistanceJointDef}
import maackle.vec

case class DJDef(
)(implicit defn: DistanceJointDef) extends DistanceJointDef {

   dampingRatio = defn.dampingRatio

   def box2d = this.asInstanceOf[joints.DistanceJointDef]
}

object DistanceJoint {

   def apply(bodyA:Body, bodyB:Body,
             length:Float = -1,
             dampingRatio:Float = -1f,
             frequencyHz:Float = -1f
               )(implicit world:World, d:joints.DistanceJointDef) = {

      d.initialize(bodyA, bodyB, bodyA.getPosition, bodyB.getPosition)
      if(length > 0) d.length = length
      else d.length = (vec(bodyA.getPosition) - vec(bodyB.getPosition)).length
      if(frequencyHz > 0) d.frequencyHz = frequencyHz
      if(dampingRatio > 0) d.dampingRatio = dampingRatio

      world.createJoint(d)
   }
   implicit def toJoint(dj:DistanceJoint):joints.Joint = dj.j
}
class DistanceJoint private (val d:joints.DistanceJointDef)(implicit world:World) {

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
