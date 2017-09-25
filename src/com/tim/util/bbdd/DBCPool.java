package com.tim.util.bbdd;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.datasources.*;

import java.sql.Connection;


public class DBCPool {

	private static DBCPool   datasource;
	private static BasicDataSource ds;
	
	
	private  DBCPool() throws SQLException 
	{
	
        ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUsername("root");
        ds.setPassword("10203040");
        ds.setUrl("jdbc:mysql://localhost/tradermodel_rafa");
       
     // the settings below are optional -- dbcp can work with defaults
        ds.setMinIdle(5);
        ds.setMaxIdle(20);
        ds.setMaxOpenPreparedStatements(180);

    }

    public  static DBCPool getInstance() throws IOException, SQLException {
        if (datasource == null) {
            datasource = new DBCPool();
            return datasource;
        } else {
            return datasource;
        }
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
    public static DataSource getDataSource() throws SQLException {
        return ds;
    }

}
