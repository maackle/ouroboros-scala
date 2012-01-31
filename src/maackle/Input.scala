package maackle

import maackle.util._
import org.lwjgl.opengl.GL11
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
import java.nio.IntBuffer
import org.lwjgl.input.{Keyboard, Mouse}

import net.java.games.input
import input.Component.Identifier
import net.java.games.input._
import stateful.SBoolean
import collection.mutable.ListBuffer

//TODO: implement actual listeners, do away with most of the def's replicating private vals


case class KeyState(val key:Int) extends SBoolean {
   def update() {
      val b = Keyboard.isKeyDown(key)
      super.update(b)
   }
   def press = now && !prev
   def release = !now && prev
}

object Input {

   private var joy:vec = vec.zero

   private var ctlEnv:ControllerEnvironment = null

   object Controllers {
      lazy val gamepads:Array[Controller] = {
         val ctls = {
            if(useControllers)
               for(c <- ctlEnv.getControllers if c.getType == input.Controller.Type.GAMEPAD) yield {c}
            else
               new Array[Controller](0)
         }
         println("found %d game pad(s)".format(ctls.size))
         ctls
      }
      def findMice(reqd:Seq[String]=List("x", "y", "Left", "Right")):List[Controller] = {
         val ctls:List[Controller] = {
            if(useControllers)
               for(ctl:Controller <- ctlEnv.getControllers.toList if ctl.getType == input.Controller.Type.MOUSE) yield {
                  var required = collection.mutable.Map[String,Boolean]()
                  for(r <- reqd) required += r -> false
                  for(cmp <- ctl.getComponents) {
                     if(required.contains(cmp.getName)) required(cmp.getName) = true
                  }
                  if(required.filter(_._2 == false).isEmpty) ctl
                  else null
               }
            else
               List[Controller]()
         }
         val usable = ctls.filter(_ != null).filter(_.getName != "Trackpad")
         println("found %d mouse(s)".format(usable.size))
         usable
      }
      lazy val all = for(c <- ctlEnv.getControllers) yield c
   }

   object In {
      var all = ListBuffer[In]()
   }
   abstract class In {
      def update()
      In.all += this
   }

   abstract class Gamepad(val ctl:Controller) extends In {
      override def toString = ctl.toString
   }

   object X360 {
      import input.Component.Identifier.{Button=>btn}
      val A = btn._0
      val B = btn._1
      val X = btn._2
      val Y = btn._3
      val L = btn._4
      val R = btn._5
      val stickLeft = btn._6
      val stickRight = btn._7
      val START = btn._8
      val SELECT = btn._9
      val XBOX = btn._10
      val UP = btn._11
      val DOWN = btn._12
      val LEFT = btn._13
      val RIGHT = btn._14
   }
   class X360(ctl:Controller) extends Gamepad(ctl) {
      import input.Component.Identifier.Button

      println("found %d rumbler(s)".format(ctl.getRumblers.size))

      val deadzone = 0.05f
      val btns = new collection.mutable.ArrayBuffer[Boolean](15)
      val btns0 = new collection.mutable.ArrayBuffer[Boolean](15)
      for(_ <- 0 to 14) { btns.append(false); btns0.append(false) }
      val stick:vec = vec.zero
      val stickRight:vec = vec.zero
      var triggerLeft:Float = 0f
      var triggerRight:Float = 0f

      def update() {
         import Identifier.{Axis, Button}
         ctl.poll()
         validate(stick) {
         for(c <- ctl.getComponents) {
            val z = c.getDeadZone
            var d = c.getPollData
            if(math.abs(d) < math.max(z, deadzone)) d = 0

            c.getIdentifier match {
               case Axis.X =>
                  stick.x = d
               case Axis.Y =>
                  stick.y = -d
               case Axis.Z =>
                  triggerLeft = d
               case Axis.RZ =>
                  triggerRight = d
               case b:Button =>
                  val ix = b.getName.toInt
                  btns0(ix) = btns(ix)
                  btns(ix) = d == 1
               case _ =>
            }
         }
         }
      }
      def buttonDown(b:Button) = {
            val ix = b.getName.toInt
            btns(ix)
      }
      def buttonUp(b:Button) = {
         val ix = b.getName.toInt
         btns(ix)
      }
      def buttonPressed(b:Button) = {
         val ix = b.getName.toInt
         !btns0(ix) && btns(ix)
      }
      def buttonReleased(b:Button) = {
         val ix = b.getName.toInt
         btns0(ix) && !btns(ix)
      }

   }


