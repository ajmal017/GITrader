/*
 * Copyright 2003-2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tim.util.bbdd;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

/**
 * <code>ResultSetHandler</code> implementation that converts one
 * <code>ResultSet</code> column into an Object. This class is thread safe.
 * 
 * @see ResultSetHandler
 */
public class DoubleHandler implements ResultSetHandler {

    /**
     * The column number to retrieve.
     */
    private int columnIndex = 1;

    /**
     * The column name to retrieve.  Either columnName or columnIndex
     * will be used but never both.
     */
    private String columnName = null;

    /** 
     * Creates a new instance of ScalarHandler.  The first column will
     * be returned from <code>handle()</code>.
     */
    public DoubleHandler() {
        super();
    }

    /** 
     * Creates a new instance of ScalarHandler.
     * 
     * @param columnIndex The index of the column to retrieve from the 
     * <code>ResultSet</code>.
     */
    public DoubleHandler(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    /** 
     * Creates a new instance of ScalarHandler.
     * 
     * @param columnName The name of the column to retrieve from the 
     * <code>ResultSet</code>.
     */
    public DoubleHandler(String columnName) {
        this.columnName = columnName;
    }

    /**
     * Returns one <code>ResultSet</code> column as an object via the
     * <code>ResultSet.getObject()</code> method that performs type 
     * conversions.
     * 
     * @return The column or <code>null</code> if there are no rows in
     * the <code>ResultSet</code>.
     * 
     * @throws SQLException
     * 
     * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
     */
    public Object handle(ResultSet rs) throws SQLException {

        int index=this.columnIndex;
        if (rs.next()) 
        {   
           
            if(this.columnName !=null)
            {
                index=rs.findColumn(columnName);
            }
            
             if(rs.getMetaData().getColumnClassName(index).equals("java.lang.Double"))
             {
                return new Double(rs.getDouble(index));
             }
             else
             {
                return rs.getObject(index);
             }
            

        } else 
        {
            return null;
        }
    }
}
