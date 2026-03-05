package bg.sofia.uni.fmi.mjt.show.ergenka;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;

public abstract class ErgenkaBase implements Ergenka {

    protected final String name;
    protected final short age;
    protected final int romanceLevel;
    protected final int humorLevel;
    protected int rating;

    public ErgenkaBase(String name, short age, int romanceLevel, int humorLevel, int rating) {
        this.name = name;
        this.age = age;
        this.romanceLevel = romanceLevel;
        this.humorLevel = humorLevel;
        this.rating = rating;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public short getAge() {
        return age;
    }

    @Override
    public int getRomanceLevel() {
        return romanceLevel;
    }

    @Override
    public int getHumorLevel() {
        return humorLevel;
    }

    @Override
    public int getRating() {
        return rating;
    }

    @Override
    public final String toString() {
        return String.format("[%s, %d, %d, %d, %d]%n", name, age, romanceLevel, humorLevel, rating);
    }
}