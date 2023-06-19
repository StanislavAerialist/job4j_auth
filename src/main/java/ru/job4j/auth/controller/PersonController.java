package ru.job4j.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.dto.PersonDto;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.service.PersonService;
import ru.job4j.auth.util.PassEncoderHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {
    private final PersonService persons;
    private PassEncoderHandler encoder;
    private static final Logger LOG = LogManager.getLogger(PersonController.class.getName());
    private final ObjectMapper objectMapper;

    @GetMapping("/all")
    public List<Person> findAll() {
        return this.persons.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = this.persons.findById(id);
        if (person.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Person not found.");
        }
        return new ResponseEntity<>(person.get(), HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Person> create(@RequestBody Person person) {
        var login = person.getLogin();
        var password = person.getPassword();
        if (login == null || password == null) {
            throw new NullPointerException("Login and password can't be null");
        }
        if (login.length() < 3) {
            throw new IllegalArgumentException("Invalid login. Login length can't be less than 3 characters.");
        }
        if (password.length() < 3) {
            throw new IllegalArgumentException("Invalid password. Password length can't be less than 3 characters.");
        }
        person.setPassword(encoder.passwordEncoder().encode(person.getPassword()));
        var isCreated = persons.save(person);
        if (isCreated.isEmpty()) {
            return new ResponseEntity<Person>(new Person(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<Person>(
                isCreated.get(),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        var isUpdated = persons.update(person);
        if (!isUpdated) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        var isDeleted = persons.delete(person);
        if (!isDeleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
        LOG.error(e.getLocalizedMessage());
    }

    @PatchMapping("/passUpdate")
    public ResponseEntity<Void> partUpdate(@RequestBody PersonDto personDTO) {
        personDTO.setPassword(encoder.passwordEncoder().encode(personDTO.getPassword()));
        var passUpdate = persons.passUpdate(personDTO);
        if (!passUpdate) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }
}