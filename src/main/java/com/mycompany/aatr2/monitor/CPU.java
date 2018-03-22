/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.monitor;

import com.spotify.docker.client.messages.CpuStats;

/**
 *
 * @author eric
 */
public class CPU extends ContextElement{
    
    private long cpuUsage;
    private int perCpu;
    private long prevCPU;
    private long prevSystem;
    private long systemUsage;
    private CpuStats cpu;
    //private CpuStats pcpu;
    //private PreCpu pcpu;
    
    public CPU(double upper, double lower, String name, CpuStats cpu, CpuStats precpu) {
        super(upper, lower, name);
        this.cpu = cpu;
        this.cpuUsage = this.cpu.cpuUsage().totalUsage();
        this.perCpu = this.cpu.cpuUsage().percpuUsage().size();
        this.systemUsage = this.cpu.systemCpuUsage();
        
    }

    @Override
     public String getName(){
        return this.name;
    }
    public long getCpuUsage() {
        return cpuUsage;
    }

    public int getPerCpu() {
        return perCpu;
    }

    public long getPrevCPU() {
        return prevCPU;
    }

    public long getPrevSystem() {
        return prevSystem;
    }

    public long getSystemUsage() {
        return systemUsage;
    }

    public void setCpuUsage(long cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public void setPerCpu(int perCpu) {
        this.perCpu = perCpu;
    }

    public void setPrevCPU(long prevCPU) {
        this.prevCPU = prevCPU;
    }

    public void setPrevSystem(long prevSystem) {
        this.prevSystem = prevSystem;
    }

    public void setSystemUsage(long systemUsage) {
        this.systemUsage = systemUsage;
    }
    
    /**
     * Calculates the percentage CPU being used.
     *
     * @return the CPU percentage being used
     */
    @Override
    public double calculateCPU() {

        double cpuP = 0;
        long cpuDelta = cpuUsage - prevCPU;
        long systemDelta = systemUsage - prevSystem;

        if (systemDelta > 0.0 && cpuDelta > 0.0) {
            cpuP = ((cpuDelta / systemDelta) * (perCpu)) * 100;
        }

        return cpuP;

    }
}
