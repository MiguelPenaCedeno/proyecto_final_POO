# Sistema de Gestion de Equipos de Laboratorio

Proyecto de Programacion Orientada a Objetos en Java. Nuestro objetivo para este proyecto fue diseñar un sistema de gestion de equipos de laboratorio, usuarios y sesiones de uso, con control de acceso por rol y persistencia en archivos de texto y binarios.

## Paquetes

- model: clases del dominio (Usuario y subclases, Equipo, Sesion, enums)
- service: logica del sistema (SistemaGestion)
- persistence: carga desde archivos de texto (CargadorTexto)
- exceptions: excepciones personalizadas
- ui: menu por consola (MenuPrincipal)

## Acceso

El sistema arranca con un administrador por defecto para el primer ingreso.

- Codigo: admin
- Clave: admin123

## Formato de los archivos de texto

Una entidad por linea, campos separados por asterisco, sin encabezados.

### Equipos

```
codigo*nombre*tipo*laboratorio*estado*valor*usos
```

Tipo: COMPUTADOR, OSCILOSCOPIO, MICROSCOPIO, MULTIMETRO, IMPRESORA_3D
Estado: DISPONIBLE, EN_USO, MANTENIMIENTO

Ejemplo:

```
E01*Osciloscopio Tektronix*OSCILOSCOPIO*LabElectronica*DISPONIBLE*2500000*5
```

### Usuarios

El campo final depende del rol, que es el tercer campo de la linea.

```
ESTUDIANTE: codigo*nombre*rol*clave*programa
MONITOR: codigo*nombre*rol*clave*area
ADMINISTRADOR: codigo*nombre*rol*clave
```

Rol: ESTUDIANTE, MONITOR, ADMINISTRADOR

Ejemplo:

```
U01*Carlos Ramirez*ESTUDIANTE*clave123*Ingenieria Electronica
U03*Luis Gomez*MONITOR*clave789*LabElectronica
U04*Sofia Mendez*ADMINISTRADOR*admin456
```

El codigo admin no debe usarse en el archivo porque ya esta registrado por
defecto y se omitiria al cargar

## Estado del sistema

El estado completo se guarda y recupera por serializacion en un archivo
binario, escribiendo y leyendo las tres colecciones en el mismo orden:
usuarios, equipos y sesiones.
