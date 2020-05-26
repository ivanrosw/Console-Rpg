package local.models.game;

public class Usable implements Item {

    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void use() {

    }
}
