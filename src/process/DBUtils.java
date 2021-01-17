package process;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtils {
	public String user = "root";
	public String password = "123456";
	public String driver = "com.mysql.jdbc.Driver";
	public String url = "jdbc:mysql://localhost:3306/fga";
	public String host = "";
	public String port = "";
	public String dbName = "";
	public Connection conn = null;

	public DBUtils()
	{
		
	}
	
	public DBUtils(String user, String password, 
			String host, String port, String dbName) {
		super();
		this.user = user;
		this.password = password;
		this.host = host;
		this.port = port;
		this.dbName = dbName;
		setConnectionString(host,port,dbName,
				user, password);
	}

	protected void setConnectionString(String host, String port, String dbName,
			String user, String password) {
		this.url = "jdbc:mysql://" + host + ":" + port + "/" + dbName
				+ "?user=" + user + "&password=" + password;
	}

	public Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password);
			if (conn != null) {
				System.out.println("连接数据库成功!");
			}
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public ResultSet excuteSql(String sql) {
		ResultSet rs = null;
		try {
			if (conn != null) {
				Statement stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
			} else {
				conn = getConnection();
				Statement stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				return rs;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

}
