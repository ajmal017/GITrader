package com.tim.util.bbdd;

import java.beans.BeanInfo;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
 

public class QueryRunner extends org.apache.commons.dbutils.QueryRunner
{
    
	    /** Creates a new instance of QueryRunner */
	static final  javax.sql.DataSource GITraderDs_=null;;
    @SuppressWarnings("deprecation")
	public QueryRunner() throws java.sql.SQLException, Exception      
    {
    	super(DBCPool.getInstance().getDataSource());
    	
    }   
    public int updateBean(String sql,Object bean) throws java.sql.SQLException
    {
        BeanInfo bin=null;
   
        try {
            bin = Introspector.getBeanInfo(bean.getClass());

        } catch (IntrospectionException e) {
            throw new java.sql.SQLException(
                "Bean introspection failed: " + e.getMessage());
        }

        PropertyDescriptor[] props=bin.getPropertyDescriptors();
        String[] trozos=sql.split("##");
       
        //los trozos impares son los que definen las variables
        String[] variables=new String[trozos.length/2];
        String sqlFinal="";
        for(int i=0;i<trozos.length;i++)
        {
            if(i%2==0)
            {
                sqlFinal+=trozos[i];
                
            }
            else
            {
                variables[(i-1)/2]=trozos[i];
                sqlFinal+="?";
            }
        }
        Object[]datos=new Object[variables.length];
       
        for(int x=0;x<variables.length;x++)
        {
            String variable=variables[x];
            boolean existePropiedad=false;
            for(int y=0;y<props.length;y++)
            {
                if(!existePropiedad&&variable.equalsIgnoreCase(props[y].getName()))
                {
                    existePropiedad=true;
                    try
                    {
                    datos[x]=props[y].getReadMethod().invoke(bean, new Object[0]);
                    }
                    catch(Exception ex)
                    {
                       throw new java.sql.SQLException(
                        "No puedo acceder al metodo "+props[y].getName()+ex.getMessage()); 
                    }
                    
                }
            }
            if(!existePropiedad)
            {
                 throw new java.sql.SQLException(
                "No encuentro propiedad para "+variable);
            }
        }
       return this.update(sqlFinal,datos);
    }
        
}
