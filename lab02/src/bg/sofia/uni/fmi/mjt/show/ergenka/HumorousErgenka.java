package bg.sofia.uni.fmi.mjt.show.ergenka;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;

public class HumorousErgenka extends ErgenkaBase {

    public HumorousErgenka(String name, short age, int romanceLevel, int humorLevel, int rating) {
        super(name, age, romanceLevel, humorLevel, rating);
    }

    @Override
    public void reactToDate(DateEvent dateEvent) {
        if (dateEvent == null) {
            return;
        }

        int bonuses = 0;
        if (dateEvent.getDuration() >= 30 && dateEvent.getDuration() <= 90) {
            bonuses = 4;
        } else if (dateEvent.getDuration() < 30) {
            bonuses = -2;
        } else if (dateEvent.getDuration() > 90) {
            bonuses = -3;
        }

        int tension = Math.max(1, dateEvent.getTensionLevel());
        int part1 = Math.floorDiv(humorLevel * 5, tension);
        int part2 = Math.floorDiv(romanceLevel, 3);

        rating += part1 + part2 + bonuses;
        // rating = ((humorLevel * 5) / dateEvent.getTensionLevel()) + (romanceLevel / 3) + bonuses;
    }
}