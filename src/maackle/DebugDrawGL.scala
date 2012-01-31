package maackle

import org.jbox2d.callbacks.DebugDraw
import org.jbox2d.callbacks.DebugDraw._
import org.lwjgl.opengl.GL11._
import maackle.GLX._
import org.jbox2d.collision.AABB
import org.jbox2d.common.{Transform, Color3f, Vec2, OBBViewportTransform}

object DebugDrawGL extends {
   val viewport = new OBBViewportTransform()
} with DebugDraw(viewport) {
   setFlags(
      e_shapeBit|
      e_jointBit|
      e_aabbBit|
//      e_pairBit|
      e_centerOfMassBit
   )
   viewport.setYFlip(true)
   viewport.setExtents(1f, 1f)
   def drawCircle(center:Vec2, radius:Float, color:Color3f) {
      glPushMatrix()
      glColorB2(color)
      translate(center)
      glScalef(radius, radius, 1)
      Draw.unitCircle(30)
      glPopMatrix()
   }

   def drawPoint(center:Vec2, radius:Float, color:Color3f) {
      glColorB2(color)
      drawCircle(center, radius/100f, color)
   }

   def drawSegment(a:Vec2, b:Vec2, color:Color3f) {
      glColorB2(color)
      Draw.Line((a,b))
   }
   def notimpl() {
      throw new UnsupportedOperationException("DebugDraw function not implemented")
   }
   def drawAABB(argAABB:AABB, color:Color3f) = notimpl()
   def drawSolidCircle(center:Vec2, radius:Float, axis:Vec2, color:Color3f) {
      glColorB2(color)
      GLX.lineWidth(3f)
      drawCircle(center, radius, color)
   }
   def drawSolidPolygon(vertices:Array[Vec2], count:Int, color:Color3f) {
      glColorB2(color)
      GLX.lineWidth(3f)
      begin(GL_POLYGON) {
         Draw.bindVertices(vertices.slice(0,count))
      }
   }
   def drawString(x:Float, y:Float, s:String, color:Color3f) {notimpl()}
   def drawTransform(xf:Transform) {
      Vector(xf.position, vec.polar(10,xf.getAngle))
   }
}