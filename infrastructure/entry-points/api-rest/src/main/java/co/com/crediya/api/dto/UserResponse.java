package co.com.crediya.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserResponse(
        Long id,
        String nombre,
        String apellido,
        LocalDate fechaNacimiento,
        String direccion,
        String telefono,
        String email,
        String documentoIdentidad,
        BigDecimal salarioBase
) {
}
