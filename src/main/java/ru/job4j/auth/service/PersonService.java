package ru.job4j.auth.service;

import ru.job4j.auth.dto.PersonDto;
import ru.job4j.auth.model.Person;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    Optional<Person> save(Person person);

    boolean update(Person person);

    boolean delete(Person person);

    List<Person> findAll();

    Optional<Person> findById(int id);

    Optional<Person> findByLogin(String login);

    boolean passUpdate(PersonDto personDto);
}
