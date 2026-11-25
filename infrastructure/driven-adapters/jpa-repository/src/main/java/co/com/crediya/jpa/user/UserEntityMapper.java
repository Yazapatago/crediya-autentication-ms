package co.com.crediya.jpa.user;

import co.com.crediya.model.user.User;

public final class UserEntityMapper {

    private UserEntityMapper() {
    }

    public static UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .nombre(user.getNombre())
                .apellido(user.getApellido())
                .fechaNacimiento(user.getFechaNacimiento())
                .direccion(user.getDireccion())
                .telefono(user.getTelefono())
                .email(user.getEmail())
                .documentoIdentidad(user.getDocumentoIdentidad())
                .salarioBase(user.getSalarioBase())
                .build();
    }

    public static User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .fechaNacimiento(entity.getFechaNacimiento())
                .direccion(entity.getDireccion())
                .telefono(entity.getTelefono())
                .email(entity.getEmail())
                .documentoIdentidad(entity.getDocumentoIdentidad())
                .salarioBase(entity.getSalarioBase())
                .build();
    }
}
