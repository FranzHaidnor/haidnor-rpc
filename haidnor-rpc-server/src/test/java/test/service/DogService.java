package test.service;

import test.model.Dog;

public interface DogService {

    int getAge(Long dogId);

    Dog getDog(Long dogId);

}
