# README.md — Proyecto serviciudad (backend Spring Boot)

Este repositorio contiene el backend (Spring Boot, Java 17) del proyecto serviciudad con arquitectura hexagonal. El servicio consolida facturas desde un archivo legacy (energía) y desde una base de datos MySQL (acueducto).

- Para clonar el repositorio, cree una carpeta en el explorador de archivos, abrala en vscode o su editor de codigo preferido, despues abre una terminal y ejecuta lo siguiente:

    ```git clone https://github.com/ERSAgit/PROYECTO_SERVICIUDAD.git```
  
    ```cd .\PROYECTO_SERVICIUDAD```

- Una vez con el proyecto en el editor de codigo, abra el archivo de ```ServiciudadApplication.java``` que esta en ```src\main\java\com\serviciudad\serviciudad\ServiciudadApplication.java``` Alli presione ```run``` para ejecutar el proyecto. Tenga en cuenta que el proyecto esta en maven wrapped si desea usar comandos ejecute en una terminal powershell ```.\mvnw.cmd spring-boot:run``` o ```./mvnw spring-boot:run``` en una terminal bash.

- Una vez ejecutado con exito, en postman importe la coleccion adjuntada a la entrega o cree una peticion GET de la siguiente manera ```http://localhost:8080/api/v1/clientes/{id}/deuda-consolidada``` donde ```{id}``` es el id del usuario ej: ```0001000010```, ```http://localhost:8080/api/v1/clientes/0001000010/deuda-consolidada``` Envie la peticion y deberia recibir el resultado.

- Respecto a la base de datos, esta se encuentra alojada en AWS, si desea ver la informacion puede crear una nueva connection en MySQL workbench de la siguiente manera: ```connection Name: Conexion AWS```, ```Connection Method: Standar (TCP/IP)```, ```Hostname: database-1.cnse4eayeaej.us-east-2.rds.amazonaws.com```, ```Port: 3306```, ```Username: admin``` probar y si requiere contraseña ```Password: soyeladmin123```
