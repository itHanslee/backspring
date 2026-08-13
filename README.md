💉 Sistema de Vacunación - Backend (backspring)
API REST desarrollada con Spring Boot y MySQL para la gestión integral del sistema de vacunación. Proporciona control de usuarios por roles, seguridad stateless con JWT y persistencia modular orientada a entidades clave (Ciudadanos, Personal de Salud y Administradores).

🛠️ Tecnologías Utilizadas
Java 21 & Spring Boot 4.0

Spring Security & JWT (JSON Web Tokens)

Argon2 (Encriptación avanzada de contraseñas)

Spring Data JPA / Hibernate

MySQL (Gestor de Base de Datos)

Maven (Gestor de dependencias)

🚀 Requisitos Previos
Antes de ejecutar el proyecto, asegúrate de contar con:

JDK 21 instalado y configurado en las variables de entorno.

MySQL Server corriendo localmente en el puerto 3306.

Base de datos creada en MySQL:

SQL
CREATE DATABASE sistema_vacunacion;
⚙️ Configuración del Proyecto
1. Clonar el Repositorio
Bash
git clone https.github.com/itHanslee/backspring.git
cd backspring/Backend/Delta
2. Configurar la Base de Datos
Revisa o actualiza el archivo src/main/resources/application.properties con tus credenciales locales de MySQL:


# Mapeo JPA / DDL
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
⚠️ Nota sobre la Base de Datos: Las claves primarias y foráneas (id_usuario) están tipadas como BIGINT para alinearse con el tipo Long del backend. Si encuentras errores de validación DDL, asegúrate de que tus tablas utilicen BIGINT en lugar de INT.

🏃‍♂️ Ejecución de la Aplicación
Puedes iniciar el servidor utilizando el wrapper de Maven desde la terminal:

Bash
# En Windows
mvnw.cmd spring-boot:run

# En Linux / macOS
./mvnw spring-boot:run
La API estará disponible en: http://localhost:8080

🔑 Módulos y Roles del Sistema
El backend utiliza una estrategia de herencia @Inheritance(strategy = InheritanceType.JOINED) a partir de la entidad base Usuario:

ADMINISTRADOR: Alta, baja y control del personal de salud.

PERSONAL_SALUD: Registro de dosis aplicadas, consulta de pacientes por documento y gestión del puesto de vacunación.

CIUDADANO: Consulta de historial de vacunas y generación de carnés digitales.

🤝 Flujo de Trabajo para Desarrolladores (Git)
Para mantener el historial limpio y evitar conflictos al integrar cambios:

Sincronizar cambios antes de trabajar:

Bash
git pull origin main --rebase
Subir cambios resueltos:

Bash
git add .
git commit -m "Descripción clara de los cambios"
git push origin main
