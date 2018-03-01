/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.monitor;

import com.spotify.docker.client.messages.MemoryStats;

/**
 *
 * @author eric
 */
public class Memory extends ContextElement{
    
    //private final String name;
    private long limit;
    private long usage;
    private long maxusg;
    private MemoryStats stats;
    
    public Memory(double upper, double lower, String name, MemoryStats ms) {
        super(upper, lower, name);
        this.stats = ms;
        this.limit = stats.limit();
        this.usage = stats.usage();
        this.maxusg = stats.maxUsage();
    }

    public long getLimit() {
        return limit;
    }

    public long getUsage() {
        return usage;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public void setUsage(long used) {
        this.usage = used;
    }

    public long getMaxusg() {
        return maxusg;
    }

    public MemoryStats getStats() {
        return stats;
    }

    public void setMaxusg(long maxusg) {
        this.maxusg = maxusg;
    }

    public void setStats(MemoryStats stats) {
        this.stats = stats;
    }
    
    @Override
     public String getName(){
        return this.name;
    }
     /**
     *
     * @return percentage of the memory used.
     */
    @Override
    public double getMemoryPerc() {

        double memMetric = (usage / limit) * 100;
        //double memMetric = (limit-usage);
        return memMetric;
    }
}
