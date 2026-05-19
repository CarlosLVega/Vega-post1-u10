# Productos Service - Analisis SonarQube

Proyecto Spring Boot desarrollado para el laboratorio de la Unidad 10: Metricas de Calidad y SonarQube. El repositorio contiene una aplicacion con problemas de calidad intencionales, configuracion de JaCoCo para cobertura y configuracion de SonarQube para ejecutar el analisis estatico local.

## Tecnologias utilizadas

- Java 21
- Spring Boot 3.5.0
- Maven 3.9+
- Spring Web
- Spring Data JPA
- H2 Database
- Lombok
- JaCoCo
- SonarQube Community Edition

## Ejecucion del proyecto

Compilar el proyecto:

```bash
mvn compile
```

Ejecutar pruebas y generar el reporte de cobertura:

```bash
mvn clean verify
```

Ejecutar la aplicacion:

```bash
mvn spring-boot:run
```

La aplicacion usa una base de datos H2 en memoria y deja disponible la consola H2 en:

```text
http://localhost:8080/h2-console
```

## Configuracion de SonarQube

Levantar SonarQube Community Edition con Docker:

```bash
docker run -d ^
  --name sonarqube ^
  -p 9000:9000 ^
  -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true ^
  sonarqube:community
```

Verificar el estado del contenedor:

```bash
docker ps
docker logs -f sonarqube
```

Acceder al dashboard local:

```text
http://localhost:9000
```

Credenciales iniciales:

```text
admin / admin
```

Datos del proyecto creado en SonarQube:

```text
Project name: Productos Service
Project key: com.universidad:productos-service
```

Ejecutar el analisis con el token generado en SonarQube:

```bash
mvn clean verify sonar:sonar -Dsonar.token=TU_TOKEN
```

Dashboard del proyecto:

```text
http://localhost:9000/dashboard?id=com.universidad%3Aproductos-service
```

## Estado inicial del analisis

| Categoria | Cantidad | Rating |
|-----------|----------|--------|
| Bugs | 1 | C |
| Vulnerabilidades | 0 | A |
| Code Smells | 5 | B |
| Cobertura | 17.1% | - |

## Hallazgos principales identificados

### Bug 1: Producto inexistente retorna null

- Archivo: `src/main/java/com/universidad/productosservice/service/ProductoService.java`, linea 47
- Descripcion: el metodo `buscar` usa `orElse(null)`, por lo que el servicio puede devolver `null` cuando el producto no existe. Esto obliga a los consumidores del metodo a validar manualmente el resultado y puede provocar errores en tiempo de ejecucion.
- Severidad: Major

### Code Smell 1: Inyeccion de dependencia por campo

- Archivo: `src/main/java/com/universidad/productosservice/service/ProductoService.java`, linea 14
- Descripcion: `ProductoRepository` se inyecta con `@Autowired` sobre un campo mutable. La practica recomendada es utilizar inyeccion por constructor para mejorar la claridad, facilitar pruebas y evitar dependencias mutables.
- Severidad: Major

### Code Smell 2: Nombre de dependencia poco descriptivo

- Archivo: `src/main/java/com/universidad/productosservice/service/ProductoService.java`, linea 15
- Descripcion: el campo `repo` usa un nombre generico que reduce la legibilidad del servicio. Un nombre como `productoRepository` comunica mejor su responsabilidad.
- Severidad: Minor

### Code Smell 3: Validacion de texto incompleta

- Archivo: `src/main/java/com/universidad/productosservice/service/ProductoService.java`, linea 21
- Descripcion: la validacion `n == null || n.equals("")` no rechaza cadenas formadas solo por espacios. Una validacion con `isBlank()` expresaria mejor la regla de negocio.
- Severidad: Minor

### Code Smell 4: Logica de negocio dentro de una entidad JPA

- Archivo: `src/main/java/com/universidad/productosservice/domain/Producto.java`, linea 53
- Descripcion: el metodo `getEstado` contiene reglas de negocio y multiples ramas dentro de la entidad. Esto aumenta la complejidad de la clase de persistencia y mezcla responsabilidades.
- Severidad: Major

### Code Smell 5: Rama final inalcanzable

- Archivo: `src/main/java/com/universidad/productosservice/domain/Producto.java`, linea 61
- Descripcion: despues de evaluar los casos posibles de `stock`, el ultimo `return "DESCONOCIDO"` queda como una rama defensiva que no se alcanza para valores enteros validos.
- Severidad: Minor

## Capturas del dashboard

![Dashboard SonarQube](docs/sonar-dashboard.png)

![Detalle Bugs](docs/sonar-bugs.png)

![Detalle Code Smells](docs/sonar-code-smells.png)

## Evidencia de configuracion

El archivo `sonar-project.properties` define el proyecto, las rutas de codigo fuente, las pruebas, los binarios compilados y el reporte XML generado por JaCoCo:

```properties
sonar.projectKey=com.universidad:productos-service
sonar.projectName=Productos Service
sonar.projectVersion=1.0
sonar.sources=src/main/java
sonar.tests=src/test/java
sonar.java.binaries=target/classes
sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
sonar.exclusions=**/*Application.java
sonar.host.url=http://localhost:9000
sonar.qualitygate.wait=false
```

El plugin JaCoCo esta configurado en `pom.xml` con las fases `prepare-agent` y `report`, permitiendo generar el archivo:

```text
target/site/jacoco/jacoco.xml
```

## Commits realizados

1. `setup inicial del proyecto Spring Boot`
2. `agrega codigo imperfecto y configuracion SonarQube JaCoCo`
3. `documenta analisis inicial SonarQube`

## Conclusiones

El analisis inicial evidencia problemas de mantenibilidad y posibles errores de comportamiento en el servicio. El objetivo de esta primera revision no es corregir los hallazgos, sino identificarlos, clasificarlos y dejar documentado el estado base del proyecto antes de aplicar mejoras en una siguiente iteracion.
