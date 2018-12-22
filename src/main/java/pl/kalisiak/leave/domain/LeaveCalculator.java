package pl.kalisiak.leave.domain;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.kalisiak.leave.domain.CategoryRules.Category;
import pl.kalisiak.leave.model.Correction;
import pl.kalisiak.leave.model.Employee;
import pl.kalisiak.leave.model.Leave;
import pl.kalisiak.leave.model.WorkExperience;
import pl.kalisiak.leave.repository.CorrectionRepository;
import pl.kalisiak.leave.repository.LeaveRepository;

@Service
public class LeaveCalculator {

    private static final int YEARS_TAKEN = 4;
    private static final int MONTHS_PER_YEAR = 12;

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private CorrectionRepository correctionRepository;

    public int getMinutesLeftForEmployeeOnDate(Employee employee, LocalDate date) {
        LinkedList<Integer> minutesForYears = new LinkedList<>();

        LocalDate categoryChangeDate = CategoryRules.getCategoryChangeDate(employee);
        boolean firstJob = isFirstJob(employee);

        List<Leave> leaves = leaveRepository.findAllByEmployee(employee)
                                            .stream()
                                            .sorted(Comparator.comparing(Leave::getStartDate).thenComparing(Leave::getFinishDate, Comparator.reverseOrder()))
                                            .collect(Collectors.toList());

        List<Correction> corrections = correctionRepository.findAllByEmployee(employee);

        for(int year = employee.getEmploymentStartDate().getYear(); year <= date.getYear(); year++) {
            if (firstJob && year == employee.getEmploymentStartDate().getYear()) 
                updateYearsListForFirstJob(minutesForYears, leaves, corrections, year, employee.getEmploymentStartDate(), DateUtils.earlierDate(date, LocalDate.of(year, Month.DECEMBER, 31)));
            else 
                updateYearsList(minutesForYears, leaves, corrections, year, employee.getEmploymentStartDate(), categoryChangeDate, date);
        }

        return minutesForYears.stream().collect(Collectors.summingInt(Integer::intValue));
    }

    private void updateYearsList(LinkedList<Integer> minutesForYears, List<Leave> leaves, List<Correction> corrections, int year, LocalDate employmentStartDate, LocalDate categoryChangeDate, LocalDate endDate) {
        List<Leave> leavesThisYear = leaves.stream()
                                           .filter(leave -> leave.getStartDate().getYear() == year || leave.getFinishDate().getYear() == year)
                                           .collect(Collectors.toList());

        LocalDate startDate = LocalDate.of(year, Month.JANUARY, 1);
        if (minutesForYears.isEmpty())
            startDate = employmentStartDate;

        Category categoryAtStartDate = startDate.isBefore(categoryChangeDate) ? Category.FIRST : Category.SECOND;
        boolean categoryChanged = categoryAtStartDate == Category.SECOND;

        int initialMinutes = CategoryRules.getLeaveMinutesForCategory(categoryAtStartDate);
        if (employmentStartDate.getYear() == year) {
            initialMinutes = initialMinutes * (MONTHS_PER_YEAR - employmentStartDate.getMonthValue() + 1) / MONTHS_PER_YEAR;
        }

        minutesForYears.addLast(initialMinutes);
        if (minutesForYears.size() > YEARS_TAKEN)
            minutesForYears.removeFirst();

        applyCorrections(minutesForYears, corrections, year);

        for (Leave leave : leavesThisYear) {
            int minutesToSubstract = leave.getDurationInMinutes();

            if (!categoryChanged && !leave.getStartDate().isBefore(categoryChangeDate)) {
                appendCategoryDifference(minutesForYears);
                categoryChanged = true;
            }

            for (LocalDate leaveDay = leave.getStartDate(); !leaveDay.isAfter(leave.getFinishDate()); leaveDay = leaveDay.plusDays(1)) {
                if (leaveDay.getYear() != year || DateUtils.isWeekend(leaveDay))
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

    private void updateYearsListForFirstJob(LinkedList<Integer> minutesForYears, List<Leave> leaves, List<Correction> corrections, int year, LocalDate employmentStartDate, LocalDate endDate) {
        List<Leave> leavesThisYear = leaves.stream()
                                           .filter(leave -> leave.getStartDate().getYear() == year || leave.getFinishDate().getYear() == year)
                                           .collect(Collectors.toList());

        LocalDate startDate = employmentStartDate;

        int yearlyMinutes = CategoryRules.getLeaveMinutesForCategory(Category.FIRST);
        minutesForYears.addLast(0);

        applyCorrections(minutesForYears, corrections, year);

        int[] monthWrapper = {startDate.getMonthValue()};
        for (; monthWrapper[0] <= endDate.getMonthValue(); monthWrapper[0]++) {
            minutesForYears.set(0, minutesForYears.get(0) + (yearlyMinutes / 12));
            List<Leave> leavesThisMonth = leavesThisYear.stream()
                                           .filter(leave -> leave.getStartDate().getMonthValue() == monthWrapper[0] || leave.getFinishDate().getMonthValue() == monthWrapper[0])
                                           .collect(Collectors.toList());
            for (Leave leave : leavesThisMonth) {
                int minutesToSubstract = leave.getDurationInMinutes();

                for (LocalDate leaveDay = leave.getStartDate(); !leaveDay.isAfter(leave.getFinishDate()); leaveDay = leaveDay.plusDays(1)) {
                    if (leaveDay.getYear() != year || leaveDay.getMonthValue() != monthWrapper[0] || DateUtils.isWeekend(leaveDay))
                        continue;
                    int minutesSpentOnLeave = minutesToSubstract > CategoryRules.HOURS_PER_DAY * 60 ? CategoryRules.HOURS_PER_DAY * 60 : minutesToSubstract;
                    substractMinutes(minutesForYears, minutesSpentOnLeave);
                }
            }
        }
    }

    private void applyCorrections(LinkedList<Integer> minutesForYears, List<Correction> corrections, int year) {
        List<Correction> correctionsThisYear = corrections.stream()
                                                          .filter(correction -> correction.getYear() == year)
                                                          .collect(Collectors.toList());

        for (Correction correction : correctionsThisYear)
            minutesForYears.set(minutesForYears.size() - 1, minutesForYears.getLast() + correction.getDurationInMinutes());
    }

    private boolean isFirstJob(Employee employee) {
        if (employee.getWorkExperience() == null)
            return true;
        for (WorkExperience experience : employee.getWorkExperience()) {
            if (experience.getStartDate().isBefore(employee.getEmploymentStartDate()))
                return false;
        }
        return true;
    }

}