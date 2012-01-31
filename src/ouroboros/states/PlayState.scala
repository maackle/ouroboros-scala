package ouroboros.states

import org.jbox2d.dynamics.World
import ouroboros.Params
import maackle.gfx.Tex
import ouroboros.things.{Clod, Planet}
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import maackle.{DebugDrawGL, GLX, vec, GameState}

class PlayState extends GameState {
   implicit val world = new World(Params.Box2D.gravity, false)
   world.setDebugDraw(DebugDrawGL)

   val clouds = new Tex("img/clouds.png")
   val planet = Planet
   planet.pose()

   override def input() {
      import Keyboard._
      if(isKeyDown(KEY_MINUS)) zoom *= 0.95f
      if(isKeyDown(KEY_EQUALS)) zoom /= 0.95f
   }

   def update() {
      import Params.Box2D.stepTuple
      planet.update()
      Clod.all.foreach(_.update())
      world.step(stepTuple._1, stepTuple._2, stepTuple._3)
   }

   def render() {
      GLX.clear(GLX.Color(107,127,147))
      clouds.draw()
      planet.render()
      Clod.all.foreach(_.render())
      GLX.fill(false)

//      world.drawDebugData()
   }

   def cleanup() {

   }
   def onEnter() {

   }
   def onExit() {

   }
   def softReset() {}

   import org.jbox2d.dynamics.contacts.Contact
   import org.jbox2d.collision.Manifold
   import org.jbox2d.callbacks.ContactImpulse
   import org.jbox2d.callbacks.ContactListener

   trait CL extends ContactListener {

      def beginContact(contact:Contact) {
         assert(active_?)
         if (!contact.isTouching || !active_?) return
         val a = contact.getFixtureA.getUserData
         val b = contact.getFixtureB.getUserData
         (a, b) match {
            case _ =>
         }
      }
      def endContact(contact:Contact) {

      }
      def preSolve(contact:Contact, oldManifold:Manifold) {

      }
      def postSolve(contact:Contact, impulse:ContactImpulse) {

      }
   }
}