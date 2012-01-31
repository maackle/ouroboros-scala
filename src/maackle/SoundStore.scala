package maackle

import java.io.InputStream
import org.lwjgl.BufferUtils
import org.lwjgl.LWJGLException
import org.lwjgl.openal.AL
import org.lwjgl.openal.AL10
import org.lwjgl.openal.AL10._
import org.lwjgl.util.WaveData
import maackle.util._
import org.newdawn.slick.openal.{OggInputStream, OggDecoder}
import java.nio.{ByteBuffer, FloatBuffer, IntBuffer}
import collection.mutable.ListBuffer

class ALsource() {
   var buffer: IntBuffer = BufferUtils.createIntBuffer(1)
   var sourcebuf: IntBuffer = BufferUtils.createIntBuffer(1)
   var sourcePos: FloatBuffer = BufferUtils.createFloatBuffer(3).put(Array[Float](0.0f, 0.0f, 0.0f))
   var sourceVel: FloatBuffer = BufferUtils.createFloatBuffer(3).put(Array[Float](0.0f, 0.0f, 0.0f))
   sourcePos.flip
   sourceVel.flip
   private var playing_? = false
   def playing = alGetSourcei(id, AL_SOURCE_STATE) == AL_PLAYING
   def check(complaint:String="reality check"):Boolean = {
      import AL10._
      val err = AL10.alGetError
      val reason = err match {
         case AL_NO_ERROR => return true
         case AL_INVALID_NAME => "Invalid name parameter."
         case AL_INVALID_ENUM => "Invalid parameter."
         case AL_INVALID_VALUE => "Invalid enum parameter value."
         case AL_INVALID_OPERATION => "Illegal call."
         case AL_OUT_OF_MEMORY => "Unable to allocate memory."
         case _ => "Unknown error"
      }
      throw new Exception("%s - al error: %s (%d)".format(complaint, reason, err))
   }
   def id:Int = sourcebuf.get(0)
   def loadOgg(is:InputStream) {
      val ois = new OggInputStream(is)
      val bytes = new Array[Byte](ois.getLength)
      ois.read(bytes)
      load(AL10.AL_FORMAT_VORBIS_EXT, ByteBuffer.wrap(bytes), ois.getRate)
   }
   def loadWAV(is:InputStream): ALsource = {
      val wav = WaveData.create(is)
      load(wav.format, wav.data, wav.samplerate)
   }
   private def load(fmt:Int, dat:ByteBuffer, rate:Int):ALsource = {
      AL10.alGenBuffers(buffer)
      check("11")
      AL10.alBufferData(buffer.get(0), fmt, dat, rate)
      check("22")
      AL10.alGenSources(sourcebuf)
      check("33")
      AL10.alSourcei(sourcebuf.get(0), AL10.AL_BUFFER, buffer.get(0))
      check("44")
      AL10.alSourcef(sourcebuf.get(0), AL10.AL_PITCH, 1.0f)
      AL10.alSourcef(sourcebuf.get(0), AL10.AL_GAIN, 1.0f)
      AL10.alSource(sourcebuf.get(0), AL10.AL_POSITION, sourcePos)
      AL10.alSource(sourcebuf.get(0), AL10.AL_VELOCITY, sourceVel)
      check("55")
      this
   }

   private def setf(attr:Int, v:Float) = AL10.alSourcef(sourcebuf.get(0), attr, v)
   private def seti(attr:Int, v:Int) = AL10.alSourcei(sourcebuf.get(0), attr, v)

   def gain(v:Float):ALsource = { setf(AL10.AL_GAIN, v); this}
   def loop(v:Boolean):ALsource = { seti(AL10.AL_LOOPING, if(v) 1 else 0); this}
   def play(restart:Boolean=false) {
      if(restart || !playing) {
         AL10.alSourcePlay(sourcebuf.get(0))
         playing_? = true
      }
   }
   def pause() {
      AL10.alSourcePause(sourcebuf.get(0))
      playing_? = false
   }
   def stop() {
      AL10.alSourceStop(sourcebuf.get(0))
      playing_? = false
   }
   def destroy() {
      stop()
      AL10.alDeleteSources(sourcebuf)
      AL10.alDeleteBuffers(buffer)
   }
}

