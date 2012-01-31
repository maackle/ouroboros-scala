package ouroboros.states

import maackle.GLX.Color

import ouroboros.Draw
import org.jbox2d.dynamics.World
import org.lwjgl.input.Keyboard
import maackle._
import gfx.{Movie, SpriteSheet, Tex}

class TestState extends GameState {
   val tex = new Tex("img/player-2.png")
   implicit val world = new World(vec.zero, false)

   val ss = new SpriteSheet("img/oob.png", 2, 5)
   val ssr = ss(0,0)
   val mov = new Movie(ss.images)
   mov.fps = 4

   val music = new SoundStore
   val poods = music.addSource("bgm", "snd/nickypoods.wav")
   poods.play()

   override def input() {
      import Keyboard._
      if(isKeyDown(KEY_EQUALS)) zoom *= 1.1f
      if(isKeyDown(KEY_MINUS)) zoom /= 1.1f
      if(isKeyDown(KEY_LEFT)) mov.play()
      else mov.pause()
   }

   def update() {
      mov.update()
   }

   def render() {
      GLX.clear(Color.white)
      Color.red.bind()
      world.drawDebugData()
//      help.pixelwise {
//      mov.draw()
//      }
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