package ouroboros

import org.newdawn.slick._
import states._StateTemplate
import maackle.GameState
import org.lwjgl.opengl.{PixelFormat, DisplayMode, Display}
import org.lwjgl.opengl.GL11._

//
//object SlickGame {
//
//   def main(args: Array[String]): Unit = {
//      val game = new SlickGame
//      maackle.Game.game = game
//      game.run()
//      game.cleanup()
//   }
//}
//
//class SlickGame extends {
//
//   val title="MaackleGame"
//
//} with BasicGame(title) with maackle.Game {
//
//   val fullscreen = false
//   val windowSize = (800,600)
//   val framerate = 60
//   val pxScale = Params.pxScale
//
//   def run() {
//      try {
//         var app: AppGameContainer = new AppGameContainer(this, windowSize._1, windowSize._2, fullscreen)
//         app.start
//      }
//      catch {
//         case e: SlickException => {
//            e.printStackTrace
//         }
//      }
//   }
//
//   def init(container: GameContainer): Unit = {
//
//		Display.setVSyncEnabled(true)
//		if(fullscreen) Display.setFullscreen(fullscreen)
//      else Display.setDisplayMode(new DisplayMode(windowSize._1, windowSize._2))
////		Display.create(new PixelFormat(8, 16, 4))
//      Display.sync(60)
//
//      glEnable (GL_BLEND);
//      glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//
//      glMatrixMode(GL_PROJECTION)
//      glLoadIdentity()
//      glOrtho(-width/2,width/2, -height/2, height/2,-1,1)
//
//      GameState.init(new PlayState)
//   }
//
//   def update(container: GameContainer, delta: Int): Unit = {
//
//   }
//
//   def render(container: GameContainer, g: Graphics): Unit = {
////      Display.update()
//      update()
//      calcFPS()
//      state.tick()
//      steps+=1
//      Display.sync(framerate)
//
//
//
//      if(finished) container.exit()
//   }
//}