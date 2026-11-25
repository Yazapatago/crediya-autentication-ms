package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.RegisterUserRequest;
import co.com.crediya.api.dto.UserResponse;
import co.com.crediya.model.user.User;

public final class UserDtoMapper {

    private UserDtoMapper() {
    }

    public static User toDomain(RegisterUserRequest request) {
        if (request == null) {
            return null;
        }
        return User.builder()
                .nombre(request.nombre())
                .apellido(request.apellido())
                .fechaNacimiento(request.fechaNacimiento())
                .direccion(request.direccion())
                .telefono(request.telefono())
                .email(request.email())
                .documentoIdentidad(request.documentoIdentidad())
                .salarioBase(request.salarioBase())
                .build();
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getNombre(),
                user.getApellido(),
                user.getFechaNacimiento(),
                user.getDireccion(),
                user.getTelefono(),
                user.getEmail(),
                user.getDocumentoIdentidad(),
                user.getSalarioBase()
        );
    }
}
