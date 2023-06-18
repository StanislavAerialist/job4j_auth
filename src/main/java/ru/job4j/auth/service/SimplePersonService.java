package ru.job4j.auth.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.job4j.auth.model.Person;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.job4j.auth.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Service
@AllArgsConstructor
public class SimplePersonService implements PersonService, UserDetailsService {
    private static final Logger LOG = LogManager.getLogger(SimplePersonService.class.getName());
    private final PersonRepository personRepository;

    @Override
    public Optional<Person> save(Person person) {

        Optional<Person> rsl = Optional.empty();
        try {
            personRepository.save(person);
            rsl = Optional.of(person);
        } catch (Exception e) {
            LOG.debug(e.getMessage(), e);
        }
        return rsl;

    }

    @Override
    public boolean update(Person person) {
        boolean rsl = false;
        if (personRepository.existsById(person.getId())) {
            personRepository.save(person);
            rsl = true;
        }
        return rsl;
    }

    @Override
    public boolean delete(Person person) {
        boolean rsl = false;
        if (personRepository.existsById(person.getId())) {
            personRepository.delete(person);
            rsl = true;
        }
        return rsl;
    }

    @Override
    public List<Person> findAll() {
        return personRepository.findAll();
    }

    @Override
    public Optional<Person> findById(int id) {
        return personRepository.findById(id);
    }

    @Override
    public Optional<Person> findByLogin(String login) {
        return personRepository.findByLogin(login);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> user = personRepository.findByLogin(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        return new User(user.get().getLogin(), user.get().getPassword(), emptyList());
    }
}
