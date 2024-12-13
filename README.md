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

## Endpoints de TransactionController

---

### 1. **POST `/send`**

#### Descripción:
Envia una transacción a otra cuenta. Se espera recibir tanto email como CBU de la cuenta destino

#### Autenticación:
- **Requerida**: Si (Token JWT)

#### Cuerpo de la solicitud:
```json
{
  "destinationCbu": "1234567890123456789012",
  "currency": "USD",
  "amount": 100.50,
  "description": "Pago de servicios",
  "concept": "Factura 001"
}

```

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `200`  | Transacción finalizada exitosamente. |
| `404`  | Cuenta destinataria no encontrada con el CBU especificado. |
| `400`  | El usuario fue eliminado y su cuenta ya no está disponible para operar. |

#### Ejemplo de Request:

```http
POST /send HTTP/1.1
Host: api.example.com
Content-Type: application/json

{
  "destinationCbu": "1234567890123456789012",
  "currency": "USD",
  "amount": 100.50,
  "description": "Pago de servicios",
  "concept": "Factura 001"
}

```

#### Ejemplo de Respuesta:
```json
{
  "status": "success",
  "message": "Transacción finalizada exitosamente."
}
```
---

### 1. **POST `/deposit/{cbu}`**

#### Descripción:
Realiza un deposito a la propia cuenta del usuario loggeado.

#### Autenticación:
- **Requerida**: Si (Token JWT)

#### Cuerpo de la solicitud:
```json
{
  "amount": 500.00,
  "description": "Depósito en cuenta",
  "concept": "Ahorro mensual"
}

```

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `200`  | Transacción finalizada exitosamente. |
| `404`  | Cuenta destinataria no encontrada con el CBU especificado. |
| `400`  | El usuario fue eliminado y su cuenta ya no está disponible para operar. |
| `500`  | Error inesperado al procesar el depósito. |

#### Ejemplo de Request:

```http
POST /deposit/1234567890123456789012 HTTP/1.1
Host: api.example.com
Content-Type: application/json

{
  "amount": 500.00,
  "description": "Depósito en cuenta",
  "concept": "Ahorro mensual"
}

```

#### Ejemplo de Respuesta:
```json
{
  "status": "success",
  "message": "Depósito realizado exitosamente.",
  "data": {
    "transaction": {
      "id": "12345",
      "amount": 500.00,
      "type": "INGRESO",
      "description": "Depósito en cuenta",
      "concept": "Ahorro mensual",
      "date": "2024-12-11T10:00:00Z"
    },
    "account": {
      "cbu": "1234567890123456789012",
      "balance": 1500.00,
      "currency": "USD"
    }
  }
}
```
---

### 1. **GET `/{id}`**

#### Descripción:
Obtener la transacción del usuario loggeado por id

#### Autenticación:
- **Requerida**: Si (Token JWT)

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `200`  | Transacción finalizada exitosamente. |
| `401`  | Acceso no autorizado a la transacción solicitada. |
| `500`  | Error inesperado al buscar la transacción. |

#### Ejemplo de Request:

```http
GET /12345 HTTP/1.1
Host: api.example.com
Authorization: Bearer <token>

```

#### Ejemplo de Respuesta:
```json
{
  "status": "success",
  "message": "Transacción encontrada exitosamente.",
  "data": {
    "id": 12345,
    "cbuDestino": "9876543210987654321098",
    "cbuOrigen": "1234567890123456789012",
    "amount": 200.00,
    "type": "PAGO",
    "description": "Pago de servicios",
    "concept": "Electricidad",
    "timestamp": "2024-12-11T10:00:00",
    "accountOrigen": {
      "cbu": "1234567890123456789012",
      "owner": "Juan Pérez",
      "currency": "USD"
    },
    "accountDestino": {
      "cbu": "9876543210987654321098",
      "owner": "Empresa Eléctrica",
      "currency": "USD"
    }
  }
}
```
---

### 1. **PATCH `/{id}}`**

#### Descripción:
Editar una transacción para modificar solo la descripción.

#### Autenticación:
- **Requerida**: Si (Token JWT)

