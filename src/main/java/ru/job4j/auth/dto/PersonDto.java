package ru.job4j.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.job4j.auth.util.Operation;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDto {
    @NotNull
    @Min(value = 1, message = "Id must be non null", groups = {Operation.OnPassUpdate.class})
    int id;
    @NotBlank(message = "Password must be not empty", groups = {Operation.OnPassUpdate.class})
    String password;
}
