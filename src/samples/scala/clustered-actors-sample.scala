package samples

import java.util.concurrent._
import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.mutable._

// Messages
case object Tick
case class AddItem(desc: String)

// Main runner
object ClusteredActorsSample {
  def main(args: Array[String]) {
    val cart = Cart.newInstance("myId")//new Cart 
    cart.start
    cart.ping
    for (i <- 1 to 100) {
      java.lang.Thread.sleep(2000L)
      cart ! AddItem("Item " + i)      
    }
  }
}

/**
 * Cart actor, responds to:
 * <p>-- Tick - prints out the list of current items
 * <p>-- AddItem - adds an item to the list of current items
 */
class Cart extends Actor {
  private[this] var items: List[String] = Nil

  def act() {
    loop {
      react {
        case AddItem(item) => 
          items = item :: items
        case Tick => 
          println("Items: " + items)
      }
    }
  }

  def ping = {
    ActorPing.scheduleAtFixedRate(this, Tick, 0L, 5000L)
  }
}

object Cart {
  private[this] val instances: Map[Any, Cart] = new HashMap

  def newInstance(id: Any): Cart = {
    instances.get(id) match {
      case Some(cart) => cart
      case None => 
        val cart = new Cart
        instances += (id -> cart)
        cart
    }
  }
}

// =============================================
/**
 * Pings an actor every X seconds.
 * 
 * Code based on code from the ActorPing class in the /lift/ repository (http://liftweb.net).
 * Copyright: 
 *
 * (c) 2007 WorldWide Conferencing, LLC
 * Distributed under an Apache License
 * http://www.apache.org/licenses/LICENSE-2.0
 */
object ActorPing { 

  def scheduleAtFixedRate(to: Actor, msg: Any, initialDelay: Long, period: Long): ScheduledFuture = {
    val cmd = new Runnable { def run { to ! msg } }
    service.scheduleAtFixedRate(cmd, initialDelay, period, TimeUnit.MILLISECONDS)
  }
  
  private val service = Executors.newSingleThreadScheduledExecutor(threadFactory)  
  
  private object threadFactory extends ThreadFactory {
    val threadFactory = Executors.defaultThreadFactory()
    def newThread(r: Runnable) : Thread = {
      val d: Thread = threadFactory.newThread(r)
      d setName "ActorPing"
      d setDaemon true
      d
    }
  }
}