#### Cuerpo de la solicitud:
```json
{
  "description": "Nueva descripción de la transacción",
  "concept": "Electricidad"
}

```

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `403`  | No tienes permisos para editar esta transacción. |
| `404`  | Transacción no encontrada |
| `400`  | Descripción inválida. |
| `500`  | Error inesperado al actualizar la transacción. |

#### Ejemplo de Request:

```http
PATCH /12345 HTTP/1.1
Host: api.example.com
Content-Type: application/json
Authorization: Bearer <token>

{
  "description": "Nueva descripción de la transacción",
  "concept": "Electricidad"
}

```

#### Ejemplo de Respuesta:
```json
{
  "status": "success",
  "message": "Transacción actualizada exitosamente.",
  "data": {
    "description": "Nueva descripción de la transacción",
    "concept": "Electricidad"
  }
}
```
---

### 1. **GET `/user/{userId}`**

#### Descripción:
Obtener las transacciones de usuarios por id.

#### Autenticación:
- **Requerida**: Si (Token JWT)

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `403`  | Acceso denegado. |
| `404`  | Usuario no encontrado |
| `500`  | Error inesperado al obtener las transacciones. |

#### Ejemplo de Request:

```http
GET /user/12345 HTTP/1.1
Host: api.example.com
Authorization: Bearer <token>

```

#### Ejemplo de Respuesta:
```json
{
  "status": "success",
  "message": "Transacciones obtenidas exitosamente",
  "data": [
    {
      "id": 1,
      "cbuDestino": "1234567890123456789012",
      "cbuOrigen": "0987654321098765432109",
      "amount": 500.00,
      "type": "PAGO",
      "description": "Pago de servicios",
      "concept": "Servicios",
      "timestamp": "2024-12-11T14:30:00",
      "accountOrigen": {
        "id": 1001,
        "alias": "mi-cuenta-origen",
        "cbu": "0987654321098765432109",
        "currency": "ARS"
      },
      "accountDestino": {
        "id": 1002,
        "alias": "cuenta-destino",
        "cbu": "1234567890123456789012",
        "currency": "ARS"
      }
    }
  ]
}

```
---

### 1. **GET `/user`**

#### Descripción:
Obtener las transacciones de usuarios especificos.

#### Autenticación:
- **Requerida**: Si (Token JWT)

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `401`  | No se encuentra autenticado. |
| `404`  | No se encontraron transacciones para el usuario |
| `500`  | Error al obtener las transacciones del usuario. |

#### Ejemplo de Request:

```http
GET /user HTTP/1.1
Host: api.example.com
Authorization: Bearer <token>

```

#### Ejemplo de Respuesta:
```json
{
  "status": "success",
  "message": "Transacciones obtenidas exitosamente",
  "data": [
    {
      "id": 1,
      "cbuDestino": "1234567890123456789012",
      "cbuOrigen": "0987654321098765432109",
      "amount": 500.00,
      "type": "PAGO",
      "description": "Pago de servicios",
      "concept": "Servicios",
      "timestamp": "2024-12-11T14:30:00",
      "accountOrigen": {
        "id": 1001,
        "alias": "mi-cuenta-origen",
        "cbu": "0987654321098765432109",
        "currency": "ARS"
      },
      "accountDestino": {
        "id": 1002,
        "alias": "cuenta-destino",
        "cbu": "1234567890123456789012",
        "currency": "ARS"
      }
    }
  ]
}
```
---

### 1. **POST `/payment`**

#### Descripción:
Realizar un pago por el usuario loggeado.

#### Autenticación:
- **Requerida**: Si (Token JWT)

#### Cuerpo de la solicitud:
```json
{
  "nroTarjeta": "1234567812345678",
  "amount": 1000.50,
  "currency": "ARS",
  "description": "Pago de tarjeta de crédito",
  "concept": "Tarjeta de crédito"
}

```

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `400`  | El monto a depositar debe ser mayor a cero. |
| `401`  | No se encuentra autenticado |
| `500`  | Error al procesar el pago. |

#### Ejemplo de Request:

