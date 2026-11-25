package co.com.crediya.api;

import co.com.crediya.api.dto.ErrorResponse;
import co.com.crediya.api.dto.RegisterUserRequest;
import co.com.crediya.api.dto.UserResponse;
import co.com.crediya.api.mapper.UserDtoMapper;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.registrarusuario.RegistrarUsuarioUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Registro de usuarios de CrediYa")
public class UserController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Registrar usuario")
    @ApiResponse(responseCode = "201", description = "Usuario registrado",
            content = @Content(schema = @Schema(implementation = UserResponse.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "El correo ya está registrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Error inesperado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UserResponse> registrar(@RequestBody RegisterUserRequest request) {
        log.info("Inicia registro de usuario");
        User usuarioRegistrado = registrarUsuarioUseCase.registrar(UserDtoMapper.toDomain(request));
        log.info("Usuario registrado exitosamente con id={}", usuarioRegistrado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDtoMapper.toResponse(usuarioRegistrado));
    }
}
