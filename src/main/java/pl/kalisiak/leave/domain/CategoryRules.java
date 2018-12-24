package pl.kalisiak.leave.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.kalisiak.leave.model.Education;
import pl.kalisiak.leave.model.EducationLevel;
import pl.kalisiak.leave.model.Employee;

public class CategoryRules {
    private static final int FIRST_CATEGORY_DAYS = 20;
    private static final int SECOND_CATEGORY_DAYS = 26;

    private static final int BASIC_VOCATIONAL_YEARS = 3;
    private static final int VOCATIONAL_SECONDARY_YEARS = 5;
    private static final int GENERAL_SECONDARY_YEARS = 4;
    private static final int POST_SECONDARY_YEARS = 6;
    private static final int UNIVERSITY_YEARS = 8;

    private static final int DAYS_PER_YEAR = 365;
    public static final int HOURS_PER_DAY = 8;

    private static final int SENIORITY_YEARS_BETWEEN_CATEGORIES = 10;
    private static final int SENIORITY_DAYS_BETWEEN_CATEGORIES = SENIORITY_YEARS_BETWEEN_CATEGORIES * DAYS_PER_YEAR;

    public enum Category {
        FIRST,
        SECOND
    }

    private static final Map<Category, Integer> daysForCategory;
    static {
        daysForCategory = new EnumMap<>(Category.class);
        daysForCategory.put(Category.FIRST, FIRST_CATEGORY_DAYS);
        daysForCategory.put(Category.SECOND, SECOND_CATEGORY_DAYS);
    }
    
    public static Integer getLeaveDaysForCategory(Category category) {
        return daysForCategory.get(category);
    }

    public static Integer getLeaveMinutesForCategory(Category category) {
        return daysForCategory.get(category) * HOURS_PER_DAY * 60;
    }

    private static final Map<EducationLevel, Integer> educationToSeniorityYears;
    static {
        educationToSeniorityYears = new EnumMap<>(EducationLevel.class);
        educationToSeniorityYears.put(EducationLevel.BASIC_VOCATIONAL, BASIC_VOCATIONAL_YEARS);
        educationToSeniorityYears.put(EducationLevel.VOCATIONAL_SECONDARY, VOCATIONAL_SECONDARY_YEARS);
        educationToSeniorityYears.put(EducationLevel.GENERAL_SECONDARY, GENERAL_SECONDARY_YEARS);
        educationToSeniorityYears.put(EducationLevel.POST_SECONDARY, POST_SECONDARY_YEARS);
        educationToSeniorityYears.put(EducationLevel.UNIVERSITY, UNIVERSITY_YEARS);
    }

    @Getter
    @RequiredArgsConstructor
    private static class PeriodOfTime {
        private final LocalDate startDate;
        private final LocalDate endDate;
    }

    public static LocalDate getCategoryChangeDate(Employee employee) {
        int totalDaysWorked = 0;           

        Comparator<PeriodOfTime> comparator = Comparator.comparing(PeriodOfTime::getStartDate).thenComparing(PeriodOfTime::getEndDate, Comparator.reverseOrder());
        List<PeriodOfTime> periodsWorked = employee.getWorkExperience() == null ? Collections.emptyList() : employee.getWorkExperience().stream()
                                                                       .map(experience -> new PeriodOfTime(experience.getStartDate(), experience.getFinishDate()))
                                                                       .sorted(comparator)
                                                                       .collect(Collectors.toList());

        Education education = employee.getEducation(); 
        PeriodOfTime educationPeriod = education != null ? new PeriodOfTime(education.getStartDate(), education.getFinishDate()) : null;
        boolean educationCounted = false;

        LocalDate lastDateAnalyzed = getInitialDate(educationPeriod, periodsWorked, employee.getEmploymentStartDate());

        for (PeriodOfTime workPeriod : periodsWorked) {
            if (workPeriod.getStartDate().isBefore(lastDateAnalyzed) && workPeriod.getEndDate().isBefore(lastDateAnalyzed))
                continue;

            if (educationPeriod != null &&
            		(lastDateAnalyzed.isBefore(educationPeriod.getEndDate()) || lastDateAnalyzed.isEqual(educationPeriod.getEndDate())) && 
            		workPeriod.getEndDate().isAfter(educationPeriod.getStartDate()) && !educationCounted) {
                if (workPeriod.getStartDate().isBefore(educationPeriod.getStartDate())) {
                    totalDaysWorked += ChronoUnit.DAYS.between(DateUtils.laterDate(lastDateAnalyzed, workPeriod.getStartDate()), educationPeriod.getStartDate());
                }
                totalDaysWorked += educationToSeniorityYears.get(education.getLevel()) * DAYS_PER_YEAR;
                lastDateAnalyzed = educationPeriod.getEndDate();
                educationCounted = true;
            }
            totalDaysWorked += ChronoUnit.DAYS.between(DateUtils.laterDate(lastDateAnalyzed, workPeriod.getStartDate()), workPeriod.getEndDate());
            lastDateAnalyzed = workPeriod.getEndDate();

            if (totalDaysWorked >= SENIORITY_DAYS_BETWEEN_CATEGORIES)
                break;
        }

        // education was not analyzed and still not enough days worked
        if (educationPeriod != null && 
                (lastDateAnalyzed.isBefore(educationPeriod.getStartDate()) || lastDateAnalyzed.isEqual(educationPeriod.getStartDate())) &&
                !educationCounted && totalDaysWorked < SENIORITY_DAYS_BETWEEN_CATEGORIES) {
        	totalDaysWorked += educationToSeniorityYears.get(education.getLevel()) * DAYS_PER_YEAR;
        	lastDateAnalyzed = educationPeriod.getEndDate();
        }
        	
        
        if (totalDaysWorked < SENIORITY_DAYS_BETWEEN_CATEGORIES)
        	return employee.getEmploymentStartDate().plusDays((long)SENIORITY_DAYS_BETWEEN_CATEGORIES - totalDaysWorked);
        
        if (educationPeriod != null && lastDateAnalyzed.isEqual(educationPeriod.getEndDate()))
            return lastDateAnalyzed;
            
        return lastDateAnalyzed.minusDays((long)totalDaysWorked - SENIORITY_DAYS_BETWEEN_CATEGORIES);
    }

    private static LocalDate getInitialDate(PeriodOfTime educationPeriod, List<PeriodOfTime> periodsWorked, LocalDate employmentStartDate) {
        if (educationPeriod == null && periodsWorked.isEmpty())
            return employmentStartDate;

        if (educationPeriod == null)
            return periodsWorked.get(0).getStartDate();

        if (periodsWorked.isEmpty())
            return educationPeriod.getStartDate();

        if (educationPeriod.getEndDate().isBefore(periodsWorked.get(0).getStartDate()))
            return educationPeriod.getStartDate();
        else
            return periodsWorked.get(0).getStartDate();
    }

 
}