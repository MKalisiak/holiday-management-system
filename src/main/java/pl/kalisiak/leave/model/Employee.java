package pl.kalisiak.leave.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Employee extends GenericModel {

	@NotEmpty
	private String firstname;

	@NotEmpty
	private String lastname;

	@Column(unique = true)
	@NotEmpty
	@Email
	private String email;

	@NotEmpty
	private String password;
	
	@ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "roles", joinColumns = @JoinColumn(name = "employee_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
	private Set<Role> roles;

	@OneToOne(mappedBy = "employee")
	@Cascade(CascadeType.ALL)
	@EqualsAndHashCode.Exclude
	private Education education;
	
	@OneToMany(mappedBy = "employee", fetch = FetchType.EAGER)
	@Cascade(CascadeType.ALL)
	@EqualsAndHashCode.Exclude
	private Set<WorkExperience> workExperience;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private Department department;

	@EqualsAndHashCode.Exclude
	@ManyToOne
	@JoinColumn(name = "supervisor_id")
	private Employee supervisor;

	public void setEducation(Education education) {
		if (education != null)
			education.setEmployee(this);
		this.education = education;
	}

	public void addWorkExperience(WorkExperience experience) {
		if (experience == null)
			return;
		if (this.workExperience == null)
			this.workExperience = new HashSet<>();
		experience.setEmployee(this);
		this.workExperience.add(experience);
	}

	public void removeWorkExperience(WorkExperience experience) {
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