object NullSource extends ALsource {
   override def playing = false
   override def check(complaint:String="reality check") = false
   override def id:Int = -1
   override def loadOgg(is:InputStream) {}
   override def loadWAV(is:InputStream) = this
   override def play(restart:Boolean=false) {}
   override def pause() {}
   override def stop() {}
   override def destroy() {}
   override def gain(g:Float) = this
   override def loop(v:Boolean):ALsource = { this}
   override def toString = "NullSource"
}

class SoundStore(private val nullify:Boolean=false) {
//   var buffer: IntBuffer = BufferUtils.createIntBuffer(1)
//   var sourcebuf: IntBuffer = BufferUtils.createIntBuffer(1)

   private var sourcebank = Map[ String, ALsource ]()
   private var sourceids = Map[ String, Int ]()

   def apply(id:String):ALsource = if(SoundStore.enabled) sourcebank(id) else NullSource
   def source(id:String):ALsource = if(SoundStore.enabled) sourcebank(id) else NullSource
   def sources:Iterable[ALsource] = sourcebank.values

   def initialized = SoundStore.initialized_?

   SoundStore.all += this

   def addSource(id:String, path:String, loop:Boolean=false): ALsource = {
      if(!SoundStore.enabled) return NullSource
      assert(initialized)
      val pathPattern(dir, file, ext) = path
      val s = if(nullify) NullSource else new ALsource()

      try {
         val stream = getStream(path)
         ext.toUpperCase match {
            case "WAV" => s.loadWAV(stream)
            case "OGG" => s.loadOgg(stream)
         }
         s.loop(loop)
      }
      catch {
         case e:NullPointerException => println("couldn't load sound clip %s".format(path))
      }
      sourcebank += id -> s
      sourceids += id -> s.id
      return s
   }

   def addNull(id:String): ALsource = {
      if(!SoundStore.enabled) return NullSource
      sourcebank += id -> NullSource
      sourceids += id -> -1
      return NullSource
   }
   
   def playAll() { if(!SoundStore.enabled) return; sourceids.values foreach(AL10.alSourcePlay(_)) }
   def pauseAll() { if(!SoundStore.enabled) return; sourceids.values foreach(AL10.alSourcePause(_)) }
   def stopAll() { if(!SoundStore.enabled) return; sourceids.values foreach(AL10.alSourceStop(_)) }

   def destroy() {
      if(!SoundStore.enabled) return
      sources.foreach(_.destroy())
      SoundStore.all -= this
   }
}


object SoundStore {
   val all = ListBuffer[SoundStore]()
   var listenerPos: FloatBuffer = BufferUtils.createFloatBuffer(3).put(Array[Float](0.0f, 0.0f, 0.0f))
   var listenerVel: FloatBuffer = BufferUtils.createFloatBuffer(3).put(Array[Float](0.0f, 0.0f, 0.0f))
   /**Orientation of the listener. (first 3 elements are "at", second 3 are "up")
      Also note that these should be units of '1'. */
   var listenerOri: FloatBuffer = BufferUtils.createFloatBuffer(6).put(Array[Float](0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f))
   listenerPos.flip
   listenerVel.flip
   listenerOri.flip
   private var initialized_? = false
   val oggDecoder = new OggDecoder
   def init() {
      if(!SoundStore.enabled) return
      try {
         AL.create(null, 44100, 15, true);
      }
      catch {
         case le: LWJGLException => {
            le.printStackTrace()
            return
         }
      }
      initialized_? = true
      AL10.alGetError
      AL10.alListener(AL10.AL_POSITION, listenerPos)
      AL10.alListener(AL10.AL_VELOCITY, listenerVel)
      AL10.alListener(AL10.AL_ORIENTATION, listenerOri)
      enabled = true
   }
   private var enabled = true
   def disable() {
      enabled = false
   }
   def destroyAll() {
      for(s <- all) {
         s.stopAll()
         s.destroy()
      }
   }
}