package bg.sofia.uni.fmi.mjt.jobmatch.comparators;

import bg.sofia.uni.fmi.mjt.jobmatch.model.match.SkillRecommendation;

import java.util.Comparator;

public class ComparatorSkillRecommendation implements Comparator<SkillRecommendation> {

    @Override
    public int compare(SkillRecommendation o1, SkillRecommendation o2) {
        int cmp = Double.compare(o1.improvementScore(), o2.improvementScore());

        if (cmp != 0) {
            return cmp;
        }

        return o2.skillName().compareTo(o1.skillName());
    }
}