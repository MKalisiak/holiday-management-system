package pl.kalisiak.leave.service;

import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import pl.kalisiak.leave.DTO.EducationDTO;
import pl.kalisiak.leave.DTO.EmployeeDTO;
import pl.kalisiak.leave.DTO.EmployeeRegistrationDTO;
import pl.kalisiak.leave.DTO.WorkExperienceDTO;
import pl.kalisiak.leave.exceptions.EmailAlreadyTakenException;
import pl.kalisiak.leave.exceptions.FinishBeforeStartException;
import pl.kalisiak.leave.exceptions.NoSuchEmployeeException;
import pl.kalisiak.leave.exceptions.SupervisorMissingException;
import pl.kalisiak.leave.model.Education;
import pl.kalisiak.leave.model.Employee;
import pl.kalisiak.leave.model.Role;
import pl.kalisiak.leave.model.WorkExperience;
import pl.kalisiak.leave.repository.EmployeeRepository;
import pl.kalisiak.leave.repository.WorkExperienceRepository;

@Service
public class EmployeeService implements UserDetailsService {

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private WorkExperienceRepository experienceRepository;

    @Autowired
    private EducationService educationService;

    @Autowired
    private WorkExperienceService workExperienceService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public EmployeeDTO findById(Long id) throws NoSuchEmployeeException {
        Employee employee = repository.findById(id).orElse(null);
        if (employee == null)
            throw new NoSuchEmployeeException("No employee with given id");
        return modelToDTO(employee);
    }

    public EmployeeDTO findByEmail(String email) throws NoSuchEmployeeException {
        Employee employee = repository.findByEmail(email).orElse(null);
        if (employee == null)
            throw new NoSuchEmployeeException("No employee with given email");
        return modelToDTO(employee);
    }

    public Set<EmployeeDTO> findAllBySupervisorId(Long id) throws NoSuchEmployeeException {
        Employee supervisor = repository.findById(id).orElse(null);
        if (supervisor == null)
            throw new NoSuchEmployeeException("No employee with given id");
        return modelToDTOAll(repository.findAllBySupervisor(supervisor));
    }

    public Set<EmployeeDTO> findAll() {
        return modelToDTOAll(repository.findAll());
    }

    @Transactional
    public EmployeeDTO register(EmployeeRegistrationDTO newEmployeeDTO) throws EmailAlreadyTakenException, SupervisorMissingException {
        if (repository.findByEmail(newEmployeeDTO.getEmail()).orElse(null) != null)
            throw new EmailAlreadyTakenException("An employee with this email already exists");
        Employee employee = registrationDTOToModel(newEmployeeDTO);
        if (!employee.getRoles().contains(Role.CEO) && employee.getSupervisor() == null)
            throw new SupervisorMissingException("The employee was not assigned a supervisor");
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employee.addRole(Role.EMPLOYEE);
        employee = repository.save(employee);
        return modelToDTO(employee);
    }

    @Transactional
    public EmployeeDTO addEducationToEmployee(EmployeeDTO employeeDTO, EducationDTO educationDTO) throws NoSuchEmployeeException, FinishBeforeStartException {
        if (educationDTO.getFinishDate().isBefore(educationDTO.getStartDate()))
            throw new FinishBeforeStartException("Finish date cannot be before start date");
        Employee employee = repository.findById(employeeDTO.getId()).orElse(null);
        if (employee == null) 
            throw new NoSuchEmployeeException("No employee with given id");
        Education education = educationService.dtoToModel(educationDTO);
        employee.setEducation(education);
        employee = repository.save(employee);
        return modelToDTO(employee);
    }

    @Transactional
    public EmployeeDTO addExperienceToEmployee(EmployeeDTO employeeDTO, WorkExperienceDTO experienceDTO) throws NoSuchEmployeeException, FinishBeforeStartException {
        if (experienceDTO.getFinishDate().isBefore(experienceDTO.getStartDate()))
            throw new FinishBeforeStartException("Finish date cannot be before start date");
        Employee employee = repository.findById(employeeDTO.getId()).orElse(null);
        if (employee == null) 
            throw new NoSuchEmployeeException("No employee with given id");
        WorkExperience experience = workExperienceService.dtoToModel(experienceDTO);
        employee.addWorkExperience(experience);
        employee = repository.save(employee);
        return modelToDTO(employee);
    }

    public Employee registrationDTOToModel(EmployeeRegistrationDTO registrationDTO) {
        Employee employee = new Employee();
        employee.setFirstname(registrationDTO.getFirstname());
        employee.setLastname(registrationDTO.getLastname());
        employee.setPassword(registrationDTO.getPassword());
        employee.setEmail(registrationDTO.getEmail());
        if (registrationDTO.getRole() != null)
            employee.addRole(registrationDTO.getRole());
        employee.setDepartment(registrationDTO.getDepartment());
        employee.setSupervisor(repository.findById(registrationDTO.getSupervisorId()).orElse(null));
        return employee;
    }
    
    public Employee dtoToModel(EmployeeDTO employeeDTO) {
        if (employeeDTO == null)
            return null;
        Employee employee = new Employee();
        employee.setId(employeeDTO.getId());
        employee.setFirstname(employeeDTO.getFirstname());
        employee.setLastname(employeeDTO.getLastname());
        employee.setEmail(employeeDTO.getEmail());
        employee.setRoles(employeeDTO.getRoles());
        employee.setEducation(educationService.dtoToModel(employeeDTO.getEducation()));
        employee.setWorkExperience(workExperienceService.dtoToModelAll(employeeDTO.getWorkExperience()));
        employee.setDepartment(employeeDTO.getDepartment());
        employee.setSupervisor(repository.findById(employeeDTO.getSupervisorId()).orElse(null));
        return employee;
    }

    public EmployeeDTO modelToDTO(Employee employee) {
        if (employee == null)
            return null;
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employee.getId());
        employeeDTO.setFirstname(employee.getFirstname());
        employeeDTO.setLastname(employee.getLastname());
        employeeDTO.setEmail(employee.getEmail());
        employeeDTO.setRoles(employee.getRoles());
        employeeDTO.setEducation(educationService.modelToDTO(employee.getEducation()));
        employeeDTO.setWorkExperience(workExperienceService.modelToDTOAll(employee.getWorkExperience()));
        employeeDTO.setDepartment(employee.getDepartment());
        employeeDTO.setSupervisorId(employee.getSupervisor() == null ? null : employee.getSupervisor().getId());
        return employeeDTO;
    }

    public Set<Employee> dtoToModelAll(Collection<EmployeeDTO> employeesDTO) {
        if (employeesDTO == null)
            return null;
        return employeesDTO.stream()
            .map(employeeDTO -> dtoToModel(employeeDTO))
            .collect(Collectors.toSet());
    }

    public Set<EmployeeDTO> modelToDTOAll(Collection<Employee> employees) {
        if (employees == null)
            return null;
        return employees.stream()
            .map(employee -> modelToDTO(employee))
            .collect(Collectors.toSet());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = repository.findByEmail(username).orElse(null);

        if (employee == null) {
            throw new UsernameNotFoundException("User of given username not found");
        }
        return new User(employee.getEmail(), employee.getPassword(), mapRolesToAuthorities(employee.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
            return roles.stream()
                        .map(role -> new SimpleGrantedAuthority(role.toString()))
                        .collect(Collectors.toList());
    }

}