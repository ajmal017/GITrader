package com.tim.model;

import java.sql.Timestamp;

import java.io.Serializable;


public class SimulationPosition extends Position{
	

		
		
		/* FLAG PARA CONTROLAR QUE DEBE SER CANCELADA */
		private Long simulationID=null;
		
		
		
		/* NO DATABASE DATA, USED TO MAP QUERIES IN THIS BEAN */
		
		public Long getSimulationID() {
			return simulationID;
		}

		public void setSimulationID(Long simulationID) {
			this.simulationID = simulationID;
		}

		
}
