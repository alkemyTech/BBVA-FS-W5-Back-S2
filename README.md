# BBVA-FS-W5-Back-S2
Repositorio Back Squad 2 | BBVA Fullstack Wave 5.

## Equipo de Trabajo

- **Líder Técnico (Alkemy)**: Futrille, Daniel
- **Desarrolladores (BBVA )**:
  - Caggiano, Juan Cruz
  - Cozzani, Hugo
  - Ottoboni, Matias
  - Pereira, Martin

### Notas:
- **Usuarios Admin:** Los primeros 5 usuarios están asignados como **Admin**.
- **Usuarios User:** Los siguientes 5 usuarios están asignados como **User**.
- **Cuentas de cada usuario:** Cada usuario tiene una cuenta en **ARS** y **USD** con un balance inicial de **10,000** y un límite de transacción diferente para cada tipo de moneda.

### Detalle de los Usuarios Admin y User

#### Admins
| ID  | First Name | Last Name   | Email                       | Password               |
|-----|------------|-------------|-----------------------------|------------------------|
| 1   | Pepe       | Giménez     | pepe.gimenez@yopmail.com    | Pepe@2024Gimenez!      |
| 2   | Juan       | Pérez       | juan.perez@yopmail.com      | JuanP@2024Perez!       |
| 3   | Ana        | Martínez    | ana.martinez@yopmail.com    | Ana_M@2024Martinez#    |
| 4   | Carlos     | López       | carlos.lopez@yopmail.com    | Carlos!2024Lopez@      |
| 5   | Marta      | Fernández   | marta.fernandez@yopmail.com | Marta2024_Fernandez!   |

#### Users
| ID  | First Name | Last Name   | Email                         | Password               |
|-----|------------|-------------|-------------------------------|------------------------|
| 11  | Pedro      | Ruiz        | pedro.ruiz@yopmail.com        | Pedro!2024Ruiz#        |
| 12  | María      | García      | maria.garcia@yopmail.com      | Maria@2024Garcia!      |
| 13  | Fernando   | Jiménez     | fernando.jimenez@yopmail.com  | Fernando2024!Jimenez#  |
| 14  | Carmen     | Álvarez     | carmen.alvarez@yopmail.com    | Carmen!2024Alvarez#    |
| 15  | Rafael     | Moreno      | rafael.moreno@yopmail.com     | Rafael2024_Moreno!     |
---

----------------------------------------------------------------------

# API Documentation

## Endpoints de UserController

---

### 1. **GET `/users`**

#### Descripción:
Devuelve una lista con todos los usuarios.

#### Autenticación:
- **Requerida**: Si

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `200`  | Lista de usuarios obtenida exitosamente. |

#### Ejemplo de Request:
```http
GET /users HTTP/1.1
Host: api.example.com
```

#### Ejemplo de Respuesta:
```json
[
  {
    "id": 1,
    "name": "Juan Perez",
    "email": "juan.perez@example.com"
  },
  {
    "id": 2,
    "name": "Maria Lopez",
    "email": "maria.lopez@example.com"
  }
]
```

---

### 2. **GET `/users/paginated`**

#### Descripción:
Devuelve una lista paginada de usuarios no eliminados.

#### Autenticación:
- **Requerida**: Si

#### Parámetros:
| Parámetro | Tipo   | Ubicación | Obligatorio | Descripción |
|-----------|--------|------------|-------------|-------------|
| `page`    | int    | Query      | No          | Número de página (por defecto 0). |
| `size`    | int    | Query      | No          | Tamaño de página (por defecto 10). |

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `200`  | Lista de usuarios paginada obtenida exitosamente. |
| `400`  | Los valores de página y tamaño deben ser positivos. |
| `500`  | Error interno al obtener los usuarios paginados. |

#### Ejemplo de Request:
```http
GET /users/paginated?page=1&size=5 HTTP/1.1
Host: api.example.com
```

#### Ejemplo de Respuesta:
```json
{
  "page": 1,
  "size": 5,
  "totalElements": 20,
  "content": [
    {
      "id": 6,
      "name": "Carlos Gonzalez",
      "email": "carlos.gonzalez@example.com"
    }
  ]
}
```

---

### 3. **DELETE `/users/{id}`**

#### Descripción:
Elimina un usuario específico de la base de datos. **Solo los usuarios con rol ADMIN están autorizados.**

#### Autenticación:
- **Requerida**: Sí (Token JWT)
- **Rol mínimo**: `ADMIN`

#### Parámetros:
| Parámetro | Tipo   | Ubicación | Obligatorio | Descripción |
|-----------|--------|------------|-------------|-------------|
| `id`      | Long   | Path       | Sí          | ID del usuario a eliminar. |

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `204`  | Usuario eliminado exitosamente. |
| `403`  | No autorizado para realizar esta acción. |
| `404`  | Usuario no encontrado. |

#### Ejemplo de Request:
```http
DELETE /users/123 HTTP/1.1
Host: api.example.com
Authorization: Bearer <tu-token-jwt>
```

#### Ejemplo de Respuestas:
- **204 No Content**
  Usuario eliminado exitosamente.

