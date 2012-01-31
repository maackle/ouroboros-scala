package maackle

import org.jbox2d.dynamics._
import traits.ActiveSwitch

package object b2d {



   class A(x:Int)(q:Int) {
      def this(y:Int, z:Int)(q:Int) = this({
         val x = y+z
         x
      })(q)
   }


}