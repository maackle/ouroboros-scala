package ouroboros

import maackle.vec
import maackle.traits.{MaackleBase}
import org.jbox2d.dynamics.{BodyType, BodyDef, World}

package object things {

   ///This is meant to be the base class for all game objects
   trait Thing extends  MaackleBase {
      // add additional attributes
   }

}