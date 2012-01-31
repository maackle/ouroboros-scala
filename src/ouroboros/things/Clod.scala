package ouroboros.things

import org.jbox2d.dynamics.World
import maackle.gfx.Tex
import maackle.help._
import maackle.{GLX, Game, vec}
import ouroboros.BodyFactory
import collection.mutable.ListBuffer
import org.jbox2d.collision.shapes.MassData
import maackle.traits.{CircularBody, MonoBodied}

object Clod {
   lazy val tex = new Tex("img/clod.png")
   val all = ListBuffer[Clod]()
}


class Clod(pos:vec, vel:vec)(implicit val world:World) extends MonoBodied with CircularBody with Thing  {

   birth()

   def birth() {
      pose()
      Clod.all += this
      Planet.changeMass(-mass)
   }

   def death() {
      if((position - Planet.position).length < Planet.radius + radius)
         Planet.changeMass(+mass) // return to mama
      Clod.all -= this
      depose()
   }

   def update() {
      if((position - Planet.position).length < Planet.radius + radius) {
         death()
      }
      Planet.doGravity(this)
   }

   def render() {
      Clod.tex.render(position, angle)
   }

   def createBody(world:World) = BodyFactory.Clod(pos, vel)(world)
}