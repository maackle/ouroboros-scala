package maackle


import maackle.util._
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11._
import ouroboros.states.{_StateTemplate}
import org.lwjgl.BufferUtils
import java.nio.{IntBuffer, FloatBuffer}
import org.lwjgl.opengl.{GL11, PixelFormat, DisplayMode, Display}
import org.lwjgl.util.glu.GLU

object Game {

   private var instance:Game = null
   private def game:Game = instance

   def pxScale = game.pxScale
   def fps = game.fps
   def frameTime = game.frameTime
   var invertColor = false

   def apply() = instance

   def setInstance(g:Game) {
      assert(instance==null)
      instance = g
   }

   object Screen {

      private var viewport: IntBuffer = BufferUtils.createIntBuffer(16)
      private var modelview: FloatBuffer = BufferUtils.createFloatBuffer(16)
      private var projection: FloatBuffer = BufferUtils.createFloatBuffer(16)
      private var winZ: FloatBuffer = BufferUtils.createFloatBuffer(1)
      private var winX: Float = 0
      private var winY: Float = 0
      private var position: FloatBuffer = BufferUtils.createFloatBuffer(3)

      def update() {

         GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview)
         GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection)
         GL11.glGetInteger(GL11.GL_VIEWPORT, viewport)
      }

      def screen2world(screen:vec): vec = {
         winX = screen.x
         winY = screen.y //viewport.get(3).asInstanceOf[Float] - screen.y
         //      GL11.glReadPixels(screen.x.toInt, winY.asInstanceOf[Int], 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, winZ)
         GLU.gluUnProject(winX, winY, 0, modelview, projection, viewport, position)
         val ret = vec(position.get(0), position.get(1))
         ret
      }
   }
}
trait Game {

   Game.setInstance(this)
   def width:Float = Display.getDisplayMode.getWidth * pxScale
   def height:Float = Display.getDisplayMode.getHeight * pxScale
   def dim = (width, height)
   def maxDim = math.max(width, height)
   def minDim = math.min(width, height)
   def windowSize:(Int,Int)
   def title:String
   def framerate:Int

   def topLeft = vec(-width/2, height/2)
   def topRight = vec(width/2, height/2)
   def bottomLeft = vec(-width/2, -height/2)
   def bottomRight = vec(width/2, -height/2)
   def center = vec.zero

   def state = GameState.current
   def startState:GameState

   val pxScale:Float
   var steps:Int = 0
   var fps:Int = 0
	val fullscreen:Boolean
   private var frameTime:Double = 0
   private var (fpsLastTime, fpsLastStep) = (getMilliseconds, 0)
   private var lastTime:Double = 0
   protected var finished = false

	def main(args:Array[String]){

//		if(args!=null)
//         for(arg <- args){
//            arg match{
//               case "-fullscreen" =>
//                  fullscreen = true
//            }
//         }

		init()
		run()
      cleanup()
   }

   def softReset() {
      state.softReset
      state match {
         case s:_StateTemplate =>
         case _ =>
      }
      println("*** SOFT RESET ***")
   }

   val resetCounter = Countdown(120)

   def initJRebel() {
      import org.zeroturnaround.javarebel._
      ReloaderFactory.getInstance().addClassReloadListener( new ClassEventListener() {
         def onClassEvent(eventType:Int, klass:Class[_]) {
            klass.getSimpleName match {
               case _ =>
                     resetCounter.forceSet()

            }
         }
         def priority = ClassEventListener.PRIORITY_DEFAULT
      })
   }

   def run() {

      GameState.init(startState)

      while(!finished) {
         tick()
         state.tick()
         steps+=1
         Display.update()
         Display.sync(framerate)
      }
      cleanup()
   }

   def init() {
		Display.setTitle(title)
		Display.setVSyncEnabled(true)
		if(fullscreen) Display.setFullscreen(fullscreen)
      else Display.setDisplayMode(new DisplayMode(windowSize._1, windowSize._2))
		Display.create(new PixelFormat(8, 16, 4))
      Display.sync(60)

      org.jbox2d.common.Settings.velocityThreshold = 0.01f

      initJRebel()

      glDisable(GL_DEPTH_TEST)
      glDisable(GL_LIGHTING)
      glEnable (GL_BLEND)
      glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

      glMatrixMode(GL_PROJECTION)
      glLoadIdentity()
      glOrtho(-width/2,width/2, -height/2, height/2,-1,1)
   }

	def cleanup() {
      state.cleanup()
      SoundStore.destroyAll()
		Display.destroy()
   }

   def update() {
      import Keyboard._

		if(isKeyDown(KEY_ESCAPE) || Display.isCloseRequested)
			finished = true

      resetCounter.tick()
      if(resetCounter.justEnded)
         softReset()
   }

   def calcFPS() {
      val t = getMilliseconds
      frameTime = ( t - lastTime ) / 1000.0
      lastTime = t
      if (t - fpsLastTime > 1000) {
         fps = steps - fpsLastStep
         fpsLastStep = steps
         fpsLastTime += 1000
      }
   }

   def tick() {
      update()
      calcFPS()
   }
}