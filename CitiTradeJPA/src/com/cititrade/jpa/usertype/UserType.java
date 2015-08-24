package com.cititrade.jpa.usertype;

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
	   
    public void setType_ID(int id) {
        this.type_id = id;
    }
     
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getType_ID() {
        return this.type_id;
    }  
    
    public String getUser_Type() {
		return this.user_type;
	}

 
	public void setUser_Type(String type) {
		this.user_type = type;
	}
}
