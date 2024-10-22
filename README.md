# Franquicia API

Esta es una API para gestionar franquicias, sucursales y productos. Proporciona endpoints para crear, modificar y consultar franquicias y sus componentes (sucursales y productos).

## Extras Implementados:
- **Docker**: Se incluyen archivos `Dockerfile` y `docker-compose.yml` para facilitar la creación y despliegue de contenedores.
- **Despliegue en la nube**: La API está desplegada en **AWS EC2**, con la base de datos gestionada en **RDS**.

## Tecnologías Utilizadas:
- Spring Boot
- MySQL en AWS RDS
- Docker
- AWS EC2

## Requisitos
- Docker
- Java 17
- Maven

## Requisitos

Asegúrate de tener instalados los siguientes componentes antes de intentar ejecutar la aplicación:

- **Java 17 (OpenJDK)**: Debes tener Java 17 o superior instalado.
- **Maven**: Para la gestión de dependencias y empaquetado del proyecto.
- **Docker y Docker Compose**: Para ejecutar la aplicación y la base de datos en contenedores.
- **Git**: Para clonar el repositorio.
- **Postman**: Para probar los endpoints de la API.
  
## Instrucciones para ejecutar en local

### Clonar el repositorio

Clona el repositorio de tu proyecto a tu máquina local utilizando el siguiente comando:

```bash
git clone https://github.com/tu-usuario/franquicia-api.git
cd franquicia-api
