package pl.kalisiak.leave.DTO;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.kalisiak.leave.model.Department;
import pl.kalisiak.leave.model.Role;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmployeeDTO extends GenericDTO {
	
	@NotEmpty
	private String firstname;
	
	@NotEmpty
	private String lastname;
	
	@NotEmpty
	@Email
	private String email;
	
	private Set<Role> roles;

	@EqualsAndHashCode.Exclude
	private EducationDTO education;
	
	@EqualsAndHashCode.Exclude
	private Set<WorkExperienceDTO> workExperience;

	@NotNull
	private Department department;

	private Long supervisorId;
	
	public void setEducation(EducationDTO education) {
		if (education != null) 
			education.setEmployee(this);
		this.education = education;
	}

	public void addWorkExperience(WorkExperienceDTO experience) {
		if (experience == null)
			return;
		if (this.workExperience == null)
			this.workExperience = new HashSet<>();
		experience.setEmployee(this);
		this.workExperience.add(experience);
	}

	public void removeWorkExperience(WorkExperienceDTO experience) {
		if (experience == null)
			return;
		experience.setEmployee(null);
		this.workExperience.remove(experience);
	}
	
	public void addRole(Role role) {
		if (role == null)
			return;
		if (this.roles == null)
			this.roles = new HashSet<>();
		this.roles.add(role);
	}

	public void removeRole(Role role) {
		this.roles.remove(role);
	}

}
