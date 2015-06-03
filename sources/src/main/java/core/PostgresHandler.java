package core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import core.HardcodedData.DBVariableTypes;

public class PostgresHandler {
	private String dburl;
	private String userName;
	private String password;
	private Connection conn;
	
	private String sqlCommand;
	private String errorMessage;

	/**
	 * class constructor.
	 * 
	 * @param url
	 *            link to the database.
	 * @param userName
	 *            username to access the database.
	 * @param password
	 *            password to access the database.
	 */
	public PostgresHandler(String url, String userName, String password) {
		this.dburl = url;
		this.userName = userName;
		this.password = password;
		
		setSqlCommand("select * from ");
		setErrorMessage("Exception caught");
		
		try {
			this.createConnection();
		} catch (Exception e) {
			System.out.println("Fail to create connection to database " + url);
			System.out.println(e);
		}
	}
	
	/**
	 * createConnection()
	 * 
	 * @return
	 * @throws Exception
	 *             To create a postgresSQL connection
	 */
	private void createConnection() throws Exception {
		Class.forName("org.postgresql.Driver");
		this.conn = DriverManager.getConnection(this.dburl, this.userName, this.password);
	}

	/**
	 * 
	 */
	public void closeConnection() {
		try {
			this.getConn().close();
		} catch (SQLException e) {
		}
	}
	
	
	/**
	 * gets values in specified columns in rows satisfying a query from a table
	 * and puts them together in a 2D array.
	 * 
	 * @param schemaName
	 *            name of the schema the table belongs to.
	 * @param tableName
	 *            name of the table of interest.
	 * @param queryStr
	 *            value of the query, which is a string.
	 * @param queryFieldName
	 *            name of the field to look up for the query value.
	 * @param colIdx
	 *            index of columns from which values will be retrieved.
	 */
	public int[][] getIntArrayFromRows(String schemaName, String tableName, String queryStr, String queryFieldName, int[] colIdx) {
		if (!this.getTypeOfField(schemaName, tableName, queryFieldName).
				equals(HardcodedData.DBVariableTypes.text.toString())) {
			return null;
		}
		
		for (int icol = 0; icol <= colIdx.length - 1; icol++){
			if (!this.getTypeOfField(schemaName, tableName, colIdx[icol])
					.equals(String.valueOf(HardcodedData.DBVariableTypes.int4))){
				return null;
			}
		}
		
		int[][] intArr = null;
		try {
			ResultSet rs = this.conn
					.createStatement()
					.executeQuery(getSqlCommand() + schemaName + '.' + tableName);
			
			while (rs.next()){
				if (rs.getString(queryFieldName).equals(queryStr)) {
					int[][] tmpIntArr = new int[1][colIdx.length];
					
					for (int i = 0; i <= tmpIntArr[0].length - 1; i++){
						tmpIntArr[0][i] = rs.getInt(colIdx[i]);
					}
					
					intArr = ArrayHandler.concateMatrices(intArr,tmpIntArr);
				}
			}
			
			rs.close();

		} catch (Exception e) {
		}
		return intArr;
	}

	
	/**
	 * gets the type of a field in a table.
	 * 
	 * @param schemaName
	 *            name of the schema the table belongs to.
	 * @param tableName
	 *            name of the table of interest.
	 * @param field
	 *            name of the field of interest.
	 * @return type of the field.
	 */
	public String getTypeOfField(String schemaName, String tableName,
			String field) {
		String type = null;
		
		try {
			DatabaseMetaData metaData = this.conn.getMetaData();
			try {
				ResultSet rs = metaData.getColumns(null, schemaName, tableName,
						null);
				int pkFieldIdx = 0;
				while (rs.next()){
					if (rs.getString("COLUMN_NAME").equals(field)) {
						pkFieldIdx = rs.getInt("ORDINAL_POSITION");
						break;
					}
				}
				
				rs.close();

				rs = this.conn
						.createStatement()
						.executeQuery(getSqlCommand() + schemaName + '.' + tableName);
				type = rs.getMetaData().getColumnTypeName(pkFieldIdx);
				rs.close();
			} catch (Exception e) {
				System.out.println("can't create ResultSet");
			}
		} catch (Exception e) {
			System.out.println("can't create DatabaseMetaData");
        }
		return type;
	}

	/**
	 * gets the type of a field in a table.
	 * 
	 * @param schemaName
	 *            name of the schema the table belongs to.
	 * @param tableName
	 *            name of the table of interest.
	 * @param fieldIdx
	 *            index of the field of interest.
	 * @return type of the field.
	 */
	public String getTypeOfField(String schemaName, String tableName,
			int fieldIdx) {
		String type = null;
		try {
			ResultSet rs = this.conn
					.createStatement()
					.executeQuery(getSqlCommand() + schemaName + '.' + tableName);
			type = rs.getMetaData().getColumnTypeName(fieldIdx);
			rs.close();
		} catch (Exception e) {
		}
		return type;
	}
	
	
	/**
	 * gets values in specified columns in all rows from a table and puts them
	 * together in a 2D array.
	 * 
	 * @param schemaName
	 *            name of the schema the table belongs to.
	 * @param tableName
	 *            name of the table of interest.
	 * @param colIdx
	 *            index of columns from which values will be retrieved.
	 * @return
	 */
	public int[][] getIntArrayFromAllRows(String schemaName, String tableName,
			int[] colIdx) {
		for (int icol = 0; icol <= colIdx.length - 1; icol++){
			if (!this.getTypeOfField(schemaName, tableName, colIdx[icol])
					.equals(String.valueOf(HardcodedData.DBVariableTypes.int4))){
				return null;
			}
		}
		
		int[][] intArr = null;
		try {
			ResultSet rs = this.conn
					.createStatement()
					.executeQuery(getSqlCommand() + schemaName + '.' + tableName);
			while (rs.next()) {
				int[][] tmpIntArr = new int[1][colIdx.length];
				
				for (int i = 0; i <= tmpIntArr[0].length - 1; i++){
					tmpIntArr[0][i] = rs.getInt(colIdx[i]);
				}
				
				intArr = (new ArrayHandler())
						.concateMatrices(intArr, tmpIntArr);
			}
			rs.close();

		} catch (Exception e) {
		}
		return intArr;
	}

	
	
	public Connection getConn() {
		return conn;
	}
	
	public String getSqlCommand() {
		return sqlCommand;
	}

	public void setSqlCommand(String sqlCommand) {
		this.sqlCommand = sqlCommand;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
