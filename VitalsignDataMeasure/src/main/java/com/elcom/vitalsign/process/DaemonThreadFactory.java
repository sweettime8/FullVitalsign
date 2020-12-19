/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vitalsign.process;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 *
 * @author Admin
 */
public class DaemonThreadFactory implements ThreadFactory {

    private final ThreadFactory factory;

    public DaemonThreadFactory() {
        this(Executors.defaultThreadFactory());
    }

    /**
     * Construct a ThreadFactory with setDeamon(true) wrapping the given factory
     *
     * @param factory
     */
    public DaemonThreadFactory(ThreadFactory factory) {
        if (factory == null) {
            throw new NullPointerException("factory cannot be null");
        }
        this.factory = factory;
    }

    @Override
    public Thread newThread(Runnable r) {
        final Thread t = factory.newThread(r);
        t.setDaemon(true);
        return t;
    }
}
