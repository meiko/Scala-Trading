package uk.co.tradingdevelopment.trading.scala.rss

import scala.xml._
import java.net.{URL, URLDecoder}
import java.text.SimpleDateFormat
import java.util.{Date, Locale}

import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{Actor, ActorSystem, Props}
import akka.event.Logging
import akka.pattern.ask

import scala.concurrent._
import scala.util.{Failure, Success, Try}
import java.io._

import org.htmlcleaner.HtmlCleaner

import scala.collection.immutable

case class RssItem(key:String,description:String, link:String)


object RssStreamer{
  lazy val cleaner = new HtmlCleaner
  val props = cleaner.getProperties
  props.setOmitComments(true)

  props.setPruneTags("script,style,img")

  props.setAllowHtmlInsideAttributes(false)

  def getRss(url:String):Vector[RssItem] =  Try(getItems(loadUrl(url)).map(resolveNode).toVector) match {
    case Success(vn) => vn.flatten.map(r => {
      val cleanTag = cleaner.clean(r.description)

      RssItem(r.key,cleanTag.getText.toString,r.link)
    })
    case Failure(ex) => throw ex
  }


private def resolveNode(nodeSeq:NodeSeq) = nodeSeq.toVector.map(n =>   RssItem("RSS" ,(n \ "title").text + " " + (n \ "description").text,(n \ "link").text ))
private def getItems(xml:Node): Vector[NodeSeq] = Try{

  for (channel <- xml \\ "channel") yield {
    for (item <- (channel \\ "item")) yield item
  }
} match {
  case Success(x) => x.toVector
  case Failure(ex) => throw ex
}
  private def loadUrl(url:String) =Try( XML.load(new URL(url))) match {
    case Success(e) =>e
    case Failure(ex) => throw ex
  }
}

class RssStreamer(interval:Int, rssFeeds:Vector[String]) extends PollingRxStreamer[RssItem](interval,Some(r => r.description.take(25))){
  override protected def getData: Vector[RssItem] = for {
    feed <- rssFeeds
    item <- RssStreamer.getRss(feed)
  } yield item

}


//
//class RssStreamer() extends Reader {
//
//  val dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
//
//  private def parseAtomDate(date:String, formatter:SimpleDateFormat):Date = {
//    val newDate = date.reverse.replaceFirst(":", "").reverse
//    return formatter.parse(newDate)
//  }
//
//  private def getHtmlLink(node:NodeSeq) = {
//    node
//      .filter(n => (n \ "@type").text == "text/html")
//      .map( n => (n \ "@href").text).head
//  }
//
//  def extract(xml:Elem) : Seq[RssFeed] = {
//    for (feed <- xml \\ "feed") yield {
//      val items = for (item <- (feed \\ "entry")) yield {
//        RssItem(
//          (item \\ "title").text,
//          getHtmlLink((item \\ "link")),
//          (item \\ "summary").text,
//          parseAtomDate((item \\ "published").text, dateFormatter),
//          (item \\ "id").text
//        )
//      }
//      AtomRssFeed(
//        (feed \ "title").text,
//        getHtmlLink((feed \ "link")),
//        (feed \ "subtitle ").text,
//        items.take(8))
//    }
//  }
//
//  def receive() = {
//    case xml:Elem => {
//      extract(xml) match {
//        case head :: tail => print(head)
//        case Nil =>
//      }
//    }
//  }
//}
//
//class XmlReader extends Reader {
//
//  val dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
//
//  def extract(xml:Elem) : Seq[RssFeed] = {
//
//    for (channel <- xml \\ "channel") yield {
//      val items = for (item <- (channel \\ "item")) yield {
//        RssItem(
//          (item \\ "title").text,
//          (item \\ "link").text,
//          (item \\ "description").text,
//          dateFormatter.parse((item \\ "pubDate").text),
//          (item \\ "guid").text
//        )
//      }
//      XmlRssFeed(
//        (channel \ "title").text,
//        (channel \ "link").text,
//        (channel \ "description").text,
//        (channel \ "language").text,
//        items.take(8))
//    }
//  }
//
//  def receive() = {
//    case xml:Elem => {
//      extract(xml) match {
//        case head :: tail => print(head)
//        case Nil =>
//      }
//    }
//  }
//}
//
//
//class RssReader extends Actor {
//  val log = Logging(context.system, this)
//
//  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
//    val p = new java.io.PrintWriter(f)
//    try { op(p) } finally { p.close() }
//  }
//
//  def read(url : URL) = {
//    Try(url.openConnection.getInputStream) match {
//      case Success(u) => {
//        val xml = XML.load(u)
//        implicit val timeout = Timeout(30.seconds)
//        val actor = if((xml \\ "channel").length == 0) context.actorOf(Props[AtomReader])
//        else context.actorOf(Props[XmlReader])
//        actor ! xml
//      }
//      case Failure(_) =>
//    }
//  }
//
//  def receive() = {
//    case path:URL => read(path)
//  }
//}
//
//class SubscriptionReader extends Actor {
//
//  def open(filename:String) = XML.loadFile(filename)
//
//  def read(xml:Elem):Seq[URL] = {
//    for {
//      node <- (xml \\ "@xmlUrl")
//    } yield new URL(node.text)
//  }
//
//  def receive() = {
//    case filename:String => {
//      sender ! read(open(filename))
//    }
//  }
//}