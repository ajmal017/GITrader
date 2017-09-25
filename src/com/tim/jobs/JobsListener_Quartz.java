package com.tim.jobs;

import java.io.IOException;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Priority;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.tim.dao.TradingMarketDAO;
import com.tim.model.StrategyCloseAllPositions;
import com.tim.util.LogTWM;
import com.tim.util.Utilidades;

public class JobsListener_Quartz  implements Servlet {
    
   
	@Override
	public void init(ServletConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		try {
            //Instanciamos el gestor de tareas, a través de la clase SchedulerFactory
           /*   SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
            Scheduler sched = schedFact.getScheduler();
            
            
            LogTWM.getLog(JobsListener_Quartz.class);
        	LogTWM.log(Priority.INFO, "Starting Activate Trading Quartz Jobs process..");
            
        	
        	JobKey _Key1= new JobKey("StartupTrading_Read");
        	if (sched.checkExists(_Key1))
        			sched.deleteJob(_Key1);
        	
        	// define the job and tie it to our HelloJob class
    		JobDetail job = JobBuilder.newJob(Trading_Read.class)
    				.withIdentity("StartupTrading_Read", "group3").build();
    		
    		// define the job and tie it to our HelloJob class
    		
    		JobKey _Key2= new JobKey("StartupTrading_Actions");
        	if (sched.checkExists(_Key2))
        			sched.deleteJob(_Key2);
    		
    		JobDetail job2 = JobBuilder.newJob(Trading_Actions.class)
    				.withIdentity("StartupTrading_Actions", "group4").build(); 
    	 
    		*/
    	/* 	JobKey _Key3= new JobKey("StartupTrading_Read_Fix_Gaps");
        	if (sched.checkExists(_Key3))
        			sched.deleteJob(_Key3);
    		
    		JobDetail job3 = JobBuilder.newJob(Trading_Read_Fix_Gaps.class)
    				.withIdentity("StartupTrading_Read_Fix_Gaps", "group5").build(); */
    		// compute a time that is on the next round minute
    		// Date runTime = TriggerUtils.  (new Date());

    		// compute a time that is on the next round minute
    		//Date runTime = evenMinuteDate(new Date());

    		// Trigger the job to run on the next round minute
    		/*  Trigger trigger = TriggerBuilder
    				.newTrigger()    				
    				.withIdentity("JobsInStartUp", "group4")
    				.build();
    		
    		Trigger trigger2 = TriggerBuilder
    				.newTrigger()
    				.withIdentity("JobsInStartUp", "group4")    			
    				.build(); */ 
    		
    		/* Trigger trigger3 = TriggerBuilder
    				.newTrigger()
    				.withIdentity("JobsInStartUp", "group5")
    				.build();   				 */
    		// Tell quartz to schedule the job using our trigger
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
    		scheduler.start();
    		 
    	 	/* scheduler.scheduleJob(job, trigger);
    		 scheduler.scheduleJob(job2, trigger2);*/
    		 //scheduler.scheduleJob(job3, trigger3);
    		
    		
    		
    		
    		/* activamos el scheduler de MYSQL. No me tira desde el my.ini */
    		TradingMarketDAO.StartTradingSchedulerMYSQL();
    		
            
           
            
        } catch (Exception e) {e.printStackTrace();}
		
	}
	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		try {
			
			LogTWM.getLog(JobsListener_Quartz.class);
			
			LogTWM.log(Priority.INFO, "Shutting Quartz Jobs");
			
			Scheduler sched =  StdSchedulerFactory.getDefaultScheduler();
			boolean waitForJobsToComplete = true ;			 
			sched.shutdown();
			/* List<JobExecutionContext> lJobs = sched.getCurrentlyExecutingJobs();
			if (lJobs!=null && lJobs.size()>0)
			{
				for (int j=0;j<lJobs.size();j++)
				{
					JobExecutionContext _JobRunning = lJobs.get(i);
					_JobRunning.
					
				}
				
				
			}
			List sched.getCurrentlyExecutingJobs()
			*/
			//Thread.sleep(1000);
			
			
			LogTWM.log(Priority.INFO, "Destroyed Quartz Jobs");
			
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}
	@Override
	public ServletConfig getServletConfig() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getServletInfo() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}