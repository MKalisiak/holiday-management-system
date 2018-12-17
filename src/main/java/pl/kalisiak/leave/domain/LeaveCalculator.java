package pl.kalisiak.leave.domain;

import java.util.LinkedList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import pl.kalisiak.leave.model.Employee;

@Service
public class LeaveCalculator {

    private static final int YEARS_TAKEN = 4;

    public int getMinutesLeftForEmployee(Employee employee) {
        LinkedList<Integer> minutesForYears = new LinkedList<>();
        for(int i = 0; i < YEARS_TAKEN; i++)
            minutesForYears.push(0);

        for(int year = employee.getEmploymentStartDate().getYear(); year <= employee.getEmploymentFinishDate().getYear(); year++) {
            updateYearsList(employee, minutesForYears, year);
        }

        return minutesForYears.stream().collect(Collectors.summingInt(Integer::intValue));
    }

    private void updateYearsList(Employee employee, LinkedList<Integer> minutesForYears, int year) {
        // TODO od najstarszego roku w ktorym jest jeszcze dostepny urlop odejmowac zuzyty
    }

    private int getCategoryMinutesForYear(Employee employee, int year) {
        // TODO przemyslec...
        return 0;
    }

    // TODO lista n-elementowa (n == liczba lat z ktorych sie liczy urlop)
    // i idac po latach odejmuje od pierwszego niezerowego elementu, a po zmianie roku odrzucam pierwszy element i na koniec wpycham nowy
}