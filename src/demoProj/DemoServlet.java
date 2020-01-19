package demoProj;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DemoServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3146486870152830705L;

	private static String dbUser = "";
	private static String dbPassword = "";
	private static String dbAddr = "127.0.0.1";
	private static String dbPort = "3306";
	private static String dbName = "";
	private static int number = 10020;
	private static String datenow = "2019-01-03";

	{
		try {
			Class.forName("com.mysql.jdbc.Driver");

			Properties prop = new Properties();
			//读取属性文件a.properties
			//InputStream in = new BufferedInputStream (new FileInputStream(new File("D:\\workspace\\demoProj\\config\\demodb.properties")));
			URL path = this.getClass().getClassLoader().getResource("/");
			InputStream in = new BufferedInputStream (new FileInputStream(new File(path.getPath() + "config/demodb.properties")));
			prop.load(in);
			dbUser = prop.getProperty("MYSQL_USER");//"root";
			dbPassword = prop.getProperty("MYSQL_ROOT_PASSWORD");//"123456";
			dbAddr = prop.getProperty("MYSQL_ADDR");//"miaoyun-demo-db-svc";
			dbPort = prop.getProperty("MYSQL_PORT");//"3306";
			Map<String,String> map = System.getenv();
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				String key = it.next();
				System.out.println("get env key : " + key);
				System.out.println("get env value : " + map.get(key));
				// if (key.equals("MYSQL_ADDR")) {
				// 	dbAddr = map.get(key);
				// 	System.out.println("get dbAddr : " + dbAddr);
				// }

				// if (key.equals("MYSQL_PORT")) {
				// 	dbPort = map.get(key);
				// 	System.out.println("get dbPort : " + dbPort);
				// }

				if (key.equals("MYSQL_ROOT_PASSWORD")) {
					dbPassword = map.get(key);
					System.out.println("get dbPassword : " + dbPassword);
				}
			}
			dbName = prop.getProperty("MYSQL_NAME");//"demo";

			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("into Servlet!!");
		
		try {
			String mysqlDriver = "jdbc:mysql://" + dbAddr  + ":" + dbPort + "/" + dbName + "?user=" + dbUser + "&password=" + dbPassword;
			System.out.println("Mysql driver : " + mysqlDriver);
			Connection conn = DriverManager.getConnection(mysqlDriver);// localhost:主机名，端口号；Garbage:数据库database；Mysql帐户、密码
			// 获取支持sql的statement
			Statement st = conn.createStatement();
			// 根据写的sql语句查询结果到ResultSet集合中去
			String sql = "SELECT * FROM demo_data";
			ResultSet set = st.executeQuery(sql);
			PrintWriter pw = response.getWriter();
			// 定义一个表头
			InetAddress netAddress = getInetAddress();
			pw.println("<table><tr>"
					+ "<td>Version</td>"
					+ "<td>v2</td></tr>"
					+ "<tr><td>Host IP</td>"
					+ "<td>" + getHostIp(netAddress) + "</td></tr>"
					+ "<tr><td>Host Name</td>"
					+ "<td>" + getHostName(netAddress) + "</td></tr>"
					+ "<tr><td>Database Address</td>"
					+ "<td>" + dbAddr + "</td>"
					+ "</tr></table><br>");
			pw.println("<div class='show'>");
			pw.println("<table><thead><tr>"
					+ "<th>ID</th>"
//					+ "<td>Birth Date</td>"
					+ "<th>Host</th>"
					+ "<th>Name</th>"
//					+ "<td>Gender</td>"
//					+ "<td>Hire Date</td>"
					+ "</tr></thead>");
			while (set.next()) {
				int empNo = set.getInt(1);
				number = empNo;
				String birthDate = set.getString(2);
				String firstName = set.getString(3);
				String lastName = set.getString(4);
				String gender = set.getString(5);
				String hireDate = set.getString(6);
				pw.println("<tr>"
						+ "<td>" + empNo + "</td>"
//						+ "<td>" + birthDate + "</td>"
						+ "<td>" + firstName + "</td>" 
						+ "<td>" + lastName + "</td>"
//						+ "<td>" + gender + "</td>"
//						+ "<td>" + hireDate + "</td>"
						+ "<tr>");
			}
			pw.println("</table>");
			pw.println("</div>");
			pw.close();
			set.close();
			st.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {

            InetAddress netAddress = getInetAddress();
            String hostname = getHostName(netAddress);
            String mysqlDriver = "jdbc:mysql://" + dbAddr  + ":" + dbPort + "/" + dbName + "?user=" + dbUser + "&password=" + dbPassword;
			Connection conn = DriverManager.getConnection(mysqlDriver);// localhost:主机名，端口号；Garbage:数据库database；Mysql帐户、密码
			Statement stmt = conn.createStatement();
			String username = request.getParameter("username");
			System.out.println("get username : " + username);
			number++;
			String sql = "INSERT INTO demo_data " + " VALUES (" + number + " ,'" + datenow + "' , '" + hostname + "', '" + username + "', 'M', '" + datenow + "')";
			stmt.executeUpdate(sql);
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public static InetAddress getInetAddress(){
  
        try{  
            return InetAddress.getLocalHost();  
        }catch(UnknownHostException e){  
            System.out.println("unknown host!");  
        }  
        return null;  
  
    }  
  
    public static String getHostIp(InetAddress netAddress){  
        if(null == netAddress){  
            return null;  
        }  
        String ip = netAddress.getHostAddress(); //get the ip address  
        return ip;  
    }  
  
    public static String getHostName(InetAddress netAddress){  
        if(null == netAddress){  
            return null;  
        }  
        String name = netAddress.getHostName(); //get the host address  
        return name;  
    } 
}
