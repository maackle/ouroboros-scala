package maackle.stateful

import collection.mutable.Queue

/**
 * Created by IntelliJ IDEA.
 * User: michael
 * Date: 12/6/11
 * Time: 3:45 PM
 * To change this template use File | Settings | File Templates.
 */

class Stateful[T](size:Int, default:T) {
   protected var history = Queue[T]()

   for(i <- 1 to size+1) history.enqueue(default)

   def now = history.last
   def prev = history(size-1)
   protected def x = now
   protected def y = prev
   def <=(v:T) = {
      history.enqueue(v)
      history.dequeue()
   }
   protected def update(v:T) = <=(v)

   override def toString = "[Stateful (now=%s prev=%s)(%s)]".format(now, prev, history)
}

case class SBoolean() extends Stateful(10, false) {

   def xor = x && !y || y && !x
}

case class SInt(size:Int) extends Stateful(size, 0)
case class SFloat(size:Int) extends Stateful(size, 0f)
case class SDouble(size:Int) extends Stateful(size, 0.0)
