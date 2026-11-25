package co.com.crediya.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private final Long id;
    private final String nombre;
    private final String apellido;
    private final LocalDate fechaNacimiento;
    private final String direccion;
    private final String telefono;
    private final String email;
    private final String documentoIdentidad;
    private final BigDecimal salarioBase;
}
