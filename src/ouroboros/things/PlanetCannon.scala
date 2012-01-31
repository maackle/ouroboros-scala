package ouroboros.things

import org.jbox2d.dynamics._
import maackle.help._
import maackle.gfx.Tex
import maackle._
import stateful.SFloat
import ouroboros.{BodyFactory, Params, Draw}
import org.lwjgl.input.Keyboard
import maackle.util.Countdown
import org.jbox2d.collision.shapes.{MassData, CircleShape}
import traits.{CircularBody, Bodied, MonoBodied}

object Planet extends  MonoBodied with CircularBody with Thing  {
   import Params.Planet._
   val cannons = new Cannon(0f) :: Nil
   val tex = new Tex("img/home.png")
   val massData = new MassData

//   def radius:Float = math.sqrt(mass / density) / math.Pi toFloat
   def world = body.getWorld

   def update() {
      radius = math.sqrt(mass / density / math.Pi) toFloat
      val t = Game().steps / 100f
      for(c <- cannons) c.update()
   }

   def render() {
      GLX.pushmatrix {
         tex.render(position, angle, radius/initRadius)
         for(c <- cannons) c.render()
//         GLX.Color.red.bind()
//         GLX.fill(false)
//         Draw.circle(position, radius)
      }
   }

   def doGravity(o:MonoBodied) {
      val p = o.position - position
      val r = p.length
      val grav = -p * (mass * gravitationalConstant / (r*r*r) )
      o.body.applyForce(grav, p)
   }

   def changeMass(amt:Float) {
      body.getMassData(massData)
      massData.mass += amt
      body.setMassData(massData)
   }

   def createBody(world:World) = BodyFactory.Planet()(world)

   object Cannon {
      lazy val texBack = new Tex("img/cannon-back.png", offset=vec(55,57))
      lazy val texFront = new Tex("img/cannon-front.png", offset=vec(55,57))
      lazy val texMain = new Tex("img/cannon.png", offset=vec(23,24))
   }
   class Cannon(protected var angularPos:Float) extends Thing {
      import Params.Cannon._
      val angleMem = SFloat(2)
      val shotClock = Countdown(10)

      var shotAngle:Float = 0f //TODO

      def position = vec.polar(radialDistance, angularPos)
      def barrelTip = position + vec.polar(barrelLength, shotAngle + angularPos)
      def angularVelocity = angleMem.now - angleMem.prev
      def radialDistance = planet.radius + Params.Cannon.radialOffset
      def planet = Planet.this

      def input() {
         import Keyboard._
         if(isKeyDown(KEY_A))
            angularPos += 0.1f
         if(isKeyDown(KEY_D))
            angularPos -= 0.1f
         if(isKeyDown(KEY_Q))
            shotAngle += 0.02f
         if(isKeyDown(KEY_E))
            shotAngle -= 0.02f
         if(isKeyDown(KEY_SPACE) && shotClock.isExpired) {
            shotClock.set()
            shoot(firepower)
         }
         Input.Mice
         shotClock.tick()
      }

      def update() {
         angleMem <= angularPos
         input()
      }

      def render() {
         GLX.pushmatrix {
            Cannon.texBack.render(position, -angularPos)
            Cannon.texMain.render(position, -angularPos - shotAngle)
            Cannon.texFront.render(position, -angularPos)
         }
      }

      def shoot(speed:Float) {
         println(Planet.radius, minRadius)
         if(Planet.radius < minRadius) return
         val shaft = barrelTip - position
         var vel = shaft.unit * speed
//         vel += position.unit.rotated(math.Pi / 2) * (shotAngle + angularPos) * position.length * angularPos
         new Clod(barrelTip, vel)(planet.world)
      }
   }
}

