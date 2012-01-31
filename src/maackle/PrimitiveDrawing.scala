package maackle

import org.lwjgl.opengl.GL11._
import math._
import types._
import GLX._

object Draw extends Draw
class Draw extends PrimitiveDrawing

trait PrimitiveDrawing {
   protected var circlepts:Map[ Int, VertexList ] = Map()

   @inline
   @deprecated("Use GLX.fill")
   def fill(yes:Boolean) {
      if (yes) glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)
      else glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
   }

   @inline
   def bindVertices(verts:Seq[vec]) {
      for(v <- verts) vertex(v)
   }
   @inline
   def bindVertices(verts:VertexList) {
      for(v <- verts) { vertex(v)

      }
   }
   @inline
   def bindVertices(verts:VecList) {
      for(v <- verts) vertex(v)
   }

   @inline
   def getCircle(num:Int):VertexList = {
      if (!circlepts.isDefinedAt(num)) {
         circlepts += num -> {
            for (i:Int <- Array.range(0, num)) yield  ( cos(2.0*Pi*i/num) , sin(2.0*Pi*i/num) )
         }
      }
      circlepts(num)
   }

   def unitCircle(num:Int=100) {
      if (!circlepts.isDefinedAt(num)) {
         circlepts += num -> {
            for (i:Int <- Array.range(0, num)) yield  ( cos(2.0*Pi*i/num) , sin(2.0*Pi*i/num) )
         }
      }
      begin(GL_POLYGON) {
         bindVertices(circlepts(num))
      }
   }

   def circle(center:vec=null, radius:Float, num:Int=0) {
      glPushMatrix()
      if(center!=null) translate(center)
      glScalef(radius, radius, 1)
      unitCircle(if(num>0) num else 16)
      glPopMatrix()
   }
   
   def Quad(width:Float, height:Float) {
      glBegin(GL_QUADS)
      glVertex2f(-width/2, -height/2)
      glVertex2f(+width/2, -height/2)
      glVertex2f(+width/2, +height/2)
      glVertex2f(-width/2, +height/2)
      glEnd()
   }
   def Quad(a:vec, b:vec) {
      glBegin(GL_QUADS)
      glVertex2f(a.x, a.y)
      glVertex2f(a.x, b.y)
      glVertex2f(b.x, b.y)
      glVertex2f(b.x, a.y)
      glEnd()
   }

   def Points(points: VertexList) {
      glBegin(GL_POINTS)
      for (v <- points) vertex(v)
      glEnd()
   }
   def Line(pair:(vec, vec)) {
      glBegin(GL_LINES)
      glVertex(pair._1)
      glVertex(pair._2)
      glEnd()
   }
   def Line(v1:vec, v2:vec) {
      glBegin(GL_LINES)
      vertex(v1)
      vertex(v2)
      glEnd()
   }

   def Vector(origin:vec, to:vec) {
      Line(origin, origin+to)
   }
   def Vector(pair:(vec,vec)) {
      Line((pair._1, pair._1 + pair._2))
   }
}