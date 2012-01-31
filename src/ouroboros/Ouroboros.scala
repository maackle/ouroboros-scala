package ouroboros


import org.lwjgl._
import opengl.{Display,GL11,DisplayMode}
import GL11._
import states._
import maackle.{Input, SoundStore, GameState}
import org.newdawn.slick.TrueTypeFont
import java.awt.Font
import maackle.util._
import maackle.GLX._
import maackle.stateful.SInt
import java.nio.IntBuffer

object Ouroboros extends maackle.Game {
   def it = this
	val title = "GAM3"
	val framerate = 60
   val (w, h) = (800, 800)
   val windowSize = (w, h)
   val fullscreen = Params.fullscreen
   val pxScale = Params.pxScale
   lazy val startState = new PlayState

   protected val doSound = Params.doSound
   protected val useX360 = Params.useX360

   val sfx = new SoundStore

   def initAudio() {
      SoundStore.init()
//      sfx.addSource("name1", "path/to/snd.wav")
//      sfx.addSource("name2", "path/to/snd.wav", loop=true)
   }

   override def init() {

      super.init()

      glEnable(GL_LINE_SMOOTH)
      glHint(GL_LINE_SMOOTH_HINT, GL_NICEST)

      if(Params.enableControllers)  Input.enableControllers()
      if(Params.grabMouse) org.lwjgl.input.Mouse.setGrabbed(true)
      if(doSound) initAudio()
   }

   override def cleanup() {
      if(doSound) {
         sfx.destroy()
      }
      super.cleanup()
   }

}
