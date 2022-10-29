package dgtc.common.genid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

@Repository
public class GenIDDAO {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  private String selectBufferSizeSql;
  private String selectLastDateCountSql;
  private String updateLastDateCountSql;
  private String updateCountSql;
  private String selectNowSql;

  private GenIDConfig config;

  @Autowired
  public GenIDDAO(GenIDConfig config) {
    selectBufferSizeSql =
        "SELECT buffer_size FROM " + config.configTable + " WHERE id=" + config.id;
    selectLastDateCountSql =
        "SELECT last_date, count FROM "
            + config.dataTable
            + " WHERE id="
            + config.id
            + " FOR UPDATE";
    updateLastDateCountSql = "UPDATE " + config.dataTable + " SET last_date=?, count=? WHERE id=0";
    updateCountSql = "UPDATE " + config.dataTable + " SET count=? WHERE id=0";
    selectNowSql = "SELECT now() as now";
    this.config = config;
  }

  public GetRangeIDResult getRangeID() throws Exception {
    GetRangeIDResult result = new GetRangeIDResult();
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    int size = 0;
    int start = 0;
    Date lastDate = null;
    Date now = null;
    Date today = null;

    try {
      Class.forName(config.driverClassName);
      conn = DriverManager.getConnection(config.jdbcUrl);

      conn.setAutoCommit(false);

      preparedStatement = conn.prepareStatement(selectBufferSizeSql);
      resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        size = resultSet.getInt(1);
      }

      preparedStatement = conn.prepareStatement(selectLastDateCountSql);
      resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        lastDate = resultSet.getDate(1);
        start = resultSet.getInt(2);
      }

      preparedStatement = conn.prepareStatement(selectNowSql);
      resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        now = resultSet.getTimestamp(1);
        today = DATE_FORMAT.parse(DATE_FORMAT.format(now));
      }

      result.pDateTime = now;

      if (lastDate.compareTo(today) > 0) {
        conn.commit();
        result.pResult = 0;
        return result;
      }

      if (lastDate.compareTo(today) < 0) {
        lastDate = (Date) today.clone();
        start = 0;
        preparedStatement = conn.prepareStatement(updateLastDateCountSql);
        preparedStatement.setDate(1, new java.sql.Date(lastDate.getTime()));
        preparedStatement.setInt(2, (start + size));
        int effectRow = preparedStatement.executeUpdate();
        if (effectRow < 1) {
          conn.commit();
          result.pResult = 0;
          return result;
        }
      } else {
        preparedStatement = conn.prepareStatement(updateCountSql);
        preparedStatement.setInt(1, (start + size));
        int effectRow = preparedStatement.executeUpdate();
        if (effectRow < 1) {
          conn.commit();
          result.pResult = 0;
          return result;
        }
      }

      conn.commit();
      result.pDate = lastDate;
      result.pStart = start;
      result.pSize = size;
      result.pResult = 1;

    } catch (Exception ex) {
      throw ex;
    } finally {
      try {
        if (resultSet != null) {
          resultSet.close();
        }

        if (conn != null) {
          conn.close();
        }

      } catch (Exception ex) {
        throw ex;
      }
    }
    return result;
  }
}
