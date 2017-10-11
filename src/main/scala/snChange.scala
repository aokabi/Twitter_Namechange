import java.util.Properties

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import twitter4j._


/*
object snChange {
  def main(args: Array[String]): Unit = {
    val tf: TwitterFactory = new TwitterFactory()
    val twitter: Twitter = tf.getInstance()

    val user: User = twitter.verifyCredentials()

    val description = twitter.showUser("aokabit").getDescription()
    val url = twitter.showUser("aokabit").getURL()
    val location = twitter.showUser("aokabit").getLocation

    val text = twitter.getMentionsTimeline.get(0).getText
    println(text)
    if(text.contains("Your name is")) {
      println(twitter.updateProfile(text.split(' ')(4), url, location, description))
      var status = twitter.updateStatus("@aokabit My name is " + twitter.showUser("aokabit").getName)
    }
  }
}
*/

/**
  * MysqlSettings
  */
case class SettingData(servername: String, databasename: String, mysqlUser: String, password: String)

/**
  * Created by aokabi on 2016/12/30.
  */

object AsyncSnChange {
  
  
  def main(args: Array[String]): Unit = {
    
    val tf: TwitterFactory = new TwitterFactory()
    val twitter: Twitter = tf.getInstance()
    //使っていない
    val user: User = twitter.verifyCredentials()
    
    val prop = new Properties
    prop.load(this.getClass().getClassLoader().getResourceAsStream("settings.properties"))
    val settingData = SettingData(
      prop.getProperty("servername"),
      prop.getProperty("databasename"),
      prop.getProperty("user"),
      prop.getProperty("password")
    )
    val mysql = new Jdbc(settingData)
    
    // TwitterStreamのインスタンスを作ります
    val twitterStream: TwitterStream = new TwitterStreamFactory().getInstance();
    // リスナーを作ります
    val listener: StatusListener = new StatusListener() {
      override def
      onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = {
        // ツイートが削除された時に通知されるようです
        // 今回は無視します
      }
      
      override def
      onScrubGeo(userId: Long, upToStatusId: Long) {
        // ツイートの位置情報が削除された場合に通知されるようです
        // 今回は無視します
      }
      
      override def
      onStatus(status: Status): Unit = {
        // ツイートされた時に通知されるようです
        // 今回はコンソールにscreen_nameと内容を出力します
        //println(status.getUser().getScreenName() + " : " + status.getText())
        
        //TO
        
        val text = status.getText().replaceAll("your", "Your")
        val keywordIndex = if (text contains "@aokabit Your name is") text.split(" ") else null
        if (keywordIndex != null) {
          mysql.Connect()
          
          val name = keywordIndex(keywordIndex.indexOf("is") + 1)
          twitter.updateProfile(
            name,
            twitter.showUser("aokabit").getURL(),
            twitter.showUser("aokabit").getLocation,
  
            twitter.showUser("aokabit").getDescription()
          )
          twitter.updateStatus("My name is " + twitter.showUser("aokabit").getName)
          println(status.getUser().getScreenName())
          mysql.Insert(status, name.asInstanceOf[String])
        }
          // ガチャガチャ
        else if (text contains "ガチャ") {
          mysql.Connect()
          mysql.Gacha() match {
            case Some(x) => twitter.updateStatus(
              s"""|${new DateTime().getMillis}
                  |"${x.name}" by ${x.godparentUser} at ${x.datetime.toString(DateTimeFormat.longDateTime())}""".stripMargin)
          }
          
        }
      }
      
      override def
      onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = {
        // よくわからないけど、ツイート？トラック？の制限が変わった時に通知されるっぽい
        // 今回は無視します
      }
      
      override def
      onException(e: Exception): Unit = {
        // 例外が起こった場合に通知されます
        // 今回はスタックトレースでも出しておきます
        e.printStackTrace()
      }
      
      override def onStallWarning(warning: StallWarning): Unit = ???
    }
    // リスナーを登録します
    twitterStream.addListener(listener)
    twitterStream.filter("@aokabit")
  }
}
  