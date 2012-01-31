package ouroboros

import org.lwjgl._
import opengl.GL11
import maackle.GLX._
import opengl.GL11._
import java.nio.{FloatBuffer, ByteOrder, ByteBuffer}
import maackle.vec

object Draw extends maackle.PrimitiveDrawing {

   def enableLight() {
      val temp:ByteBuffer = ByteBuffer.allocateDirect(16);
      temp.order(ByteOrder.nativeOrder());
      val lightAmbient = Array(0.2f, 0.2f, 0.2f, 1f)
      val lightDiffuse =
         Array(0f, 0.0f, 0.0f, 0.0f)
//         Array(1.0f, 1.0f, 1.0f, 1.0f)
      val lightSpecular =
         Array(0f, 0.0f, 0.0f, 0.0f)
//         Array(1.0f, 1.0f, 1.0f, 1.0f)
      val lightPosition = Array(0.0f, 0.0f, 1.0f, 1.0f)
      GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, temp.asFloatBuffer().put(lightAmbient).flip().asInstanceOf[FloatBuffer]);
      GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, temp.asFloatBuffer().put(lightDiffuse).flip().asInstanceOf[FloatBuffer]);
      GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION,temp.asFloatBuffer().put(lightPosition).flip().asInstanceOf[FloatBuffer]);
      GL11.glEnable(GL11.GL_LIGHT1);
      GL11.glEnable(GL11.GL_LIGHTING);
   }

   def Text(what:String, pos:vec) {

   }
}