- **403 Forbidden**
```json
{
  "status": 403,
  "error": "Usted no está autorizado para eliminar usuarios."
}
```

- **404 Not Found**
```json
{
  "status": 404,
  "error": "Usuario no encontrado."
}
```

---

### 4. **GET `/users/{id}/`**

#### Descripción:
Devuelve los detalles del usuario logueado por ID. **Solo el usuario autenticado puede acceder a su propia información.**

#### Autenticación:
- **Requerida**: Sí (Token JWT)

#### Parámetros:
| Parámetro | Tipo   | Ubicación | Obligatorio | Descripción |
|-----------|--------|------------|-------------|-------------|
| `id`      | Long   | Path       | Sí          | ID del usuario a buscar. |

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `200`  | Detalles del usuario obtenidos exitosamente. |
| `403`  | No tienes permisos para ver este usuario. |

#### Ejemplo de Request:
```http
GET /users/123/ HTTP/1.1
Host: api.example.com
Authorization: Bearer <tu-token-jwt>
```

#### Ejemplo de Respuesta:
```json
{
  "id": 123,
  "name": "Luis Martinez",
  "email": "luis.martinez@example.com"
}
```

---

### 5. **PATCH `/users/`**

#### Descripción:
Permite al usuario autenticado actualizar su información.

#### Autenticación:
- **Requerida**: Sí (Token JWT)

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `200`  | Usuario actualizado exitosamente. |
| `404`  | Usuario no encontrado. |

#### Ejemplo de Request:
```http
PATCH /users/ HTTP/1.1
Host: api.example.com
Authorization: Bearer <tu-token-jwt>
Content-Type: application/json

{
  "name": "Luis Martinez",
  "email": "luis.martinez@example.com"
}
```

#### Ejemplo de Respuesta:
```json
"Usuario actualizado exitosamente."
```

---

### 6. **POST `/users/beneficiarios/{beneficiarioCBU}/add`**

#### Descripción:
Agrega un beneficiario al usuario autenticado.

#### Autenticación:
- **Requerida**: Sí (Token JWT)

#### Parámetros:
| Parámetro       | Tipo   | Ubicación | Obligatorio | Descripción |
|------------------|--------|------------|-------------|-------------|
| `beneficiarioCBU` | String | Path       | Sí          | CBU del beneficiario. |

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `200`  | Beneficiario agregado exitosamente. |
| `404`  | Usuario o beneficiario no encontrado. |

#### Ejemplo de Request:
```http
POST /users/beneficiarios/0987654321/add HTTP/1.1
Host: api.example.com
Authorization: Bearer <tu-token-jwt>
```

#### Ejemplo de Respuesta:
```json
"Beneficiario agregado exitosamente."
```

---

### 7. **GET `/users/beneficiarios`**

#### Descripción:
Devuelve una lista de beneficiarios asociados al usuario autenticado.

#### Autenticación:
- **Requerida**: Sí (Token JWT)

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `200`  | Lista de beneficiarios obtenida exitosamente. |
| `404`  | Usuario no encontrado. |

#### Ejemplo de Request:
```http
GET /users/beneficiarios HTTP/1.1
Host: api.example.com
Authorization: Bearer <tu-token-jwt>
```

#### Ejemplo de Respuesta:
```json
[
  {
    "id": 1,
    "name": "Juan Beneficiario",
    "email": "juan.beneficiario@example.com",
    "cbu": "1234567890123456789015"
  },
  {
    "id": 2,
    "name": "Maria Beneficiaria",
    "email": "maria.beneficiaria@example.com",
    "cbu": "0987654321098765432105"
  }
]
```

----------------------------------------------------------------------

## Endpoints de FixedTermDepositController

---

### 1. **GET `/fixed-term-deposits`**

#### Descripción:
Devuelve una lista de los plazos fijos asociados al usuario autenticado.

#### Autenticación:
- **Requerida**: Sí (Token JWT)

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `200`  | Lista de plazos fijos obtenida exitosamente. |

#### Ejemplo de Request:
```http
GET /fixed-term-deposits HTTP/1.1
Host: api.example.com
Authorization: Bearer <tu-token-jwt>
```

#### Ejemplo de Respuesta:
```json
[
  {
    "id": 1,
    "amount": 10000.0,
    "days": 30,
    "interest": 500.0,
    "total": 10500.0
  },
  {
    "id": 2,
    "amount": 20000.0,
    "days": 60,
    "interest": 1500.0,
    "total": 21500.0
  }
]
```

---

### 2. **POST `/fixed-term-deposits/fixedTerm`**

#### Descripción:
Crea un nuevo plazo fijo asociado al usuario autenticado.

#### Autenticación:
- **Requerida**: Sí (Token JWT)

#### Parámetros:
| Parámetro | Tipo   | Ubicación | Obligatorio | Descripción |
|-----------|--------|------------|-------------|-------------|
| `amount`  | Double | Query       | Sí          | Monto del plazo fijo. |
| `days`    | Integer| Query       | Sí          | Días del plazo fijo. |

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `201`  | Plazo fijo creado exitosamente. |
| `400`  | Parámetros inválidos. |

