package gov.epa.bencloud.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionManager {

	
	public static Connection getConnection() {
		
		Connection connection = null;
		try {
			connection = PooledDataSource.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return connection;
	}
	
	public static void releaseConnection(Connection connection) {
		if (null != connection) {
			closeConnection(connection);
		}
	}

	public static void releaseConnection(Connection connection, Statement statement) {
		if (null != statement) {
			closeStatement(statement);
		}
		if (null != connection) {
			closeConnection(connection);
		}
	}

	public static void releaseConnection(
			Connection connection, Statement statement, ResultSet resultSet) {

		if (null != resultSet) {
			closeResultSet(resultSet);
		}
		if (null != statement) {
			closeStatement(statement);
		}
		if (null != connection) {
			closeConnection(connection);
		}
	}

	public static void releaseConnection(
			Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {

		if (null != resultSet) {
			closeResultSet(resultSet);
		}
		if (null != preparedStatement) {
			closePreparedStatement(preparedStatement);
		}
		if (null != connection) {
			closeConnection(connection);
		}
	}

	public static void closeResultSet(ResultSet resultSet) {
		try {
			resultSet.close();
		} catch (SQLException e) {
		}
	}

	public static void closeStatement(Statement statement) {
		try {
			statement.close();
		} catch (SQLException e) {
		}
	}

	public static void closePreparedStatement(PreparedStatement preparedStatement) {
		try {
			preparedStatement.close();
		} catch (SQLException e) {
		}
	}

	public static void closeConnection(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
		}
	}
}
