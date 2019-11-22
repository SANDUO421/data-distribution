//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.data.distribution.utils;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Buffer {
    private Queue<String> buff = null;
    private int queuelength = 0;
    private int length = 2000;
    private long lostcount = 0L;
    private String popstr = null;

    public Buffer(int maxLength) {
        this.length = maxLength;
        this.buff = new ConcurrentLinkedQueue();
    }

    public long getLostcount() {
        if (this.lostcount >= 922337203685477580L) {
            this.lostcount = 0L;
        }

        return this.lostcount;
    }

    public void push(String in) {
        if (in != null) {
            while(true) {
                if (this.queuelength <= this.length) {
                    this.buff.offer(in);
                    ++this.queuelength;
                    break;
                }

                this.buff.poll();
                --this.queuelength;
                ++this.lostcount;
            }
        }

    }

    public synchronized String pop() {
        this.popstr = (String)this.buff.poll();
        if (this.popstr != null) {
            --this.queuelength;
        }

        return this.popstr;
    }

    public void setEmpty() {
        this.buff.clear();
        this.queuelength = 0;
    }

    public int getLength() {
        this.queuelength = this.buff.size();
        return this.queuelength;
    }

    public boolean isEmpty() {
        return this.buff.isEmpty();
    }
}