#### Ejemplo de Request:
```http
POST /fixed-term-deposits/fixedTerm?amount=10000&days=30 HTTP/1.1
Host: api.example.com
Authorization: Bearer <tu-token-jwt>
```

#### Ejemplo de Respuesta:
```json
{
  "id": 3,
  "amount": 10000.0,
  "days": 30,
  "interest": 500.0,
  "total": 10500.0
}
```

---

### 3. **POST `/fixed-term-deposits/fixedTerm/simulate`**

#### Descripción:
Simula un plazo fijo para el usuario autenticado, sin realizar la creación.

#### Autenticación:
- **Requerida**: Sí (Token JWT)

#### Parámetros:
| Parámetro | Tipo   | Ubicación | Obligatorio | Descripción |
|-----------|--------|------------|-------------|-------------|
| `amount`  | Double | Body       | Sí          | Monto del plazo fijo. |
| `days`    | Integer| Body       | Sí          | Días del plazo fijo. |

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `201`  | Simulación realizada exitosamente. |
| `400`  | Parámetros inválidos. |

#### Ejemplo de Request:
```http
POST /fixed-term-deposits/fixedTerm/simulate HTTP/1.1
Host: api.example.com
Authorization: Bearer <tu-token-jwt>
Content-Type: application/json

{
  "amount": 15000.0,
  "days": 45
}
```

#### Ejemplo de Respuesta:
```json
{
  "id": null,
  "amount": 15000.0,
  "days": 45,
  "interest": 750.0,
  "total": 15750.0
}
```
----------------------------------------------------------------------

## Endpoints de AuthController

---

### 1. **POST `/auth/register`**

#### Descripción:
Registra un nuevo usuario en el sistema. Se espera que el cuerpo de la solicitud contenga los datos necesarios para la creación de un nuevo usuario.

#### Autenticación:
- **Requerida**: No

#### Cuerpo de la solicitud:
```json
{
  "email": "usuario@example.com",
  "password": "password123",
  "name": "Juan Pérez"
}
```

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `201`  | Usuario registrado exitosamente. |
| `400`  | Datos de registro ivalidos. |
| `500`  | Error interno al registrar el usuario. |

#### Ejemplo de Request:

```http
POST /auth/register HTTP/1.1
Host: api.example.com
Content-Type: application/json

{
  "email": "usuario@example.com",
  "password": "password123",
  "name": "Juan Pérez"
}
```

#### Ejemplo de Respuesta:
```json
{
  "status": "success",
  "message": "Usuario registrado exitosamente."
}
```
---

### 2. **POST `/auth/login`**

#### Descripción:
Realiza el inicio de sesión de un usuario existente. El usuario debe proporcionar su email y contraseña para obtener un token de autenticación.

#### Autenticación:
- **Requerida**: No

#### Cuerpo de la solicitud:
```json
{
  "email": "usuario@example.com",
  "password": "password123"
}
```

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `200`  | Inicio de sesión exitoso, devuelve el token de autenticación. |
| `400`  | Credenciales inválidas. |
| `500`  | Error interno al intentar iniciar sesión. |

#### Ejemplo de Request:

```http
POST /auth/login HTTP/1.1
Host: api.example.com
Content-Type: application/json

{
  "email": "usuario@example.com",
  "password": "password123"
}
```

#### Ejemplo de Respuesta:
```json
{
  "status": "success",
  "token": "jwt-token-aqui"
}
```

#### Nota Adicional:
- /auth/register: El registro de nuevos usuarios no requiere autenticación previa.
- /auth/login: Se utiliza para autenticar a los usuarios registrados y obtener un token JWT para acceder a otras partes de la API que requieren autenticación.

---

## Endpoints de AdminController

---

## Endpoints de AdminController

---

### **GET `/admin`**

#### Descripción:
Este controlador `AdminController` se utiliza para gestionar las funcionalidades de administración relacionadas con las transacciones. Actualmente, el controlador no tiene ningún endpoint expuesto, pero está preparado para incluir futuras operaciones relacionadas con las transacciones mediante el servicio `TransactionService`.

#### Autenticación:
- **Requerida**: Sí (Token JWT)
- **Rol mínimo**: `ADMIN`

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `200`  | El controlador está disponible y listo para ser utilizado. |
| `500`  | Error interno al acceder al controlador. |

#### Ejemplo de Request:
```http
GET /admin HTTP/1.1
Host: api.example.com
Authorization: Bearer <tu-token-jwt>
```

#### Ejemplo de Respuesta:
```json
{
  "status": "success",
  "message": "Controlador de administración disponible."
}
```

#### Nota Adicional:
- El controlador AdminController está preparado para incluir funcionalidades de administración, como la gestión de transacciones, una vez que los métodos sean implementados.
- El servicio TransactionService está inyectado en el controlador para ser utilizado en los métodos futuros que gestionarán las transacciones.
- Este controlador aún no tiene métodos activos, pero puede expandirse para incluir rutas relacionadas con las transacciones en el futuro.
---

