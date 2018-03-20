/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.monitor.data;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 *
 * @author eric
 */
public class StatisticsLog {

    private final ArrayList<Statistic> monitorstats;
    private final String service;

    public StatisticsLog(String snm) {
        this.service = snm;
        this.monitorstats = new ArrayList<>();
    }

    public ArrayList<Statistic> getMonitorstats() {
        return monitorstats;
    }

    public String getService() {
        return service;
    }

    public Statistic getSystemStat(Timestamp time) {
        for (Statistic stat : monitorstats) {
            if (stat.getTimestamp().equals(time)) {
                return stat;
            }
        }
        return null;
    }

    public ArrayList<Statistic> getSystemStats(Timestamp t1, Timestamp t2) {

        ArrayList<Statistic> s = new ArrayList<>();
        monitorstats.stream().filter((stat) -> (stat.getTimestamp().after(t1) && stat.getTimestamp().before(t2))).forEachOrdered((stat) -> {
            s.add(stat);
        });
        return s;
    }

    public void newStatistic(String snme, String contid, double mem, double cpu) {
        Statistic s = new Statistic(snme, contid, cpu, mem);
        this.monitorstats.add(s);
    }

}
