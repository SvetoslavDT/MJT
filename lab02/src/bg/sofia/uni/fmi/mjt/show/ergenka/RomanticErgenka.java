package bg.sofia.uni.fmi.mjt.show.ergenka;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;

public class RomanticErgenka extends ErgenkaBase {

    private final String favouriteDateLocation;

    public RomanticErgenka(String name, short age, int romanceLevel, int humorLevel, int rating, String favoriteDateLocation) {
        super(name, age, romanceLevel, humorLevel, rating);
        this.favouriteDateLocation = favoriteDateLocation;
    }

    @Override
    public void reactToDate(DateEvent dateEvent) {
        if (dateEvent == null) {
            return;
        }

        // Should it be else if or if. Are the bonuses complementing???
        int bonuses = 0;
        if (dateEvent.getLocation().equalsIgnoreCase(favouriteDateLocation)) {
            bonuses += 5;
        }
        else if (dateEvent.getDuration() < 30) {
            bonuses -= 3;
        } else if (dateEvent.getDuration() > 120) {
            bonuses -= 2;
        }

        int tension = Math.max(1, dateEvent.getTensionLevel());
        int part1 = Math.floorDiv(romanceLevel * 7, tension);
        int part2 = Math.floorDiv(humorLevel, 3);

        rating += part1 + part2 + bonuses;
        // rating = ((romanceLevel * 7) / dateEvent.getTensionLevel()) + (humorLevel / 3) + bonuses;
    }
}