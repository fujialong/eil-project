package com.shencai.eil.common.model;

public class PageParam {
    private int size = 10;
    private int current = 1;
    private int offSet = 0;

    public PageParam() {
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCurrent() {
        return this.current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getOffSet() {
        return (this.current - 1) * this.size;
    }

    public void setOffSet(int offSet) {
        this.offSet = offSet;
    }
}
