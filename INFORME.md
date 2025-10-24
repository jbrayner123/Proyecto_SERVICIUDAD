# INFORME.md — Proyecto serviciudad

- ## Arquitectura general

    El proyecto sigue una arquitectura hexagonal (Ports & Adapters) dentro de un monolito Spring Boot.
    La idea central: la lógica de negocio (dominio + casos de uso) no depende de detalles técnicos (BD, archivos, HTTP). Esos detalles se conectan mediante puertos (interfaces) y adaptadores.

- ## Paquetes / archivos importantes (mapping)

    Ruta base del package: ```com.serviciudad.serviciudad```

    - ```application.usecase```

         ```ObtenerDeudaUseCase``` (interfaz — port in)
        ```ConsolidarDeudaService``` (implementación del use case)

    - ```domain.model```

        ```FacturaEnergia, FacturaAcueducto, DeudaConsolidada``` (modelos de dominio)

    - ```domain.port.out```

        ```FacturasEnergiaPort``` (interfaz para obtener facturas de energía)
        ```FacturasAcueductoPort``` (interfaz para consultar facturas de acueducto)

    - ```infrastructure.adapter.in.rest```

        ```DeudaController``` (controlador REST)
        ```dto``` (DTOs de respuesta: DeudaConsolidadaDTO, DetalleServicioDTO)

    - ```infrastructure.adapter.out.file```

        ```AdaptadorArchivoEnergia``` (adapter que lee el archivo legacy consumos_energia.txt)

    - ```infrastructure.adapter.out.persistence```

        ```FacturaAcueductoEntity```, ```FacturaAcueductoRepositoryJpa```, ```FacturasAcueductoAdapter``` (JPA + adapter)

    - ```shared.exceptions```

        ```NotFoundException``` (errores HTTP mapeados)

- ## Patrones aplicados y principios

    Hexagonal / Ports & Adapters: separación entre dominio y detalles técnicos.

    Adapter: ```AdaptadorArchivoEnergia```, ```FacturasAcueductoAdapter```.

    Repository: Spring Data JPA (```FacturaAcueductoRepositoryJpa```).

    DTO y Builder: para respuestas REST (```DeudaConsolidadaDTO.Builder```).

    Dependency Injection (Spring): inyección por constructor para facilitar tests.

    Principios SOLID

- ## Simulación del sistema legacy

    El sistema legacy de energía está simulado por un archivo de ancho fijo ```consumos_energia.txt```

    ```AdaptadorArchivoEnergia``` es responsable de parsearlo (posición fija: id_cliente(0..9), periodo(10..15), consumo(16..23), valor(24..end)).

    Esto permite simular lecturas de mainframe sin cambiar la lógica del dominio.

- ## Base de datos

    La app usa MySQL. En este caso la conexión está configurada para AWS RDS en ```application.properties.```

    Tabla principal usada por JPA:

    ```facturas_acueducto (id, id_cliente, periodo, consumo_m3, valor_pagar)```

