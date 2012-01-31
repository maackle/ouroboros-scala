package ouroboros.states

import maackle.GameState
import maackle.gfx.Tex
import org.jbox2d.dynamics.World
import ouroboros.Params

class _StateTemplate extends GameState {
   val world = new World(Params.Box2D.gravity, true)
   def update() {

   }
   def render() {

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