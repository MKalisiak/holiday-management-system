package pl.kalisiak.leave.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.kalisiak.leave.domain.CategoryRules;
import pl.kalisiak.leave.domain.CategoryRules.Category;
import pl.kalisiak.leave.model.Employee;
import pl.kalisiak.leave.model.Leave;
import pl.kalisiak.leave.model.WorkExperience;
import pl.kalisiak.leave.repository.LeaveRepository;

@Service
public class LeaveCalculator {

    private static final int YEARS_TAKEN = 4;

    @Autowired
    private LeaveRepository leaveRepository;

    public int getMinutesLeftForEmployeeOnDate(Employee employee, LocalDate date) {
        LinkedList<Integer> minutesForYears = new LinkedList<>();

        LocalDate categoryChangeDate = CategoryRules.getCategoryChangeDate(employee);
        boolean firstJob = isFirstJob(employee);

        List<Leave> leaves = leaveRepository.findAllByEmployee(employee)
                                            .stream()
                                            .sorted(Comparator.comparing(Leave::getStartDate).thenComparing(Leave::getFinishDate, Comparator.reverseOrder()))
                                            .collect(Collectors.toList());

        for(int year = employee.getEmploymentStartDate().getYear(); year <= date.getYear(); year++) {
            if (firstJob) 
                updateYearsListForFirstJob(minutesForYears, leaves, year, employee.getEmploymentStartDate(), date);
            else 
                updateYearsList(minutesForYears, leaves, year, employee.getEmploymentStartDate(), categoryChangeDate, date);
        }

        return minutesForYears.stream().collect(Collectors.summingInt(Integer::intValue));
    }

    private void updateYearsList(LinkedList<Integer> minutesForYears, List<Leave> leaves, int year, LocalDate employmentStartDate, LocalDate categoryChangeDate, LocalDate endDate) {
        List<Leave> leavesThisYear = leaves.stream()
                                           .filter(leave -> leave.getStartDate().getYear() == year || leave.getFinishDate().getYear() == year)
                                           .collect(Collectors.toList());

        LocalDate startDate = LocalDate.of(year, Month.JANUARY, 1);
        if (minutesForYears.isEmpty())
            startDate = employmentStartDate;

        Category categoryAtStartDate = startDate.isBefore(categoryChangeDate) ? Category.FIRST : Category.SECOND;
        boolean categoryChanged = categoryAtStartDate == Category.SECOND;

        minutesForYears.addLast(CategoryRules.getLeaveMinutesForCategory(categoryAtStartDate));
        if (minutesForYears.size() > YEARS_TAKEN)
            minutesForYears.removeFirst();

        for (Leave leave : leavesThisYear) {
            int minutesToSubstract = leave.getDurationInMinutes();

            if (!categoryChanged && !leave.getStartDate().isBefore(categoryChangeDate)) {
                appendCategoryDifference(minutesForYears);
                categoryChanged = true;
            }

            for (LocalDate leaveDay = leave.getStartDate(); !leaveDay.isAfter(leave.getFinishDate()); leaveDay = leaveDay.plusDays(1)) {
                if (leaveDay.getYear() != year || isWeekend(leaveDay))
                    continue;
                if (!categoryChanged && !leaveDay.isBefore(categoryChangeDate)) {
                    appendCategoryDifference(minutesForYears);
                    categoryChanged = true;
                }
                int minutesSpentOnLeave = minutesToSubstract > CategoryRules.HOURS_PER_DAY * 60 ? CategoryRules.HOURS_PER_DAY * 60 : minutesToSubstract;
                substractMinutes(minutesForYears, minutesSpentOnLeave);
            }
        }

        if (!categoryChanged && categoryChangeDate.getYear() == year && !categoryChangeDate.isAfter(endDate))
            appendCategoryDifference(minutesForYears);

    }

    private void appendCategoryDifference(LinkedList<Integer> minutesForYears) {
        Integer categoriesDifference = CategoryRules.getLeaveMinutesForCategory(Category.SECOND) - CategoryRules.getLeaveMinutesForCategory(Category.FIRST);
        minutesForYears.set(minutesForYears.size() - 1, minutesForYears.getLast() + categoriesDifference);
    }

    private void substractMinutes(LinkedList<Integer> minutesForYears, int amount) {
        int i = 0;
        while (amount > 0) {
            if (minutesForYears.get(i) > 0) {
                int substractingAmount = amount > minutesForYears.get(i) ? minutesForYears.get(i) : amount;
                minutesForYears.set(i, minutesForYears.get(i) - substractingAmount);
                amount -= substractingAmount;
            }
            i++;
        }
    }

    private void updateYearsListForFirstJob(LinkedList<Integer> minutesForYears, List<Leave> leaves, int year, LocalDate employmentStartDate, LocalDate endDate) {
        
    }

    private boolean isFirstJob(Employee employee) {
        for (WorkExperience experience : employee.getWorkExperience()) {
            if (experience.getStartDate().isBefore(employee.getEmploymentStartDate()))
                return false;
        }
        return true;
    }

    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek().equals(DayOfWeek.SATURDAY) || date.getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }
    // TODO lista n-elementowa (n == liczba lat z ktorych sie liczy urlop)
    // i idac po latach odejmuje od pierwszego niezerowego elementu, a po zmianie roku odrzucam pierwszy element i na koniec wpycham nowy
}