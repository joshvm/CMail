package cats.mail.server.manager

import cats.net.core.buffer.{BufferBuilder, Buffer}
import java.io.{FileOutputStream, DataOutputStream, FileInputStream, DataInputStream, File}
import scala.collection.mutable

abstract class Manager[K, V](val name: String) {

  val file: File = new File("./Server/data/%s.dat".format(name))

  protected var map: mutable.Map[K, V] = mutable.Map()

  protected def key(value: V): K

  def ++(value: V){
    ++ (key(value), value)
  }

  def ++(key: K, value: V){
    map += key -> value
    save()
  }

  def --(key: K){
    map remove key
    save()
  }
  
  def get(key: K): V = {
    val opt: Option[V] = map get key
    opt.getOrElse(null.asInstanceOf[V])
  }

  def contains(key: K): Boolean = map contains key

  def load(){
    if(!file.exists)
      return
    val in: DataInputStream = new DataInputStream(new FileInputStream(file))
    if(in.available() < 1){
      in.close()
      return
    }
    val bytes: Array[Byte] = new Array(in.readInt)
    in readFully bytes
    val buf: Buffer = Buffer wrap bytes
    for(i <- 0 until buf.getInt){
      val value: V = buf.getObject[V]
      println(s"Loaded $value")
      map += key(value) -> value
    }
    in.close()
  }

  def save(){
    val bldr: BufferBuilder = new BufferBuilder
    bldr putInt map.size
    map.values foreach bldr.putObject
    val buf = bldr.create
    val out: DataOutputStream = new DataOutputStream(new FileOutputStream(file))
    out writeInt buf.size
    out write buf.array
    out.flush()
    out.close()
  }

}
