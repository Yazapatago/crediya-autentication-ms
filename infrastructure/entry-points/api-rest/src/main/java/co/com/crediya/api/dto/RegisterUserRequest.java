package co.com.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Datos para registrar un usuario")
public record RegisterUserRequest(
        @Schema(example = "Juan", requiredMode = Schema.RequiredMode.REQUIRED) String nombre,
        @Schema(example = "Pérez", requiredMode = Schema.RequiredMode.REQUIRED) String apellido,
        @Schema(example = "1995-04-20") LocalDate fechaNacimiento,
        @Schema(example = "Calle 10 # 20-30, Medellín") String direccion,
        @Schema(example = "3001234567") String telefono,
        @Schema(example = "juan.perez@correo.com", requiredMode = Schema.RequiredMode.REQUIRED) String email,
        @Schema(example = "1020304050") String documentoIdentidad,
        @Schema(example = "3500000", requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal salarioBase
) {
}
