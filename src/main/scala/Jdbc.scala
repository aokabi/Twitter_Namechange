import org.joda.time.DateTime
import scalikejdbc._
import twitter4j.Status


/**
  * Created by aokabi on 2017/09/23.
  */


class Jdbc(settingData: SettingData) {
  
  def Connect() {
    ConnectionPool.singleton(
      url = s"jdbc:mysql://${settingData.servername}/${settingData.databasename}?characterEncoding=UTF-8",
      settingData.mysqlUser,
      settingData.password
    )
  }
  
  def Insert(status: Status, name: String): Unit = {
    val (parentName, createdAt) = (status.getUser().getScreenName(), DateTime.now)
    DB.localTx { implicit session =>
      sql"INSERT INTO snChange (godparent_user, my_name, dt) values (${parentName},${name},${createdAt} )".update.apply()
    }
  }
  
  def Gacha(): Option[NamingData]={
    val * = (rs: WrappedResultSet) => NamingData(rs.string("godparent_user"), rs.string("my_name"), rs.jodaDateTime("dt"))
    DB.readOnly { implicit session =>
      sql"SELECT * FROM snChange ORDER BY RAND() LIMIT 1".map(*).single.apply()
    }
  }
}

case class NamingData(godparentUser: String, name: String, datetime: DateTime)