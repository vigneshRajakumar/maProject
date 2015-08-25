package com.stocklist.jpa;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Stock
 *
 */

@Entity
@Table(schema="ct_project", name="ct_stocklist")
public class StockList implements Serializable {
	
	private static final long serialVersionUID = 1L;

    private int stock_id; 
    private String symbol; 
    private String sname; 
	
  
    public void setStock_ID(int id) {
        this.stock_id = id;
    }
    
  
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getStock_ID() {
        return this.stock_id;
    }  
  
    public String getSymbol() {
		return this.symbol;
	}
 
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
   
    public String getSName() {
		return this.sname;
	}
  
	public void setSName(String name) {
		this.sname = name;
	}
	
	
   
}