# PATRONES

  - # Patrón Builder:
  
  Ubicación:```src/main/java/com/serviciudad/serviciudad/infrastructure/adapter/in/rest/dto/DeudaConsolidadaDTO.java```
  
  ¿Está implementado correctamente? Sí está implementado mediante una clase interna estática Builder con métodos fluidos y constructor privado.
  
  ¿Por qué lo usamos? Necesitamos construir un objeto DeudaConsolidadaDTO complejo que contiene 6 atributos:
  
  clienteId: Identificación del cliente
  nombreCliente: Nombre completo del cliente
  fechaConsulta: Timestamp de la consulta
  energia: Detalle del servicio de energía (DetalleServicioDTO)
  acueducto: Detalle del servicio de acueducto (DetalleServicioDTO)
  totalAPagar: Suma consolidada de ambas deudas
  Estos datos provienen de aca:
  Sistema legado de energía (archivo plano de ancho fijo)
  Base de datos MySQL (facturas de acueducto)
  Lógica de negocio (cálculos y agregaciones)
  
  ¿Qué problema resuelve? Sin el patrón Builder tendríamos que usar un constructor con 6 parámetros, lo cual dificulta la legibilidad del código, pueden ocurrir errores al confundir el orden de parámetros, y complica el mantenimiento o la modificación al agregar nuevos campos.
 
  Beneficios en nuestro proyecto:
  Legibilidad: Código autoexplicativo que documenta qué valor se asigna a cada campo
  Flexibilidad: Permite construir el objeto en cualquier orden según la disponibilidad de datos
  Inmutabilidad: El constructor privado garantiza que el objeto no puede ser modificado después de su creación
  Mantenibilidad: Agregar nuevos campos al DTO solo requiere modificar el Builder
  
  Ejemplo de uso: DeudaConsolidadaDTO deuda = new DeudaConsolidadaDTO.Builder() .clienteId("0001234567") .nombreCliente("Juan Pérez") .fechaConsulta(Instant.now()) .energia(detalleEnergia) .acueducto(detalleAcueducto) .totalAPagar(new BigDecimal("275000.50")) .build();
  
  Justificación: Este patrón es especialmente útil en APIs REST donde el DTO de respuesta debe construirse dinámicamente a partir de datos obtenidos de forma asíncrona o condicional desde múltiples servicios
  
  - # Patrón DTO:

  Ubicación:```src/main/java/com/serviciudad/serviciudad/infrastructure/adapter/in/rest/dto/DeudaConsolidadaDTO.java``` 
  ```src/main/java/com/serviciudad/serviciudad/infrastructure/adapter/in/rest/dto/DetalleServicioDTO.java```
  
  ¿Está implementado correctamente? Sí, se implementaron dos DTOs: DeudaConsolidadaDTO como DTO principal de respuesta y DetalleServicioDTO como DTO anidado reutilizable para los detalles de cada servicio.
  
  ¿Por qué lo usamos? Necesitamos transferir datos entre la capa de aplicación y el cliente de la API REST sin exponer directamente las entidades internas del sistema. Los DTOs definen el contrato exacto de lo que la API devuelve al cliente.
  
  Estructura de los DTOs:
  DeudaConsolidadaDTO: Contiene clienteId, nombreCliente, fechaConsulta, energia, acueducto y totalAPagar
  DetalleServicioDTO: Contiene periodo, consumo y valorPagar (reutilizable para energía y acueducto)
  
  ¿Qué problema resuelve? Sin DTOs tendríamos que exponer directamente las entidades de base de datos o los objetos internos, lo cual genera problemas como: exponer información sensible o innecesaria, acoplar la API a la estructura interna de datos, dificultar la combinación de datos de múltiples fuentes heterogéneas, y hacer que cualquier cambio interno rompa el contrato de la API con los clientes.
  
  Beneficios en nuestro proyecto:
  Desacoplamiento: La API es independiente de la estructura interna de entidades y fuentes de datos
  Control: Definimos exactamente qué información se expone y en qué formato JSON
  Flexibilidad: Podemos combinar datos del archivo plano de energía, base de datos MySQL de acueducto y cálculos en un solo objeto de respuesta
  Seguridad: Evitamos exponer campos técnicos, IDs internos o información sensible de las entidades
  Mantenibilidad: Los cambios en la base de datos o en el archivo plano no afectan la API pública
  
  Ejemplo de estructura JSON resultante: { "clienteId": "0001234567", "nombreCliente": "Juan Pérez", "fechaConsulta": "2025-10-15T10:00:00Z", "energia": { "periodo": "202510", "consumo": "150 kWh", "valorPagar": 180000.50 }, "acueducto": { "periodo": "202510", "consumo": "15 m³", "valorPagar": 95000.00 }, "totalAPagar": 275000.50 }
  
  Justificación: En arquitecturas REST, los DTOs son fundamentales para mantener una separación clara entre la capa de presentación (API) y la capa de dominio o persistencia. Esto permite evolucionar el sistema interno sin romper el contrato con los consumidores de la API y facilita la consolidación de datos de múltiples fuentes heterogéneas como sistemas legados y bases de datos.
  
  - # Inversión de Control / Inyección de Dependencias:
  
  Ubicación:```src/main/java/com/serviciudad/serviciudad/infrastructure/adapter/in/rest/DeudaController.java```
  
  ¿Está implementado correctamente? Sí, está implementado mediante las anotaciones de Spring (@RestController) y la inyección de dependencias por constructor. Spring Boot gestiona automáticamente el ciclo de vida del controlador y del caso de uso.
  
  ¿Por qué lo usamos? Necesitamos que el controlador DeudaController use el servicio ObtenerDeudaUseCase sin crear una instancia directamente. Spring Boot se encarga de crear, configurar e inyectar automáticamente la dependencia necesaria.
  
  ¿Cómo funciona en este código?
  @RestController: Le dice a Spring que esta clase es un componente controlador REST y debe ser gestionado por el contenedor de Spring
  Constructor con parámetro: public DeudaController(ObtenerDeudaUseCase obtenerDeudaUseCase) - Spring detecta este constructor e inyecta automáticamente una instancia de ObtenerDeudaUseCase
  private final: El campo es inmutable, lo que garantiza que la dependencia no cambie durante la vida del objeto
  Sin new: En ningún momento usamos "new ObtenerDeudaUseCase()" - Spring lo hace por nosotros
  
  ¿Qué problema resuelve? Sin IoC/DI tendríamos que crear manualmente las dependencias dentro del controlador usando "new", lo cual genera acoplamiento fuerte entre clases, dificulta las pruebas unitarias porque no podemos inyectar mocks, complica el mantenimiento al tener dependencias esparcidas por todo el código, y hace imposible cambiar implementaciones sin modificar el código del controlador.
  
  Beneficios en nuestro proyecto:
  Bajo acoplamiento: El controlador no conoce ni le importa cómo se implementa ObtenerDeudaUseCase, solo sabe que existe
  Alta cohesión: Cada clase tiene una responsabilidad clara y definida
  Testabilidad: Podemos inyectar mocks o stubs del caso de uso para probar el controlador de forma aislada
  Flexibilidad: Si necesitamos cambiar la implementación de ObtenerDeudaUseCase, no tocamos el controlador
  Mantenibilidad: Spring gestiona todo el ciclo de vida (creación, configuración, destrucción) de los componentes
  
  
  Ejemplo del flujo de inyección:
  Spring Boot inicia y escanea las clases con anotaciones (@RestController, @Service, @Repository)
  Encuentra DeudaController y ve que necesita ObtenerDeudaUseCase en su constructor
  Busca una implementación de ObtenerDeudaUseCase anotada con @Service
  Crea una instancia de esa implementación (con sus propias dependencias inyectadas)
  Inyecta esa instancia en el constructor de DeudaController
  DeudaController queda listo para recibir peticiones HTTP
  Comparación sin y con DI: Sin DI (acoplamiento fuerte): DeudaController controller = new DeudaController(); // Dentro del constructor tendríamos que hacer: this.obtenerDeudaUseCase = new ObtenerDeudaUseCaseImpl(new EnergiaService(), new AcueductoService());
  Con DI (bajo acoplamiento): // Spring hace todo automáticamente DeudaController está listo para usarse sin conocer los detalles de sus dependencias
  
  Justificación: La Inversión de Control con Inyección de Dependencias es un pilar fundamental de Spring Framework. Permite construir aplicaciones modulares, mantenibles y testeables al invertir el control de creación de objetos: en lugar de que cada clase cree sus dependencias, un contenedor (Spring) las proporciona. Esto es esencial en aplicaciones empresariales donde la flexibilidad y el bajo acoplamiento son críticos para el mantenimiento a largo plazo.
  
  - # Patrón Adapter:
  
  Ubicación:```src/main/java/com/serviciudad/serviciudad/infrastructure/adapter/out/file/AdaptadorArchivoEnergia.java```
  
  ¿Está implementado correctamente? Sí, está correctamente implementado. La clase AdaptadorArchivoEnergia adapta la interfaz incompatible del archivo plano de ancho fijo (sistema legado mainframe) a la interfaz FacturasEnergiaPort que espera la aplicación.
  
  ¿Por qué lo usamos? El sistema legado de energía (Mainframe IBM Z) no tiene API y solo genera un archivo plano con formato de ancho fijo. Nuestra aplicación Java trabaja con objetos FacturaEnergia, por lo que necesitamos un adaptador que convierta el formato incompatible del archivo a objetos Java que el resto de la aplicación pueda entender.
  
  ¿Qué adapta específicamente?
  Interfaz incompatible (DESDE): Archivo de texto plano con formato de ancho fijo donde cada línea tiene posiciones fijas: id_cliente (posiciones 0-9), periodo (posiciones 10-15), consumo_kwh (posiciones 16-23), valor_pagar (desde posición 24)
  Interfaz esperada (HACIA): Lista de objetos FacturaEnergia con atributos tipados (String idCliente, String periodo, int consumoKwh, BigDecimal valorPagar)
  
  ¿Cómo funciona el adaptador?
  Implementa FacturasEnergiaPort (la interfaz que la aplicación espera)
  Lee el archivo consumos_energia.txt desde classpath o filesystem
  Procesa cada línea del archivo con el método parseLineaRobusta()
  Extrae los datos usando posiciones fijas (substring) según el formato del mainframe
  Convierte los strings a tipos Java apropiados (int, BigDecimal)
  Crea objetos FacturaEnergia con los datos parseados
  Retorna una lista de objetos que el resto de la aplicación puede usar
  
  ¿Qué problema resuelve? Sin el Adapter tendríamos que poner lógica de parseo de archivos de ancho fijo en toda la aplicación, lo cual acopla la lógica de negocio al formato del mainframe, dificulta cambiar la fuente de datos en el futuro, hace imposible testear la lógica de negocio sin el archivo, y mezcla responsabilidades de acceso a datos con lógica de dominio.
  Beneficios en nuestro proyecto:
  Encapsulación: Toda la lógica compleja de lectura y parseo de archivos está aislada en una sola clase
  Desacoplamiento: El resto de la aplicación no sabe ni le importa que los datos vienen de un archivo de ancho fijo
  Flexibilidad: Si en el futuro el mainframe expone una API REST, solo cambiamos la implementación del adaptador sin tocar la lógica de negocio
  Testabilidad: Podemos crear un AdaptadorMock para pruebas sin necesitar el archivo real
  Mantenibilidad: Los cambios en el formato del archivo solo afectan esta clase
  Robustez: Implementa parseo robusto con manejo de errores, espacios y formatos inconsistentes
  Detalles técnicos de la implementación:
  @Component: Registra el adaptador como un bean de Spring para inyección de dependencias
  implements FacturasEnergiaPort: Cumple con el contrato definido por el puerto de salida
  parseLineaRobusta(): Método privado que hace el parseo de cada línea usando posiciones fijas
  safeSubstring(): Método auxiliar que previene IndexOutOfBoundsException
  Manejo dual: Puede leer desde classpath (para desarrollo) o filesystem (para producción)
  Logging: Usa SLF4J para registrar el proceso de lectura y detectar errores
  Ejemplo del proceso de adaptación: Línea del archivo (formato mainframe): 000123456720251000001500000180000.50
  Se convierte en: FacturaEnergia( idCliente = "0001234567", periodo = "202510", consumoKwh = 1500, valorPagar = 180000.50 )

  Justificación: El patrón Adapter es esencial cuando integramos sistemas legados con interfaces incompatibles. En este caso, permite que nuestra aplicación moderna en Java consuma datos de un mainframe antiguo sin contaminar la lógica de negocio con detalles de bajo nivel sobre formatos de archivos. Es un patrón fundamental en arquitecturas hexagonales donde los adaptadores traducen entre el mundo externo y el dominio de la aplicación.


