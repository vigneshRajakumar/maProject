package com.algorithm.jpa;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(schema="ct_project", name="ct_algorithms")
public class Algo implements Serializable {
	
	private static final long serialVersionUID = 1L;

    private int algo_id; 
    private String algo_name; 
	    
    /**
     * Set the index of the algorithm
     * @param id index of the algorithm 
     */
    public void setAlgo_id(int id) {
        this.algo_id = id;
    }
    
    /**
     * Get the algorithm ID 
     * @return algo_id - Algorithm ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getAlgo_id() {
        return this.algo_id;
    }  
    
    /**
     * Get the algorithm name
     * @return algo_name - Algorithm name
     */
    public String getAlgo_name() {
		return this.algo_name;
	}

    
    /**
     * Set the algorithm name
     * @param algo_name Algorithm name
     */
	public void setAlgo_name(String name) {
		this.algo_name = name;
	}

   
}
