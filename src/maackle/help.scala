package maackle

import org.lwjgl.input.Keyboard

/**
 * Created by IntelliJ IDEA.
 * User: michael
 * Date: 1/24/12
 * Time: 9:16 PM
 * To change this template use File | Settings | File Templates.
 */

object help {
   val pxScale = Game.pxScale
   def px(x:Int) = x * pxScale
   def px(x:Float) = x * pxScale
   def px(x:vec) = x * pxScale
   def ipx(x:Int) = x / pxScale
   def ipx(x:Float) = x / pxScale
   def ipx(x:vec) = x / pxScale

   def pixelwise( fn: =>Unit ) {
      GLX.pushmatrix {
         GLX.scale(pxScale)
         fn
      }
   }

   def pixelwise( px:Int ):Float = px*pxScale
   def coordwise( x:Float ):Float = x/pxScale
}