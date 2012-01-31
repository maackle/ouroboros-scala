package ouroboros

import org.jbox2d.collision.shapes._
import org.jbox2d.dynamics._
import maackle.vec


object BodyFactory {

   def Planet()(implicit world:World) = {
      import ouroboros.Params.Defaults._
      import ouroboros.Params.Planet.initRadius
      val filter = new Filter
      val bodydef = new BodyDef
      val fixture = new FixtureDef
      val circle = new CircleShape

      bodydef.`type` = BodyType.DYNAMIC
      bodydef.angularDamping = angularDamping
      bodydef.linearDamping = linearDamping
      bodydef.position = vec.zero
      bodydef.bullet = true
      val body = world.createBody(bodydef)

      filter.categoryBits = 0x01
      filter.maskBits = 0xff
      circle.m_radius = initRadius
      fixture.shape = circle
      fixture.density = density
      fixture.restitution = restitution
      fixture.friction = friction
      fixture.userData = this
      fixture.filter = filter
      body.createFixture(fixture)
      body
   }

   def Clod(pos:vec, vel:vec)(implicit world:World) = {
      import ouroboros.Params.Clod._
      val filter = new Filter
      val bodydef = new BodyDef
      val fixture = new FixtureDef
      val circle = new CircleShape

      bodydef.`type` = BodyType.DYNAMIC
      bodydef.angularDamping = 0f
      bodydef.linearDamping = 0f
      bodydef.position = pos
      bodydef.bullet = true
      val body = world.createBody(bodydef)

      filter.categoryBits = 0x04
      filter.maskBits = 0xff - 0x01
      circle.m_radius = radius
      fixture.shape = circle
      fixture.density = density
      fixture.restitution = 1f
      fixture.friction = 0f
      fixture.userData = this
      fixture.filter = filter
      body.createFixture(fixture)
      body.setLinearVelocity(vel)
      body
   }

}