```http
POST /payment HTTP/1.1
Host: api.example.com
Content-Type: application/json
Authorization: Bearer <token>

```

#### Ejemplo de Respuesta:
```json
{
  "status": "success",
  "message": "Pago realizado con éxito",
  "data": {
    "transaction": {
      "id": 101,
      "cbuDestino": "1234567890123456789012",
      "cbuOrigen": "1234567812345678",
      "amount": 1000.50,
      "type": "Pago",
      "description": "Pago de tarjeta de crédito",
      "concept": "Tarjeta de crédito",
      "timestamp": "2024-12-11T15:45:00",
      "balance": 5000.00
    },
    "account": {
      "id": 1001,
      "alias": "mi-cuenta",
      "cbu": "1234567812345678",
      "currency": "ARS",
      "balance": 4000.00
    }
  }
}

```
---

### 1. **POST `/sendrecipient`**

#### Descripción:
Enviar dinero a un beneficiario.

#### Autenticación:
- **Requerida**: Si (Token JWT)

#### Cuerpo de la solicitud:
```json
{
  "destinationCbu": "1234567890123456789012",
  "amount": 1500.00,
  "currency": "ARS",
  "description": "Pago de servicios",
  "concept": "Servicios"
}

```

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `400`  | No se puede realizar una transferencia a una cuenta propia. |
| `404`  | Cuenta emisora no encontrada para el usuario autenticado con la moneda especificada. |
| `401`  | No se encuentra autenticado. |

#### Ejemplo de Request:

```http
POST /sendRecipient HTTP/1.1
Host: api.example.com
Content-Type: application/json
Authorization: Bearer <token>

```

#### Ejemplo de Respuesta:
```json
{
  "status": "success",
  "message": "Transacción finalizada exitosamente."
}

```
---

### 1. **GET `/user/{userId}/paginated`**

#### Descripción:
Obtener las transacciones paginadas de un usuario.

#### Autenticación:
- **Requerida**: Si (Token JWT)

#### Respuestas:
| Código | Descripción |
|--------|-------------|
| `404`  | Usuario no encontrado. |
| `401`  | No se encuentra autenticado. |

#### Ejemplo de Request:

```http
GET /user/10/paginated?page=0&size=10 HTTP/1.1
Host: api.example.com
Authorization: Bearer <token>

```

#### Ejemplo de Respuesta:
```json
{
  "data": [
    {
      "id": 101,
      "cbuDestino": "2222222222222222222222",
      "cbuOrigen": "1111111111111111111111",
      "amount": 500.00,
      "type": "Pago",
      "description": "Alquiler mensual",
      "concept": "Alquiler",
      "timestamp": "10:00",
      "accountOrigen": {
        "name": "Caja de Ahorro"
      },
      "accountDestino": {
        "name": "Cuenta Propietario"
      }
    }
  ],
  "currentPage": 0,
  "totalPages": 3,
  "previousPageUrl": null,
  "nextPageUrl": "/user/10/paginated?page=1&size=10"
}


```
---

# API Endpoints - Cuentas

## 1. GET /accounts
### Descripción:
Obtiene todas las cuentas del usuario actualmente logueado.

### Autenticación:
Requerida: **Sí** (Token JWT)

### Respuestas:
| Código | Descripción                          |
|--------|--------------------------------------|
| 200    | Éxito: Cuentas obtenidas exitosamente |
| 403    | Acceso denegado                      |
| 404    | Usuario no encontrado                |
| 500    | Error inesperado al obtener cuentas |

### Ejemplo de Request:

```http
GET /accounts HTTP/1.1
Host: api.example.com
Authorization: Bearer <token>

```

Ejemplo de Respuesta:
```json
{
  "status": "success",
  "message": "Cuentas obtenidas exitosamente",
  "data": [
    {
      "id": 1,
      "currency": "ARS",
      "balance": 10000.50,
      "transactionLimit": 5000.00,
      "status": "ACTIVE"
    },
    {
      "id": 2,
      "currency": "USD",
      "balance": 1500.75,
      "transactionLimit": 2000.00,
      "status": "ACTIVE"
    }
  ]
}
```

