package pl.kalisiak.leave.DTO;

import java.time.LocalDate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
public class WorkExperienceDTO extends GenericDTO {
	
	@NotEmpty
	private String companyName;
	
	@NotNull
	private LocalDate startDate;
	
	@NotNull
	private LocalDate finishDate;

	@NotNull
	@ToString.Exclude
	private EmployeeDTO employee;
	
}