   def getKeys(block: (Int)=>Unit) {
      Keyboard.poll()
      while (Keyboard.next()) {
         block(Keyboard.getEventKey())
      }
   }

   private var useControllers = false
   def enableControllers() {
      useControllers = true
//      Controllers.create()
      ctlEnv = ControllerEnvironment.getDefaultEnvironment
   }


   class MouseState(val position:vec, val down:(Boolean, Boolean)) {
      def left = down._1
      def right = down._2
   }

   def blank() {
      import org.lwjgl.input
      val cursor = new input.Cursor(1, 1, 0, 0, 1, BufferUtils.createIntBuffer(1), BufferUtils.createIntBuffer(1))
      input.Mouse.setNativeCursor(cursor)
   }

   class Mouze(ctl:Controller, var position:vec) extends In {

      private var dragStartPoint:vec = null
      private var dragBegin = false
      private var dragging = false
      private var dragEnd = false

      def leftClick    = !prev.left  &&  leftDown
      def rightClick   = !prev.right &&  rightDown
      def leftRelease  =  prev.left  && !leftDown
      def rightRelease =  prev.right && !rightDown
      def dragFrom = if(dragging || dragEnd) dragStartPoint else null
      def onDragBegin = dragBegin
      def onDragEnd = dragEnd

      var delta = vec(0,0)
      val deadzone = 0.05f
      val sensitivity = 0.01f
//      def screenPos = world2screen(position)
      val btns = collection.mutable.ArrayBuffer[Boolean](false, false, false)
      def leftDown = btns(0)
      def middleDown = btns(1)
      def rightDown = btns(2)
      var prev:MouseState = new MouseState(vec.zero, (false, false))

      def update() {
         prev = new MouseState(position, (leftDown, rightDown))
         ctl.poll()
         for(c <- ctl.getComponents) {
            var d = c.getPollData
            if(math.abs(d) < deadzone) d = 0
            c.getIdentifier.toString match {
               case "Left" => btns(0) = if(d > 0) true else false
               case "Middle" => btns(1) = if(d > 0) true else false
               case "Right" => btns(2) = if(d > 0) true else false
               case "x" => delta.x = d * sensitivity
               case "y" => delta.y = -d * sensitivity
               case _ =>
            }
         }
         position += delta

         if(leftClick) {
            dragBegin = true
            dragging = true
            dragStartPoint = snap(position)
         }
         else {
            dragBegin = false
         }
         if(leftRelease) {
            dragEnd = true
            dragging = false
         }
         else dragEnd = false
      }
   }

   private var snaplen = 0.5f

   def snap(p:vec) = {
      val x = math.round(p.x / snaplen)
      val y = math.round(p.y / snaplen)
      vec(x*snaplen,y*snaplen)
   }

   object Mice {
      private var dragStartPoint:vec = null
      private var clickL, clickR = false
      private var dragBegin = false
      private var dragging = false
      private var dragEnd = false

      def dragFrom = if(dragging || dragEnd) dragStartPoint else null
      def onDragBegin = dragBegin
      def onDragEnd = dragEnd
      def onClick = clickL

      def mouseLeft = Mouse.isButtonDown(0)
      def mouseRight = Mouse.isButtonDown(1)

      var prev:MouseState = new MouseState(vec.zero, (false, false))

      def mouseWorldPos = Game.Screen.screen2world(vec(Mouse.getX, Mouse.getY))
      def mouseScreenPos = (vec(Mouse.getX, Mouse.getY))

      def update() {
         if(!prev.left && mouseLeft) {
            dragBegin = true
            dragging = true
            clickL = true
            dragStartPoint = snap(mouseWorldPos)
         }
         else {
            dragBegin = false
            clickL = false
         }
         if(prev.left && !mouseLeft) {
            dragEnd = true
            dragging = false
         }
         else dragEnd = false
         prev = new MouseState(mouseScreenPos, (mouseLeft, mouseRight))
      }
   }
   def update() {
      for(i <- In.all) i.update()
      Mice.update()
   }

//   lazy val mouse = {
//      val mice = Controllers.findMice()
//      if(mice.length > 0)
//         new Mouze(mice.head, vec.zero)
//      else
//         null
//   }
}
