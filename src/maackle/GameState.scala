package maackle

import org.lwjgl.opengl.GL11._
import org.lwjgl.util.glu.GLU
import scala.collection.mutable
import org.lwjgl.opengl.GL11
import org.lwjgl.BufferUtils
import java.nio.{IntBuffer, FloatBuffer}


abstract class Tween  {
   val fps = 60
   def update()
}

case class SimpleMove(what:{def position:vec; def position_=(v:vec):Unit}, to:vec, ms:Int) extends Tween {

   var t:Float = 0.01f
   private var t0 = 0

   def update() {
      what.position = vec.lerp(what.position, to, t)
   }

}


abstract class GameState {
   var zoom:Float = 1f
   var viewRotation:Float = 0.0f
   var scroll:vec = vec(0,0)

//   var allThings:mutable.Set[TransformableAffine] = mutable.Set()
   protected var steps = 0
   protected var bgColor = {
      ouroboros.Params.bgColor
   }
   protected var active_? = false

   def init() {}
   def input() {}
   def update()
   def preRender() {}
   def render()
   def postRender() {}
   def transfer() {}


   //TODO: require that if overridder, super() is called
   def tick() {
      input()
      update()
      preRender()
      worldTransform {
         Game.Screen.update()
         Input.update()
         render()
      }
      postRender()
      transfer()
      handlePending()
      steps+=1
   }

   private var pendingPush:Option[GameState] = None
   private var pendingSwitch:Option[GameState] = None
   private var pendingPop = false
   private def pendingXfer = !pendingPush.isEmpty || !pendingSwitch.isEmpty || pendingPop

   def warn(msg:String) { println("warning(State) %s".format(msg)) }

   protected def onEnter()
   protected def transfer(ss:StateStack) {  }
   protected def onExit()
   private def handlePending():Boolean = {
      val pending = pendingXfer
      pendingPush map { GameState.push(_) }
      pendingSwitch map { GameState.change(_) }
      if(pendingPop)  { GameState.pop() }
      pendingPush = None
      pendingSwitch = None
      pendingPop = false
      pending
   }
   protected def push(state:GameState) {
      if(!pendingXfer) pendingPush = Some(state)
      else warn("tried to push twice")
   }
   protected def switch(state:GameState) {
      if(!pendingXfer) pendingSwitch = Some(state)
      else warn("tried to change twice")
   }
   protected def pop() {
      if(!pendingXfer) pendingPop = true
      else warn("tried to pop twice")
   }

   def worldTransform(block: =>Unit) {
      beginRender()
      block
      endRender()
   }

   private def beginRender() {

      glMatrixMode(GL_MODELVIEW)
      glPushMatrix()
      glRotatef(viewRotation,0,0,-1)
      glScaled(zoom, zoom, 1)
      glTranslatef(-scroll.x, -scroll.y, 0)

   }

   private def endRender() {
      glPopMatrix()
   }

   def cleanup()

   def softReset()


}


object GameState extends StateStack {

   protected def change(state:GameState) {
      if(empty) throw new StateException("changing empty stack")
      else current.onExit()
      current.active_? = false
      stack.pop()
      stack.push(state)
      current.active_? = true
      current.onEnter()
   }
   protected def push[S>:GameState](state:S) {
      if(!empty) {
         current.active_? = false
         current.onExit()
      }
      stack.push(state.asInstanceOf[GameState])
      current.onEnter()
      current.active_? = true
   }
   protected def pop(num:Int=1) {
      if(empty) throw new StateException("popping empty stack")
      for(i <- 1 to num) {
         current.active_? = false
         current.onExit()
         stack.pop()
      }
      if(!empty) {
         current.onEnter()
         current.active_? = true
      }
   }
}

sealed abstract class StateStack {
   class StateException(s:String="") extends Exception
   protected var stack = mutable.Stack[GameState]()

   protected def change(state:GameState)
   protected def push[S>:GameState](state:S)
   protected def pop(num:Int=1)
   def init(state:GameState) {
      if(empty) push(state)
   }

   def empty = stack.length == 0
   def current:GameState = {
      if(!empty) stack.top
      else throw new StateException("Tried to access empty state stack")
   }
}
