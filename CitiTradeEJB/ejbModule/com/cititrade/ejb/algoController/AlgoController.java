package com.cititrade.ejb.algoController;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.cititrade.jpa.algorithms.Algo;

@Stateless
public class AlgoController implements IAlgoController{
	
	@PersistenceContext(name="CitiTradeJPA-ejbPU")
	private EntityManager em;
	
	@Override
	public List<Algo> getAllAlgo(){

		Query query = em.createQuery("SELECT algo_a FROM Algo AS algo_a order by algo_a.algo_Name asc"); 
			
		// Execute the query, and get a collection of beans back.
		
		List<Algo> algoList = query.getResultList();
		for (Algo algo : algoList) {
			if (algo == null) {
				System.out.print("Algo is null");
			} 
		}
		return algoList;
	}	
	
	@Override
    public Algo getAlgoByName(String algoName) {

    	TypedQuery<Algo> query = em.createQuery("SELECT algo_b FROM Algo AS algo_b WHERE algo_b.algo_Name = :algoName", Algo.class);
        query.setParameter("algoName", algoName);

        // Execute the query, and get a collection of beans back.
        Algo algoObj = null;
        try {
            algoObj = query.getSingleResult();

        } catch (EntityNotFoundException ex) {
            System.out.println("Algo not found: " + algoName);

        } catch (NonUniqueResultException ex) {
            System.out.println("More than one team named: " + algoName);

        }
          
        return algoObj;
    }

	

}