## 2- GET /accounts/{userId}
Descripción:
Obtiene todas las cuentas de un usuario específico.

Parámetros:
userId: ID del usuario.
Respuestas:
Código	Descripción
200	    Exito: Cuentas obtenidas exitosamente
403	    Acceso denegado
404	    Usuario no encontrado
500	    Error inesperado al obtener cuentas

Ejemplo de Request:
```http
GET /accounts/12345 HTTP/1.1
Host: api.example.com
Authorization: Bearer <token>
```

```Ejemplo de Respuesta:
json
{
  "status": "success",
  "message": "Cuentas obtenidas exitosamente",
  "data": [
    {
      "id": 1,
      "currency": "ARS",
      "balance": 10000.50,
      "transactionLimit": 5000.00,
      "status": "ACTIVE"
    }
  ]
}
```
## 3- POST /accounts/{currency}

Descripción:
Crea una cuenta para el usuario logueado con una moneda especificada.

Parámetros:
currency: Tipo de moneda para la nueva cuenta (ej. ARS, USD).
Autenticación: Requerida: Sí (Token JWT)


Respuestas:
Código	Descripción
201	    Éxito: Cuenta creada
400	    Petición inválida
500	Error inesperado al crear la cuenta




Ejemplo de Request:
```http
POST /accounts/ARS HTTP/1.1
Host: api.example.com
Authorization: Bearer <token>
Content-Type: application/json


{
  "currency": "ARS"
}
Ejemplo de Respuesta:
{
  "status": "success",
  "message": "Cuenta creada exitosamente",
  "data": {
    "id": 3,
    "currency": "ARS",
    "balance": 0.00,
    "transactionLimit": 5000.00,
    "status": "ACTIVE"
  }
}
```

## 4- GET /accounts/balance
Descripción:
Obtener balance de cuentas del usuario loggeado.

Autenticación:
Requerida: Sí (Token JWT)

Respuestas:
Código	Descripción
200	Éxito: Balance obtenido exitosamente
403	Acceso denegado
404	Usuario no encontrado
500	Error inesperado al obtener balance

```http
GET /accounts/balance HTTP/1.1
Host: api.example.com
Authorization: Bearer <token>

{
  "status": "success",
  "message": "Balance obtenido exitosamente",
  "data": {
    "balance": 5000.00
  }
}
```

## 5. PATCH /accounts/{id}
Descripción:
Editar el límite de transacción de la cuenta del usuario loggeado.

Autenticación:
Requerida: Sí (Token JWT)

Respuestas:
Código	Descripción
200	Éxito: Límite de transacción actualizado exitosamente
403	Acceso denegado
404	Cuenta no encontrada
500	Error inesperado al actualizar el límite de transacción

```http
PATCH /accounts/1 HTTP/1.1
Host: api.example.com
Authorization: Bearer <token>
Content-Type: application/json

{
  "newTransactionLimit": 10000.00
}
{
  "status": "success",
  "message": "Límite de transacción actualizado exitosamente",
  "data": {
    "id": 1,
    "cbu": "1234567890123456789012",
    "currency": "ARS",
    "balance": 1500.00,
    "transactionLimit": 10000.00
  }
}
```

## 6-GET /accounts/transactions
Descripción:
Obtener transacciones por cada cuenta del usuario.

Autenticación:
Requerida: Sí (Token JWT)

Respuestas:
Código	Descripción
200	Éxito: Transacciones obtenidas exitosamente
403	Acceso denegado
404	Cuenta no encontrada
500	Error inesperado al obtener transacciones

```http
ET /accounts/transactions HTTP/1.1
Host: api.example.com
Authorization: Bearer <token>


{
  "status": "success",
  "message": "Transacciones obtenidas exitosamente",
  "data": [
    {
      "transactionId": 1,
      "amount": 1000.00,
      "currency": "ARS",
      "date": "2024-12-12T15:30:00",
      "description": "Pago de servicios"
    },
    {
      "transactionId": 2,
      "amount": 500.00,
      "currency": "ARS",
      "date": "2024-12-10T10:00:00",
      "description": "Compra en tienda"
    }
  ]
}

```










