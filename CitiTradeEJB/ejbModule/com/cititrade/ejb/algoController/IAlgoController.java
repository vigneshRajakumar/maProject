package com.cititrade.ejb.algoController;

import java.util.List;

import javax.ejb.Local;

import com.algorithm.jpa.Algo;



@Local
public interface IAlgoController {
	
	public List<Algo> getAllAlgo(); 
	public Algo getAlgoByName(String algoName);
}
