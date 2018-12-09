package pl.kalisiak.leave.DTO;

import java.time.LocalDate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.kalisiak.leave.model.EducationLevel;

@Data
@EqualsAndHashCode(callSuper = true)
public class EducationDTO extends GenericDTO {
	
	@NotEmpty
	private String schoolName;
	
	@NotNull
	private LocalDate startDate;
	
	@NotNull
	private LocalDate finishDate;
    
    @NotNull
	private EducationLevel level;

	@NotNull
	@ToString.Exclude
	private EmployeeDTO employee;
	
}