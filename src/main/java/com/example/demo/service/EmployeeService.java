package com.example.demo.service;

import com.example.demo.dto.EmployeeDTO;
import com.example.demo.entity.Employee;
import com.example.demo.repo.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Service layer
 * @search  - to a user
 */
@Service
public class EmployeeService {

  EmployeeService(EmployeeRepository repository){
      this.repository = repository;
  }
    private  EmployeeRepository repository;
    public void add(EmployeeDTO employee){
        Employee employee1 = new Employee();
        employee1.setName(employee.getName());
        employee1.setSurname(employee.getSurname());
        repository.save(employee1);
    }
    public void remove(long id){
        repository.deleteById(id);
    }
    public EmployeeDTO get(long id){
        Employee employeeById = repository.getEmployeeById(id);
        EmployeeDTO n =new EmployeeDTO(employeeById.getName(),employeeById.getSurname());
        return n;
    }
    public List<EmployeeDTO> getAll(){
     List<EmployeeDTO> o = new ArrayList<>();
        List<Employee> all = repository.findAll();
        while(!all.isEmpty()){
            Employee p = all.removeLast();
            o.add(new EmployeeDTO(p.getName(),p.getSurname()));
        }
        return o;
    }

    public boolean exist(long id){
        return repository.existsById(id);
    }

    public EmployeeDTO update(long id, EmployeeDTO dto){
        Employee employeeById = repository.getEmployeeById(id);
        employeeById.setName(dto.getName());
                employeeById.setSurname(dto.getSurname());
        repository.save(employeeById);
      return  dto;
    }


}
