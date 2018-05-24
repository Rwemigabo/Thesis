/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.analyse.data;



import com.mycompany.aatr2.analyse.Symptom;
import java.util.ArrayList;

/**
 *NB: Same As Service Level objectives: pre-defined symptoms to avoid or heal from.
 *Should be part of Knowledge Base.
 *
 * @author eric
 */
public class SymptomRepository {
    private final ArrayList<Symptom> symptoms = new ArrayList<>();
    
    private double mincpu_SLO;
	private double maxcpu_SLO;
	private double minmem_SLO;
	private double maxmem_SLO;
	
	public SymptomRepository() {
		this.maxcpu_SLO = 90;
		this.mincpu_SLO = 5;
		this.maxmem_SLO = 90;
		this.minmem_SLO = 5;
	}
	
	public SymptomRepository(double maxcpu, double mincpu, double maxmem, double minmem) {
		this.maxcpu_SLO = maxcpu;
		this.mincpu_SLO = mincpu;
		this.maxmem_SLO = maxmem;
		this.minmem_SLO = minmem;
	}

	
	
	public double getMincpu_SLO() {
		return mincpu_SLO;
	}

	public void setMincpu_SLO(double mincpu_SLO) {
		this.mincpu_SLO = mincpu_SLO;
	}

	public double getMaxcpu_SLO() {
		return maxcpu_SLO;
	}

	public void setMaxcpu_SLO(double maxcpu_SLO) {
		this.maxcpu_SLO = maxcpu_SLO;
	}

	public double getMinmem_SLO() {
		return minmem_SLO;
	}

	public void setMinmem_SLO(double minmem_SLO) {
		this.minmem_SLO = minmem_SLO;
	}

	public double getMaxmem_SLO() {
		return maxmem_SLO;
	}

	public void setMaxmem_SLO(double maxmem_SLO) {
		this.maxmem_SLO = maxmem_SLO;
	}


	public boolean checkSLO(double mem, double cpu) {
		if(mem > this.maxmem_SLO || mem < this.minmem_SLO || cpu > this.maxcpu_SLO || cpu< this.mincpu_SLO) {
			return true;
		}return false;
	}
    
	public ArrayList<Symptom> getSymptoms() {
        return symptoms;
    }
    
}
