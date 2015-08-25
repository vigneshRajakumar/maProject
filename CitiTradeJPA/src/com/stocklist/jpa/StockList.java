package com.stocklist.jpa;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(schema="ct_project", name="ct_stocklist")
public class StockList implements Serializable {
	
	private static final long serialVersionUID = 1L;

    private int stock_id; 
    private String symbol; 
    private String sname; 
	
    
    /**
     * Set the Stock id
     * @param id index of the Stock
     */
    
    public void setStock_ID(int id) {
        this.stock_id = id;
    }
    
    /**
     * Get the Stock ID 
     * @return stock_id - Stock ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getStock_ID() {
        return this.stock_id;
    }  
    
    /**
     * Get the Stock Symbol 
     * @return algo_name - Algorithm name
     */
    public String getSymbol() {
		return this.symbol;
	}
    
    /**
     * Set the Stock symbol
     * @param symbol Symbol of the Stock
     */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
    
    /**
     * Get the Stock Symbol 
     * @return algo_name - Algorithm name
     */
    public String getSName() {
		return this.sname;
	}
    
    /**
     * Set the Stock symbol
     * @param symbol Symbol of the Stock
     */
	public void setSName(String name) {
		this.sname = name;
	}
	
	
   
}
