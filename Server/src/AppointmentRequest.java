import java.io.*;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * @author 
 * 
 * 1. get http requests from android app
 * 2. get request type
 * 3. create connection to db
 * 4. responses back with xml
 *
 */
@WebServlet("/AppointmentRequest")
public class AppointmentRequest extends HttpServlet {

	//uses doGet and doPosts to recieve and send information
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String type = request.getParameter("type");
		if (type == null || (type.length() <= 0)) {
			return;
		}
		if (type.contains("searchByName")) {
			searchByName(request, response);
		} else if (type.contains("getPatient")) {
			getPatient(request, response);
		} else if (type.contains("getDaily")) {
			getDaily(request, response);
		} else if (type.contains("getAppointment")) {
			getAppointment(request, response);
		} else if (type.contains("addAppointment")) {
			addAppointment(request, response);
		} else if (type.contains("addPatient")) {
			addPatient(request, response);
		}
	}

	//responses to addPatient request from android app
	//The rest follow the same pattern
	private void addPatient(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		//connect to db
		Connection conn = null;
		Statement stmt = null;
		try {
			//get sql driver
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			conn = DriverManager.getConnection("jdbc:odbc:javatestdb");
			stmt = (Statement) conn.createStatement();

			//build query
			String id = request.getParameter("PatientID").trim().toLowerCase();
			String name = request.getParameter("PatientName").trim()
					.toLowerCase();
			String address = request.getParameter("Address").trim()
					.toLowerCase();
			String phone = request.getParameter("Phone").trim().toLowerCase();
			String email = request.getParameter("Email").trim().toLowerCase();

			String sqlStr = "insert into PatientInfo values ('" + id + "', '"
					+ name + "', '" + address + "', '" + phone + "', '" + email
					+ "')";

			System.out.println(sqlStr);
			
			stmt.executeUpdate(sqlStr);

			out.println("Added patient");
		} catch (ClassNotFoundException | SQLException e) {
			out.println("Add patient failed");
			e.printStackTrace();
		} finally {
			try {
				//safely close connection and http respon
				out.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
			}
		}
	}

	
	private void addAppointment(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			conn = DriverManager.getConnection("jdbc:odbc:javatestdb");
			stmt = (Statement) conn.createStatement();

			String id = request.getParameter("PatientID").trim().toLowerCase();
			String appointmentid = request.getParameter("AppointmentID").trim().toLowerCase();
			String time = request.getParameter("Time").trim().toLowerCase();

			String sqlStr = "insert into AppointmentInfo values ('" + appointmentid + "', '"
					+ id + "', '" + time + "')";

			System.out.println(sqlStr);
			stmt.executeUpdate(sqlStr);

			out.println("Added patient");
		} catch (ClassNotFoundException | SQLException e) {
			out.println("Add patient failed");
			e.printStackTrace();
		} finally {
			try {
				out.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
			}
		}
	}

	private void getAppointment(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		PrintWriter out = response.getWriter();

		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			conn = DriverManager.getConnection("jdbc:odbc:javatestdb");
			stmt = (Statement) conn.createStatement();

			String sqlStr = "select * from AppointmentInfo where AppointmentID = '"
					+ request.getParameter("AppointmentID").trim()
							.toLowerCase() + "'";

			System.out.println(sqlStr);

			ResultSet rset = (ResultSet) stmt.executeQuery(sqlStr);
			if (!rset.next()) {
				out = null;
			} else {

				out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				out.append("<Appointments>");
				do {
					out.append("<Appointment>");
					out.append("<PatientID>")
							.append(rset.getString("PatientID"))
							.append("</PatientID>");
					out.append("<AppointmentID>")
							.append(rset.getString("AppointmentID"))
							.append("</AppointmentID>");
					out.append("<Time>").append(rset.getString("Time"))
							.append("</Time>");
					out.append("</Appointment>");
				} while (rset.next());

				out.append("</Appointments>");
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
			}
		}
	}

	private void getDaily(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		PrintWriter out = response.getWriter();
		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			conn = DriverManager.getConnection("jdbc:odbc:javatestdb");
			stmt = (Statement) conn.createStatement();

			String sqlStr = "select * from AppointmentInfo where Time = '"
					+ request.getParameter("time").trim().toLowerCase() + "'";
			// System.out.println(sqlStr);

			ResultSet rset = (ResultSet) stmt.executeQuery(sqlStr);
			if (!rset.next()) {
				out = null;
			} else {

				out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				out.append("<Appointments>");
				do {
					out.append("<Appointment>");
					out.append("<PatientID>")
							.append(rset.getString("PatientID"))
							.append("</PatientID>");
					out.append("<AppointmentID>")
							.append(rset.getString("AppointmentID"))
							.append("</AppointmentID>");
					out.append("<Time>").append(rset.getString("Time"))
							.append("</Time>");
					out.append("</Appointment>");
				} while (rset.next());

				out.append("</Appointments>");
			}

		} catch (SQLException ex) {
			out.println("<h3>Something's wrong.</h3>");
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
			}
		}
	}

	private void searchByName(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		response.setContentType("text/xml;charset=UTF-8");
		PrintWriter out = response.getWriter();
		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			conn = DriverManager.getConnection("jdbc:odbc:javatestdb");
			stmt = (Statement) conn.createStatement();

			String sqlStr = "select * from PatientInfo where PatientName like '"
					+ request.getParameter("name").trim().toLowerCase() + "'";

			ResultSet rset = (ResultSet) stmt.executeQuery(sqlStr);
			if (!rset.next()) {
				out = null;
			} else {
				out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				out.append("<Patients>");
				do {
					out.append("<Patient>");
					out.append("<PatientID>")
							.append(rset.getString("PatientID"))
							.append("</PatientID>");
					out.append("<PatientName>")
							.append(rset.getString("PatientName"))
							.append("</PatientName>");
					out.append("<Address>").append(rset.getString("Address"))
							.append("</Address>");
					out.append("</Patient>");
				} while (rset.next());

				out.append("</Patients>");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
			}
		}
	}

	private void getPatient(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		System.out.println(request.getParameter("PatientID").trim()
				.toLowerCase());
		response.setContentType("text/xml;charset=UTF-8");
		PrintWriter out = response.getWriter();

		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			conn = DriverManager.getConnection("jdbc:odbc:javatestdb");
			stmt = (Statement) conn.createStatement();

			String sqlStr = "select * from PatientInfo where PatientID = '"
					+ request.getParameter("PatientID").trim().toLowerCase()
					+ "'";

			ResultSet rset = (ResultSet) stmt.executeQuery(sqlStr);
			if (!rset.next()) {
				out = null;
			} else {

				out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				out.append("<Patients>");
				do {
					out.append("<Patient>");
					out.append("<PatientID>")
							.append(rset.getString("PatientID"))
							.append("</PatientID>");
					out.append("<PatientName>")
							.append(rset.getString("PatientName"))
							.append("</PatientName>");
					out.append("<Address>").append(rset.getString("Address"))
							.append("</Address>");
					out.append("<Phone>").append(rset.getString("Phone"))
							.append("</Phone>");
					out.append("<Email>").append(rset.getString("Email"))
							.append("</Email>");
					out.append("</Patient>");
				} while (rset.next());

				out.append("</Patients>");
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
			}
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
