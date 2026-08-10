#!/usr/bin/env python3
import argparse
import base64
import os
import random
import string
from datetime import date
from pathlib import Path

try:
    import argon2
except ImportError:
    raise SystemExit(
        "Missing dependency: install with `python -m pip install argon2-cffi mysql-connector-python`"
    )

try:
    import mysql.connector
except ImportError:
    raise SystemExit(
        "Missing dependency: install with `python -m pip install mysql-connector-python argon2-cffi`"
    )

PROPERTIES_PATH = Path(__file__).resolve().parent / "application.properties"

DEFAULT_DOC = "0000000000"
DEFAULT_EMAIL = "admin@sistema.local"
DEFAULT_PASSWORD_LENGTH = 16
DEFAULT_TIPO_DOC = "CC"
DEFAULT_TIPO_USUARIO = "ADMINISTRADOR"
DEFAULT_ESTADO = "Activo"


def parse_properties(path: Path) -> dict:
    if not path.exists():
        raise FileNotFoundError(f"Properties file not found: {path}")

    props = {}
    with path.open("r", encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith("#"):
                continue
            if "=" not in line:
                continue
            key, value = line.split("=", 1)
            props[key.strip()] = value.strip()
    return props


def generate_password(length: int = DEFAULT_PASSWORD_LENGTH) -> str:
    alphabet = string.ascii_letters + string.digits + "!@#$%&*()-_=+"
    return "".join(random.SystemRandom().choice(alphabet) for _ in range(length))


def encode_password(password: str) -> str:
    hasher = argon2.PasswordHasher(
        time_cost=3,
        memory_cost=4096,
        parallelism=1,
        hash_len=32,
        salt_len=16,
        type=argon2.low_level.Type.ID,
    )
    return hasher.hash(password)


def build_connection_config(props: dict, args: argparse.Namespace):
    host = args.db_host or props.get("spring.datasource.url")
    user = args.db_user or props.get("spring.datasource.username")
    password = args.db_password or props.get("spring.datasource.password")
    database = args.db_name

    if host and host.startswith("jdbc:mysql://"):
        host = host.replace("jdbc:mysql://", "")
        if "?" in host:
            host = host.split("?", 1)[0]

    if ":" in host and "/" in host:
        host_part, database = host.split("/", 1)
        if ":" in host_part:
            host, port = host_part.split(":", 1)
        else:
            host = host_part

    if not database:
        database = props.get("spring.datasource.url", "").split("/")[-1].split("?")[0]

    if not host or not user or database is None:
        raise ValueError("Database connection values are missing. Check application.properties or pass CLI args.")

    return {
        "host": host,
        "user": user,
        "password": password or "",
        "database": database,
        "charset": "utf8mb4",
        "use_unicode": True,
    }


def create_admin_user(conn_config: dict, email: str, password: str, numero_documento: str,
                      tipo_documento: str, nombre: str, apellido: str, telefono: str,
                      estado: str, fecha_nacimiento: str, genero: str, direccion: str):
    encoded_password = encode_password(password)

    connection = mysql.connector.connect(**conn_config)
    try:
        cursor = connection.cursor()
        cursor.execute("SELECT COUNT(*) FROM usuario WHERE correo = %s", (email,))
        if cursor.fetchone()[0] > 0:
            raise ValueError(f"Ya existe un usuario con correo {email}")

        insert_usuario = (
            "INSERT INTO usuario (numero_documento, tipo_documento, nombre, apellido, correo, "
            "contrasena, telefono, estado, fecha_nacimiento, genero, direccion, tipo_usuario) "
            "VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"
        )
        cursor.execute(insert_usuario, (
            numero_documento,
            tipo_documento,
            nombre,
            apellido,
            email,
            encoded_password,
            telefono,
            estado,
            fecha_nacimiento,
            genero,
            direccion,
            DEFAULT_TIPO_USUARIO,
        ))
        admin_id = cursor.lastrowid
        cursor.execute("INSERT INTO administrador (id_usuario) VALUES (%s)", (admin_id,))
        connection.commit()
        return admin_id, encoded_password
    finally:
        connection.close()


def main():
    props = parse_properties(PROPERTIES_PATH)

    parser = argparse.ArgumentParser(description="Crear un usuario administrador en la base de datos.")
    parser.add_argument("--email", default=DEFAULT_EMAIL, help="Correo electrónico del administrador")
    parser.add_argument("--password", help="Contraseña del administrador. Si no se suministra, se genera al azar.")
    parser.add_argument("--numero-documento", default=DEFAULT_DOC, help="Número de documento del administrador")
    parser.add_argument("--tipo-documento", default=DEFAULT_TIPO_DOC, help="Tipo de documento (CC, TI, CE, RC, PA)")
    parser.add_argument("--nombre", default="Administrador", help="Nombre del administrador")
    parser.add_argument("--apellido", default="Sistema", help="Apellido del administrador")
    parser.add_argument("--telefono", default="", help="Teléfono del administrador")
    parser.add_argument("--estado", default=DEFAULT_ESTADO, help="Estado del usuario")
    parser.add_argument("--fecha-nacimiento", default=date(1990, 1, 1).isoformat(), help="Fecha de nacimiento AAAA-MM-DD")
    parser.add_argument("--genero", default="", help="Género del administrador")
    parser.add_argument("--direccion", default="", help="Dirección del administrador")
    parser.add_argument("--db-host", help="Host MySQL o JDBC URL")
    parser.add_argument("--db-user", help="Usuario de la base de datos")
    parser.add_argument("--db-password", help="Contraseña de la base de datos")
    parser.add_argument("--db-name", help="Nombre de la base de datos")
    args = parser.parse_args()

    password = args.password or generate_password()
    conn_config = build_connection_config(props, args)

    admin_id, encoded_password = create_admin_user(
        conn_config,
        args.email,
        password,
        args.numero_documento,
        args.tipo_documento,
        args.nombre,
        args.apellido,
        args.telefono,
        args.estado,
        args.fecha_nacimiento,
        args.genero,
        args.direccion,
    )

    print("Administrador creado con éxito")
    print(f"ID de usuario: {admin_id}")
    print(f"Correo: {args.email}")
    print(f"Contraseña: {password}")
    print("Recuerda guardar esta contraseña. El valor almacenado en la base de datos está hasheado con Argon2.")


if __name__ == "__main__":
    main()
