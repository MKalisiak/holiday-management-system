package pl.kalisiak.leave.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaveForYearDTO {

    private int year;

    private int minutes;

}