package pl.kalisiak.leave.DTO;

import java.time.LocalDate;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.kalisiak.leave.model.Employee;
import pl.kalisiak.leave.model.RequestStatus;

@Data
@EqualsAndHashCode(callSuper = true)
public class LeaveDTO extends GenericDTO {

	@NotNull
	private LocalDate startDate;
	
	@NotNull
	private LocalDate finishDate;

	@ManyToOne
	@JoinColumn(name = "employee_id")
	@NotNull
	@ToString.Exclude
	private Employee employee;

}
