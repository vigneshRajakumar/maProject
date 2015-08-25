package com.usercontroller.jpa;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: UserType
 *
 */

@Entity
@Table(schema="ct_project", name="ct_usertype")
public class UserType implements Serializable {

	
	private static final long serialVersionUID = 1L;

    private int type_id; //typeID
    private String user_type; //userType
	   
    
    public UserType(){
    	this.type_id = 1;
    	this.user_type = "Trader";
    }
    public void setType_ID(int id) {
        this.type_id = id;
    }
     
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getType_ID() {
        return this.type_id;
    }  
    
    public String getuser_type() {
		return this.user_type;
	}

 
	public void setuser_type(String type) {
		this.user_type = type;
	}
}
