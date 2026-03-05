package bg.sofia.uni.fmi.mjt.show;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;
import bg.sofia.uni.fmi.mjt.show.elimination.EliminationRule;
import bg.sofia.uni.fmi.mjt.show.elimination.LowAttributeSumEliminationRule;
import bg.sofia.uni.fmi.mjt.show.elimination.LowestRatingEliminationRule;
import bg.sofia.uni.fmi.mjt.show.elimination.PublicVoteEliminationRule;
import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;
import bg.sofia.uni.fmi.mjt.show.ergenka.HumorousErgenka;
import bg.sofia.uni.fmi.mjt.show.ergenka.RomanticErgenka;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        // Създаваме няколко участнички
        Ergenka a = new HumorousErgenka("Alice", (short)25, /*romance*/ 2, /*humor*/ 8, /*rating*/ 10);
        Ergenka b = new RomanticErgenka("Bella", (short)24, /*romance*/ 9, /*humor*/ 3, /*rating*/ 7, "cinema");
        Ergenka c = new HumorousErgenka("Clara", (short)27, /*romance*/ 1, /*humor*/ 4, /*rating*/ 5);
        Ergenka d = new RomanticErgenka("Diana", (short)23, /*romance*/ 4, /*humor*/ 2, /*rating*/ 3, "theatre");

        Ergenka[] initial = new Ergenka[] { a, b, c, d };

        // Дефолтни правила за елиминация (използваме няколко различни)
        EliminationRule[] defaultRules = new EliminationRule[] {
                new LowestRatingEliminationRule(),      // ще тества логиката за най-нисък рейтинг
                new LowAttributeSumEliminationRule(3)   // премахва тези с романтика<3 AND хумор<3
        };

        ShowAPIImpl show = new ShowAPIImpl(initial, defaultRules);

        printlnHeader("Начално състояние на ергенките");
        printErgenkas(show.getErgenkas());

        // Организираме обща дата (влияе на всички)
        DateEvent cinemaDate = new DateEvent("cinema", 2, 60); // location, tension, duration
        System.out.println("\nСтартираме рунд (playRound) с дата: cinema, tension=2, duration=60");
        show.playRound(cinemaDate);

        printlnHeader("След playRound (оценки)");
        printErgenkasWithRatings(show.getErgenkas());

        // Организираме индивидуална дата за Diana (organizeDate)
        DateEvent shortDate = new DateEvent("cafe", 5, 20);
        System.out.println("\nОрганизираме индивидуална кратка дата за Diana (20 минути) => organizeDate");
        show.organizeDate(d, shortDate);

        printlnHeader("След individual organizeDate (Diana)");
        printErgenkasWithRatings(show.getErgenkas());

        // Тестваме елиминация с предадени правила (null за да използва дефолтните в ShowAPIImpl)
        System.out.println("\nИзвикваме eliminateErgenkas(null) — ще се използват дефолтните правила, подадени в конструктора");
        show.eliminateErgenkas(null);

        printlnHeader("След eliminateErgenkas(null) (останалите участнички)");
        printErgenkasWithRatings(show.getErgenkas());

        // Нека построим ново шоу с всички участнички и да тестваме PublicVoteEliminationRule
        System.out.println("\n--- Тест на PublicVoteEliminationRule (гласуване) ---");
        Ergenka[] all = new Ergenka[] { a, b, c, d };
        ShowAPIImpl show2 = new ShowAPIImpl(all, new EliminationRule[0]); // без дефолтни правила

        // Създаваме гласове — мнозинството е за "Alice"
        String[] votes = new String[] { "Alice", "Alice", "Alice", "Bella", "Clara" };
        PublicVoteEliminationRule publicRule = new PublicVoteEliminationRule(votes);

        System.out.println("Гласове: " + Arrays.toString(votes));
        Ergenka[] afterPublic = publicRule.eliminateErgenkas(show2.getErgenkas());

        printlnHeader("Резултат след PublicVoteEliminationRule (премахнати тези с име 'Alice' ако мнозинство)");
        printErgenkaNames(afterPublic);

        // Тест на LowAttributeSumEliminationRule директно
        System.out.println("\n--- Тест на LowAttributeSumEliminationRule (threshold = 5) ---");
        LowAttributeSumEliminationRule lowAttrRule = new LowAttributeSumEliminationRule(5);
        Ergenka[] afterLowAttr = lowAttrRule.eliminateErgenkas(all);

        System.out.println("Премахнати участнички (humor < 5 AND romance < 5):");
        printErgenkaNames(afterLowAttr);

        System.out.println("\nКрай на тестовете.");
        System.out.println("\nЗАБЕЛЕЖКА: Ако резултатите от LowestRatingEliminationRule изглеждат обърнати\n(елиминира най-висок рейтинг вместо най-нисък), това е познат проблем в имплементацията на класа.");
    }

    private static void printErgenkas(Ergenka[] arr) {
        if (arr == null || arr.length == 0) {
            System.out.println("(няма ергенки)");
            return;
        }
        for (Ergenka e : arr) {
            System.out.printf("Name: %s, Age: %d, Romance: %d, Humor: %d, Rating: %d%n",
                    e.getName(), e.getAge(), e.getRomanceLevel(), e.getHumorLevel(), e.getRating());
        }
    }

    private static void printErgenkasWithRatings(Ergenka[] arr) {
        if (arr == null || arr.length == 0) {
            System.out.println("(няма ергенки)");
            return;
        }
        for (Ergenka e : arr) {
            System.out.printf("%s -> rating = %d%n", e.getName(), e.getRating());
        }
    }

    private static void printErgenkaNames(Ergenka[] arr) {
        if (arr == null || arr.length == 0) {
            System.out.println("(няма ергенки)");
            return;
        }
        for (Ergenka e : arr) {
            System.out.println(e.getName());
        }
    }

    private static void printlnHeader(String h) {
        System.out.println("\n=== " + h + " ===");
    }
}