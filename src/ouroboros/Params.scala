package ouroboros

import maackle.help.px
import maackle.vec
import maackle.util
import maackle.GLX.Color

object Params {
   val grabMouse = false
   val enableControllers = false
   val useX360 = false

   val fullscreen = false
   val doSound = true

   val bgColor = Color(0.9f, 0.9f, 0.9f)
   val pxScale = 1/120f
   val dt = 1/60f

   @deprecated("shouldn't need default zoom, zoom=1 should be default")
   val defaultZoom = 30f

   object Box2D {
      val stepTuple = (Params.dt, 10, 7)
      val (dt, positionIterations, velocityIterations) = stepTuple
      val maxPolygonVertices = 8
      val gravity = vec(0, 0)
   }
   object Defaults {
      val density = 1.0f
      val restitution = 0.7f
      val friction = 0.01f
      val angularDamping = 0.01f
      val linearDamping = 0.01f
   }
   object Planet {
      val density = 1f
      val initRadius = px(128f)
      val minRadius = px(75f)
      val gravitationalConstant = 0.5f * Clod.density
   }
   object Cannon {
      val radialOffset = px(20)
      val firepower = 5f
      val barrelLength = px(80)
   }
   object Clod {
      val radius = px(16)
      val density = Planet.density
   }
   object Dragon {
      
   }


   object PlayState {
      val scrollAmt = 0.25f
      val zoomFactor = 0.97f
      val (zoomMin, zoomMax) = (0.0005f, 10000000.1f)
   }
}
