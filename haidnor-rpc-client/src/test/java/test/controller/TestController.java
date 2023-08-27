package test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.model.Dog;
import test.service.DogService;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private DogService dogService;

    @RequestMapping("/getAge")
    public String getAge() {
        int age = dogService.getAge(1L);
        return String.valueOf(age);
    }

    @RequestMapping("/getDog")
    public String getDog(Long dogId) {
        Dog dog = dogService.getDog(dogId);
        return dog.toString();
    }

}
