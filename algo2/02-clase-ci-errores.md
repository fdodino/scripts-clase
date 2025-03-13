## Empezamos hablando de la integración continua 18:00-19:00

## Introducción a CI

Queremos que en un proyecto no solo sea fácil instalar el software necesario, sino que también **queremos integrar los cambios lo más temprano posible**.

Buenas prácticas

- correr los tests regularmente (antes y después de cada cambio)
- commitear seguido, micro-commits, explicando en forma breve pero directa cada cambio
- y pushear también seguido

Ok, pero este proceso si es manual está sujeto a que todos sigamos esas buenas prácticas. Entonces está bueno automatizar lo que sea posible.

Abrimos [estas diapositivas](https://docs.google.com/presentation/d/1Wy3HerqABw-dvMX-PyzzR1gPUw0uuRak2lAFO5HTzMc/edit#slide=id.gd376884672_0_233).

Configuramos el CI agregando en la carpeta el archivo [`build.yml`](./clase2/build.yml)

```bash
+ .github
  + workflows
    - build.yml
```

El archivo que nosotros les damos a uds. es ligeramente distinto porque trabajan en repos privados.

Tenemos que configurar git en un repo remoto:

- Creamos el repo en github.com/fdodino
- Agregamos el repo remoto como origin
- `git add .` / `git commit -m "Initial commit"` / `git push --set-upstream origin master`

Y vemos en la solapa Actions lo que ocurre: solito baja el proyecto, descarga las dependencias (con gradle, no en IntelliJ) y ejecuta los tests.

Github Actions funciona como un servidor de integración continua, otros son Jenkins, Circle CI, etc.

> Corolario

- si les funciona en IntelliJ y falla el CI, o viceversa hay algo en el `build.gradle.kts` que hay que arreglar
- si se olvidaron de correr los tests, el CI lo hace por ustedes
- si los tests fallan o el proyecto no compila, les llega un mail

### Agregamos el badge al README

Es una buena práctica sumar el badge para confirmar que el proyecto goza de buena salud. Mostramos cómo hacerlo siguiendo [este video](https://youtu.be/DEF-aGV_7Qg).


### Cobertura

Los tests cubren diferentes casos de prueba (las clases de equivalencia). Hay herramientas que

- ejecutan los tests
- y miden por qué líneas de código pasan

Una métrica es eso... una métrica. No hay que volverse loco para tener 100% de cobertura, cuando tenés código legacy al menos está bueno tener ese % e irlo subiendo de a poco, con tickets que digan: "queremos cubrir 80% de este módulo o esta funcionalidad". Nos permiten establecer un objetivo a cumplir.

Entramos a 

https://codecov.io/

- nos logueamos
- agregamos nuestro repo
- tenemos que configurar los settings con el token de codecov
- forzamos un cambio
- vemos cómo se puede navegar el código con tres colores: verde si la línea está cubierta, amarilla si hay ramas que están cubiertas y ramas que no (un if) o roja si no está cubierta

### Agregamos el badge al README

Lo mismo, está bueno publicar la métrica. ¿Cuánta cobertura hace falta tener en el TP? 30% es poco, 70% es mejor... hablen con su docente de TP.

## Cómo corregir un error en Runtime 19:00 - RECREO - 20:40

Partimos de clase-seguros-errores, que tiene 

- un enunciado ligeramente cambiado
- estratégicamente varios errores adrede. Hay que seguir el [README](https://github.com/uqbar-project/eg-seguros-kotlin) del proyecto.

Veamos el enunciado:

> Como requerimiento extra, los clientes normales deben registran las fechas en los que se consulta si se puede pagar un siniestro solamente cuando tienen deuda (sin duplicarlas, si un cliente con deuda consultó 3 veces el sábado pasado y 5 veces el lunes, debe figurar el sábado y el lunes como días en los que se realizó la consulta).

### Primera iteración: usamos el Stack Trace

Tenemos 

- el archivo [Cliente.kt](./clase2/build.yml)
- y el de test [CobroClienteSpec.kt](./clase2/CobroClienteSpec.kt)

Al ejecutar fallan los tests. La primera herramienta para ver los errores parece muy obvia, pero uno tiene que acostumbrarse a leer el stack trace, y a moverse a través del envío de mensajes: también uno aprende que de la pila de envío de mensajes nos importan más las que refieren a nuestro código, porque es raro que haya un error en las bibliotecas (aunque puede pasar que descubramos un bug o bien que la estemos usando mal a la biblioteca).

Hacemos un pequeño cambio:

```kt
fun registrarConsulta() {
    if (this.esMoroso() && !tieneConsultas(LocalDate.now())) {
        diasDeConsulta.add(LocalDate.now())
    }
}
```

y ya pasa un test.

### Segunda iteración: el debugger

En Algo2 vamos a tener la suerte de poder utilizar como herramienta el debugger: 

- contamos cómo poner un breakpoint
- hay breakpoints que se activan con una condición, pueden investigar cómo activarlos
- después nos movemos paso a paso, step in, step over, step out, run... etc.
- el debugger permite inspeccionar los objetos en memoria, para darnos cuenta por ejemplo de que dos Dates en java pueden ser iguales pero no idénticos (apuntan a objetos diferentes)

> === (identidad) vs. == (igualdad) son dos comparaciones diferentes: dos referencias son iguales si apuntan al mismo objeto pero el operador (==) se puede redefinir, como por ejemplo en los Dates, donde dos referencias a Date son iguales si se refieren a la misma fecha, por más que se trate de dos objetos distintos.

En general los objetos que representan valores redefinen la igualdad: number, strings, fechas, mails, direcciones, etc.

```kt
fun tieneConsultas(dia: LocalDate) =  diasDeConsulta.any { it == dia }  // cambiamos el === por ==
```

Recomendar que vean el README que explica paso a paso con gifs cómo usar el debugger, más allá de que lo veamos en clase.

> Ojo con abusar del debugger, a veces te va a llevar un buen tiempo encontrar el lugar donde querés debuggear un error y es fácil perder el foco.

## RECREO

### Otras herramientas que sirven para ver errores: println

La búsqueda binaria: 

- println("1"), println("2") => saber dónde ocurre un error. Cuando tenés una arquitectura compleja y no tenés debugger a veces no te queda otra.

Volvemos para atrás el código a cuando teníamos 2 errores y ponemos println en `registrarConsulta`:

```kt
fun registrarConsulta() {
    println("consultas: ${diasDeConsulta}")
    val ultimaConsulta = diasDeConsulta.last()
    println("tiene consultas? ${ultimaConsulta}")
    if (this.esMoroso() && ultimaConsulta === LocalDate.now() ) {
        diasDeConsulta.add(LocalDate.now())
    }
}
```

No es tan efectivo como el debugging o tan directo como leer el stack trace, pero es importante tener diferentes técnicas para cuando estamos trabados.


### Comentar código que falla

Al comentar código, vemos si persiste un error, o si sigue ocurriendo. Esta técnica se parece al que usan los plomeros para encontrar una fuga, o un gasista o un electricista.

En el caso del error anterior, comentamos el `registrarConsulta()` y vemos que uno de los dos tests pasa.

```kt
override fun puedeCobrarSiniestro(): Boolean {
    // registrarConsulta()
    return !esMoroso()
}
```

El otro test pasa también si comentamos el segundo assert:

```kt
    it("si tiene deuda no puede cobrar siniestro y debe registrar la consulta del libre deuda") {
        // Arrange
        val clienteMoroso = ClienteNormal()
        // Act
        clienteMoroso.facturar(10)
        // Assert
        clienteMoroso.puedeCobrarSiniestro() shouldBe false
        // clienteMoroso.tieneConsultas(LocalDate.now()) shouldBe true
    }
```

Con lo cual de las dos funcionalidades (saber si puede cobrar el siniestro y registrar consultas de morosos para una fecha), la segunda es la que tiene un problema. Aislamos uno de los dos requerimientos para concentrarnos solo en ése. Cuando hay un error en producción, poder identificar qué funcionalidad es la que no está andando bien es muy importante, sobre todo cuando no tenemos oportunidad de hacer un revert de nuestro último pasaje a producción.

### Comentar código cuando todo funciona ok para ver si fallan los tests

Al revés de cuando estamos buscando un error, comentar código es otra forma de probar que los tests tienen buena calidad: no es tanto la cantidad (% de cobertura)
 sino saber que una funcionalidad tiene al menos un test asociado.

## 20:40 Empezamos con el manejo de proyectos

Juguemos un poco a diseñar a ver qué se les ocurre:

- cómo quieren arrancar: TDD, código y tests, diagrama estático, diagrama dinámico, etc.
- qué requerimientos atacarían primero
- cuánto puede marear tarea/subtarea vs. tarea simple y compuesta
- ver qué cosas se repiten del enunciado (el costo)
- llevarlos para el costo de una tarea

Eso permite que ya estén en tema. 
21:20 cortamos.
