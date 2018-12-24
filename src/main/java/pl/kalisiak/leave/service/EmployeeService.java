package pl.kalisiak.leave.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pl.kalisiak.leave.DTO.EducationDTO;
import pl.kalisiak.leave.DTO.EmployeeDTO;
import pl.kalisiak.leave.DTO.EmployeeRegistrationDTO;
import pl.kalisiak.leave.DTO.LeaveForYearDTO;
import pl.kalisiak.leave.DTO.WorkExperienceDTO;
import pl.kalisiak.leave.domain.LeaveCalculator;
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

    @Autowired
    private LeaveCalculator leaveCalculator;

    public EmployeeDTO findById(Long id, boolean calculateLeave) throws NoSuchEmployeeException {
        Employee employee = repository.findById(id).orElse(null);
        if (employee == null)
            throw new NoSuchEmployeeException("No employee with given id");
        return modelToDTO(employee, calculateLeave);
    }

    public EmployeeDTO findByEmail(String email, boolean calculateLeave) throws NoSuchEmployeeException {
        Employee employee = repository.findByEmail(email).orElse(null);
        if (employee == null)
            throw new NoSuchEmployeeException("No employee with given email");
        return modelToDTO(employee, calculateLeave);
    }

    public Set<EmployeeDTO> findAllBySupervisorId(Long id, boolean calculateLeave) throws NoSuchEmployeeException {
        Employee supervisor = repository.findById(id).orElse(null);
        if (supervisor == null)
            throw new NoSuchEmployeeException("No employee with given id");
        return modelToDTOAll(repository.findAllBySupervisor(supervisor), calculateLeave);
    }

    public Set<EmployeeDTO> findAll(boolean calculateLeave) {
        return modelToDTOAll(repository.findAll(), calculateLeave);
    }

    @Transactional(rollbackFor = Exception.class)
    public EmployeeDTO register(EmployeeRegistrationDTO newEmployeeDTO, boolean calculateLeave) throws EmailAlreadyTakenException, SupervisorMissingException, FinishBeforeStartException {
        if (newEmployeeDTO.getEmploymentFinishDate() != null && newEmployeeDTO.getEmploymentFinishDate().isBefore(newEmployeeDTO.getEmploymentStartDate()))
            throw new FinishBeforeStartException("Finish date cannot be before start date");
        if (repository.findByEmail(newEmployeeDTO.getEmail()).orElse(null) != null)
            throw new EmailAlreadyTakenException("An employee with this email already exists");
        Employee employee = registrationDTOToModel(newEmployeeDTO);
        if (!employee.getRoles().contains(Role.CEO) && employee.getSupervisor() == null)
            throw new SupervisorMissingException("The employee was not assigned a supervisor");
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employee.addRole(Role.EMPLOYEE);
        employee = repository.save(employee);
        return modelToDTO(employee, calculateLeave);
    }

    @Transactional(rollbackFor = Exception.class)
    public EmployeeDTO updateEmployee(EmployeeDTO employeeDTO, boolean calculateLeave) throws NoSuchEmployeeException, FinishBeforeStartException, SupervisorMissingException {
        if (employeeDTO.getEmploymentFinishDate() != null && employeeDTO.getEmploymentFinishDate().isBefore(employeeDTO.getEmploymentStartDate()))
            throw new FinishBeforeStartException("Finish date cannot be before start date");
        Employee employee = repository.findById(employeeDTO.getId()).orElse(null);
        if (employee == null)
            throw new NoSuchEmployeeException("No employee with given id");
        Employee supervisor = employeeDTO.getSupervisorId() != null ? repository.findById(employeeDTO.getSupervisorId()).orElse(null) : null;
        if (!employeeDTO.getRoles().contains(Role.CEO) && supervisor == null)
            throw new SupervisorMissingException("The employee was not assigned a supervisor");
        employee.setFirstname(employeeDTO.getFirstname());
        employee.setLastname(employeeDTO.getLastname());
        employee.setEmail(employeeDTO.getEmail());
        employee.setRoles(employeeDTO.getRoles());
        employee.setDepartment(employeeDTO.getDepartment());
        employee.setSupervisor(supervisor);
        employee.setEmploymentStartDate(employeeDTO.getEmploymentStartDate());
        employee.setEmploymentFinishDate(employeeDTO.getEmploymentFinishDate());
        employee = repository.save(employee);
        return modelToDTO(employee, calculateLeave);
    }

    @Transactional(rollbackFor = Exception.class)
    public EmployeeDTO addEducationToEmployee(EmployeeDTO employeeDTO, EducationDTO educationDTO, boolean calculateLeave) throws NoSuchEmployeeException, FinishBeforeStartException {
        if (educationDTO.getFinishDate().isBefore(educationDTO.getStartDate()))
            throw new FinishBeforeStartException("Finish date cannot be before start date");
        Employee employee = repository.findById(employeeDTO.getId()).orElse(null);
        if (employee == null) 
            throw new NoSuchEmployeeException("No employee with given id");
        Education education = educationService.dtoToModel(educationDTO);
        employee.setEducation(education);
        employee = repository.save(employee);
        return modelToDTO(employee, calculateLeave);
    }

    @Transactional(rollbackFor = Exception.class)
    public EmployeeDTO addExperienceToEmployee(EmployeeDTO employeeDTO, WorkExperienceDTO experienceDTO, boolean calculateLeave) throws NoSuchEmployeeException, FinishBeforeStartException {
        if (experienceDTO.getFinishDate().isBefore(experienceDTO.getStartDate()))
            throw new FinishBeforeStartException("Finish date cannot be before start date");
        Employee employee = repository.findById(employeeDTO.getId()).orElse(null);
        if (employee == null) 
            throw new NoSuchEmployeeException("No employee with given id");
        WorkExperience experience = workExperienceService.dtoToModel(experienceDTO);
        employee.addWorkExperience(experience);
        employee = repository.save(employee);
        return modelToDTO(employee, calculateLeave);
    }

    public Employee registrationDTOToModel(EmployeeRegistrationDTO registrationDTO) {
        Employee employee = new Employee();
        employee.setFirstname(registrationDTO.getFirstname());
        employee.setLastname(registrationDTO.getLastname());
        employee.setPassword(registrationDTO.getPassword());
        employee.setEmail(registrationDTO.getEmail());
        employee.setRoles(registrationDTO.getRoles());
        employee.setDepartment(registrationDTO.getDepartment());
        employee.setSupervisor(repository.findById(registrationDTO.getSupervisorId()).orElse(null));
        employee.setEmploymentStartDate(registrationDTO.getEmploymentStartDate());
        employee.setEmploymentFinishDate(registrationDTO.getEmploymentFinishDate());
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
        employee.setEmploymentStartDate(employeeDTO.getEmploymentStartDate());
        employee.setEmploymentFinishDate(employeeDTO.getEmploymentFinishDate());
        return employee;
    }

    public EmployeeDTO modelToDTO(Employee employee, boolean calculateLeave) {
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
        employeeDTO.setEmploymentStartDate(employee.getEmploymentStartDate());
        employeeDTO.setEmploymentFinishDate(employee.getEmploymentFinishDate());
        if (calculateLeave) {
            List<LeaveForYearDTO> leavesForYears = new LinkedList<>();
            List<Integer> calculatedMinutesForYears = leaveCalculator.getMinutesLeftForEmployeeOnDate(employee, LocalDate.now());
            for (int i = 0, year = LocalDate.now().getYear() - calculatedMinutesForYears.size() + 1; i < calculatedMinutesForYears.size(); i++, year++) {
                leavesForYears.add(new LeaveForYearDTO(year, calculatedMinutesForYears.get(i)));
            }
            employeeDTO.setLeavesForYears(leavesForYears);
        }
        return employeeDTO;
    }

    public Set<Employee> dtoToModelAll(Collection<EmployeeDTO> employeesDTO) {
        if (employeesDTO == null)
            return null;
        return employeesDTO.stream()
            .map(employeeDTO -> dtoToModel(employeeDTO))
            .collect(Collectors.toSet());
    }

    public Set<EmployeeDTO> modelToDTOAll(Collection<Employee> employees, boolean calculateLeave) {
        if (employees == null)
            return null;
        return employees.stream()
            .map(employee -> modelToDTO(employee, calculateLeave))
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