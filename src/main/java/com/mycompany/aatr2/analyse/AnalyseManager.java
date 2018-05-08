/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.analyse;


import java.util.ArrayList;

import com.mycompany.aatr2.Cluster;

/**
 *
 * @author eric
 */
public class AnalyseManager {

    private final ArrayList<Analyser> analysers = new ArrayList<>();
    private static final AnalyseManager inst = new AnalyseManager();
    
    
    public static AnalyseManager getInstance() {
        return inst;
    }
    
    private AnalyseManager(){
    	
    }
    
    public void newAnalyser(Cluster c){
        int newID = analysers.size() + 1;
        Analyser ana = new Analyser(newID, c);
        ana.initiate();
        analysers.add(ana);

    }

    public ArrayList<Analyser> getAnalysers() {
        return analysers;
    }


}