- # Patrón Repository (Provisto por Spring Data JPA)

Ubicación:
```src/main/java/com/serviciudad/serviciudad/infrastructure/adapter/out/persistence/FacturaAcueductoRepositoryJpa.java```
(Complementos relacionados:
```src/main/java/com/serviciudad/serviciudad/infrastructure/adapter/out/persistence/FacturaAcueductoEntity.java```
```src/main/java/com/serviciudad/serviciudad/infrastructure/adapter/out/persistence/FacturasAcueductoAdapter.java)```

¿Está implementado correctamente?
Sí. En el repositorio se usa Spring Data JPA extendiendo JpaRepository<FacturaAcueductoEntity, Long> y se definió un método derivado findByIdCliente(String idCliente) para consultas frecuentes por identificador de cliente. La entidad JPA (FacturaAcueductoEntity) está correctamente anotada y mapeada a la tabla facturas_acueducto. El adaptador (FacturasAcueductoAdapter) usa ese repository para obtener entidades y transformarlas al modelo de dominio FacturaAcueducto.

Por qué lo usamos (motivación en este proyecto)
Necesitamos abstraer el acceso a la base de datos MySQL (lectura/escritura de facturas de acueducto) sin mezclar sentencias SQL ni detalles de JPA dentro de la lógica de negocio. Spring Data JPA nos da una implementación automática del patrón Repository que:

Proporciona operaciones CRUD básicas sin escribir código repetitivo.

Permite definir métodos por convención (findBy...) para consultas comunes.

Integra manejo de transacciones y paginación de forma sencilla.

¿Qué problema resuelve?
Sin el patrón Repository tendríamos que escribir muchas clases con JDBC/JPA boilerplate: EntityManager creation, queries, mapeos, manejo de transacciones, etc. El Repository abstrae esa complejidad y nos permite centrarnos en la lógica (use cases) y en el mapeo entre entidad y dominio.

Beneficios en nuestro proyecto

Reducción de código repetitivo: save, findById, findAll, delete ya vienen implementados.

Rapidez de desarrollo: crear nuevos queries es tan simple como declarar métodos con nombres semánticos (findByIdClienteAndPeriodo(...)).

Testabilidad: en tests unitarios mockeas la interfaz del repositorio; en integración puedes usar H2 o Testcontainers.

Intercambiabilidad: si mañana cambias MySQL por otro RDBMS o por una API, sólo cambias el adaptador/implementación.

Soporte a características avanzadas: paginación, sorting, @Query para JPQL/SQL nativo, especificaciones (Specification API).

Justificación (resumen final):
El patrón Repository (mediante Spring Data JPA) abstrae la complejidad del acceso a datos y reduce boilerplate, acelera el desarrollo y mejora la mantenibilidad. En nuestro monolito hexagonal sirve como el adaptador de persistencia que implementa el port out (FacturasAcueductoPort) permitiendo que los use cases y el dominio permanezcan limpios, testables y sin acoplamiento a detalles de MySQL/AWS.
