package com.example.demo.controller;

import com.example.demo.dto.EmployeeDTO;
import com.example.demo.entity.Employee;
import com.example.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Krishana dubey
 * @EmployeeController controller
 * @Features list for Crud
 */
@RestController("")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String welcome() {
        return "Welcome Server is running";
    }

    private boolean existEmployee(long id) {
     return employeeService.exist(id);
    }


    @PostMapping("register")
    public String register(@RequestBody EmployeeDTO employee){

        employeeService.add(employee);
        return "Successfully you are added";
    }
    @GetMapping("/search")

    public String searchOnId(@RequestParam("id") long id){
        if(!existEmployee(id)){
            return "Employee does not exist over here";
        }

       return employeeService.get(id).toString();
    }

    @PostMapping("/update")
    public String update(@RequestParam("id") long id,@RequestBody EmployeeDTO employee){
        if(!existEmployee(id)){
            return "Employee does not exist with this id";
        }
       delete(id);
        employeeService.add(employee);
        return "Successfully you added this employee : ";
    }

    @DeleteMapping("/delete")
    public String delete(@RequestParam("id") long id){
        if(!existEmployee(id)){
            return "Employee does not exist with this id";
        }
        employeeService.remove(id);
        return "Successfully you deleted this employee : "+id;
    }

    @GetMapping("/getAllEmployee")
    public List<EmployeeDTO> getAll(){
        return employeeService.getAll();
    }

}
