/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.analyse.data;



import com.mycompany.aatr2.analyse.Symptom;
import java.util.ArrayList;

/**
 *
 * @author eric
 */
public class SymptomRepository {
    private final ArrayList<Symptom> symptoms = new ArrayList<>();
    
    public SymptomRepository() {
    }

    public ArrayList<Symptom> getSymptoms() {
        return symptoms;
    }
    
    
    
}
