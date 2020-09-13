package de.loc.tools;

public class Pair<L, R> {
    private L left;
    private R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return this.left;
    }

    public R getRight() {
        return this.right;
    }

    public void setLeft(L obj) {
        this.left = obj;
    }

    public void setRight(R obj) {
        this.right = obj;
    }
}
