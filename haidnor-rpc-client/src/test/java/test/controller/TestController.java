package test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.service.StudentService;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private StudentService studentService;

    @RequestMapping("/getAge")
    public String test() {
        int age = studentService.getAge(1L);
        return String.valueOf(age);
    }
}
