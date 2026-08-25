# HexaTours Web

Aplicación web para HexaTours, desarrollada con Spring Boot y Thymeleaf. Incluye el sitio público de la agencia, autenticación de usuarios y una base administrativa para gestionar usuarios y paquetes turísticos.

## Funcionalidades

- Página principal responsive con video, servicios, beneficios, destinos y preguntas frecuentes.
- Formulario de contacto con redirección a WhatsApp.
- Registro e inicio de sesión con Spring Security.
- Contraseñas protegidas con BCrypt.
- Administración de usuarios y paquetes turísticos.
- Persistencia de datos en MariaDB.
- Gestión de imágenes mediante Cloudinary.
- Configuración segura mediante variables de entorno.

## Tecnologías

- Java 21
- Spring Boot 3.3
- Spring MVC y Thymeleaf
- Spring Security
- Spring Data JPA / Hibernate
- MariaDB
- Cloudinary
- HTML, CSS y JavaScript
- Maven

## Arquitectura

El proyecto utiliza una arquitectura por capas:

```text
Navegador
   ↓
Controladores MVC
   ↓
Servicios
   ↓
Repositorios JPA
   ↓
MariaDB
```

Las vistas se encuentran en `src/main/resources/templates` y los archivos CSS, JavaScript, imágenes y videos en `src/main/resources/static`.

## Configuración local

1. Copia `.env.example` con el nombre `.env.local`.
2. Completa las variables con tus credenciales locales de MariaDB y Cloudinary.
3. Mantén `.env.local` fuera del control de versiones.

Variables principales:

```text
DB_HOST
DB_PORT
DB_NAME
DB_USERNAME
DB_PASSWORD
JPA_DDL_AUTO
APP_REMEMBER_ME_KEY
SESSION_COOKIE_SECURE
CLOUDINARY_URL
```

El usuario de prueba puede configurarse localmente mediante las variables `APP_SEED_*`. No se deben publicar credenciales reales en el repositorio.

## Ejecución

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
./mvnw spring-boot:run
```

Después abre:

```text
http://localhost:8080
```

## Rutas principales

- `/`: sitio público de HexaTours.
- `/login`: inicio de sesión.
- `/registro`: registro de usuarios.
- `/dashboard`: panel administrativo.
- `/dashboard/usuarios`: administración de usuarios.
- `/dashboard/paquetes`: administración de paquetes.

## Pruebas

```powershell
.\mvnw.cmd test
```

## Seguridad

- No publiques `.env.local`.
- No escribas contraseñas o claves API directamente en el código.
- Utiliza HTTPS y cookies seguras en producción.
- Usa `JPA_DDL_AUTO=update` solamente durante el desarrollo.
- Rota cualquier credencial que haya sido expuesta.

## Propiedad

Copyright © 2026 Codeboost. Todos los derechos reservados.

El código y los recursos de este proyecto son propiedad de Codeboost y HexaTours. Su copia, distribución o uso comercial requiere autorización previa.
