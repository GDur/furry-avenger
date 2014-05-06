/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject1;

import java.util.Date;

/**
 *
 * @author GDur
 */
public class Stopwatch {

    private Date startTime, stopTime;

    public void start() {
        startTime = new Date();
    }

    public void stop() {
        stopTime = new Date();
    }

    public String getElapsedTime() {
        long timediff = (stopTime.getTime() - startTime.getTime());
        return ("Time difference: " + timediff + "ms");
    }

}
