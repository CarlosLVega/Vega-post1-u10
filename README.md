# Productos Service - Analisis SonarQube

Proyecto Spring Boot para el laboratorio de la Unidad 10: Metricas de Calidad y SonarQube. El objetivo es ejecutar un analisis inicial sobre codigo intencionalmente imperfecto, integrar JaCoCo para cobertura y documentar los hallazgos encontrados en SonarQube.

## Tecnologias

- Java 21
- Spring Boot 3.5.0
- Maven 3.9+
- Spring Web
- Spring Data JPA
- H2 Database
- Lombok
- JaCoCo
- SonarQube Community Edition

## Ejecucion local

Compilar el proyecto:

```bash
mvn compile
```

Ejecutar pruebas y generar cobertura JaCoCo:

```bash
mvn clean verify
```

Si Windows o OneDrive bloquea archivos dentro de `target`, cerrar el IDE o pausar la sincronizacion y volver a ejecutar el comando. En este equipo se verifico correctamente con:

```bash
mvn verify
```

El reporte XML de cobertura se genera en:

```text
target/site/jacoco/jacoco.xml
```

Ejecutar la aplicacion:

```bash
mvn spring-boot:run
```

## SonarQube local

Levantar SonarQube con Docker:

```bash
docker run -d ^
  --name sonarqube ^
  -p 9000:9000 ^
  -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true ^
  sonarqube:community
```

Verificar el contenedor:

```bash
docker ps
docker logs -f sonarqube
```

Acceder a:

```text
http://localhost:9000
```

Credenciales iniciales:

```text
admin / admin
```

Crear un proyecto manual con estos datos:

```text
Project name: Productos Service
Project key: com.universidad:productos-service
```

Luego generar un token y ejecutar el analisis:

```bash
mvn clean verify sonar:sonar -Dsonar.token=TU_TOKEN
```

Dashboard esperado:

```text
http://localhost:9000/dashboard?id=com.universidad%3Aproductos-service
```

## Estado inicial del analisis

> Importante: los valores de Bugs, Vulnerabilidades, Code Smells y Ratings deben copiarse del dashboard real de SonarQube despues de ejecutar el analisis. La cobertura JaCoCo verificada localmente fue aproximadamente 17.1%.

| Categoria | Cantidad | Rating |
|-----------|----------|--------|
| Bugs | Pendiente de dashboard | Pendiente |
| Vulnerabilidades | Pendiente de dashboard | Pendiente |
| Code Smells | Pendiente de dashboard | Pendiente |
| Cobertura | 17.1% | - |

## Hallazgos principales identificados

### Bug 1: Producto inexistente retorna null

- Archivo: `src/main/java/com/universidad/productosservice/service/ProductoService.java`, linea 47
- Descripcion: el metodo `buscar` usa `orElse(null)`, por lo que el servicio puede devolver `null` cuando el producto no existe. Esto obliga a los consumidores a manejar nulos y puede provocar errores en tiempo de ejecucion.
- Severidad: Major

### Code Smell 1: Inyeccion de dependencia por campo

- Archivo: `src/main/java/com/universidad/productosservice/service/ProductoService.java`, linea 14
- Descripcion: `ProductoRepository` se inyecta con `@Autowired` sobre un campo mutable. La practica recomendada es usar inyeccion por constructor y declarar la dependencia como `final`.
- Severidad: Major

### Code Smell 2: Nombre de dependencia poco descriptivo

- Archivo: `src/main/java/com/universidad/productosservice/service/ProductoService.java`, linea 15
- Descripcion: el campo `repo` usa un nombre generico que reduce la legibilidad del servicio. Un nombre como `productoRepository` comunica mejor su responsabilidad.
- Severidad: Minor

### Code Smell 3: Validacion de texto incompleta

- Archivo: `src/main/java/com/universidad/productosservice/service/ProductoService.java`, linea 21
- Descripcion: la validacion `n == null || n.equals("")` no rechaza cadenas con solo espacios. `isBlank()` expresaria mejor la regla de negocio.
- Severidad: Minor

### Code Smell 4: Logica de negocio dentro de una entidad JPA

- Archivo: `src/main/java/com/universidad/productosservice/domain/Producto.java`, linea 53
- Descripcion: `getEstado` contiene reglas de negocio y multiples ramas dentro de la entidad. Esto aumenta la complejidad de la clase de persistencia.
- Severidad: Major

### Code Smell 5: Rama final inalcanzable

- Archivo: `src/main/java/com/universidad/productosservice/domain/Producto.java`, linea 61
- Descripcion: despues de evaluar todos los casos de `stock`, el ultimo `return "DESCONOCIDO"` queda como una rama defensiva que no se alcanza para valores enteros validos.
- Severidad: Minor

## Capturas del dashboard

Agregar las capturas reales generadas despues de ejecutar SonarQube:

![Dashboard SonarQube](docs/sonar-dashboard.png)
![Detalle Bugs](docs/sonar-bugs.png)
![Detalle Code Smells](docs/sonar-code-smells.png)

Las imagenes deben guardarse con esos nombres dentro de la carpeta `docs/`.

## Checkpoints de verificacion

- El proyecto compila con `mvn compile`.
- Las clases `Producto`, `ProductoRepository` y `ProductoService` estan creadas en los paquetes requeridos.
- JaCoCo esta configurado en `pom.xml` con las fases `prepare-agent` y `report`.
- `sonar-project.properties` esta en la raiz del proyecto y apunta a `http://localhost:9000`.
- `mvn verify` genera `target/site/jacoco/jacoco.xml`.
- Al ejecutar SonarQube localmente, el dashboard debe mostrar al menos 1 Bug, al menos 3 Code Smells y la cobertura del proyecto.
- El repositorio debe tener minimo 3 commits descriptivos: setup inicial, codigo con problemas, analisis documentado.
