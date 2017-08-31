# Clase, builders y herencia

por Fernando Dodino - Agosto 2017
Distribuido con licencia [Creative commons Share-a-like](https://creativecommons.org/licenses/by-sa/4.0/legalcode)

## Repaso clases y objetos

19:00 - 20:30 Fer

Arrancamos repasando la diferencia entre objeto y clase.
¿Cuándo usar objetos anónimos, cuándo usar wko y cuándo clase?

- los **objetos anónimos** tienen un alcance acotado, por ejemplo un test, o la resolución de un requerimiento (un método y todo el encadenamiento de mensajes que sale de ese método). En el ejemplo de los taxistas construíamos pasajeros cuyo ciclo de vida era corto. Lo mismo pasa cuando evaluamos filter, o map de una colección: el objeto que representa al bloque no tiene nombre y no nos importa que lo tenga, existe para resolver el requerimiento puntual.

```xtend
method taxistasBuenos() = taxistas.filter { taxista => taxista.esBueno() } 
// las llaves encierran un objeto anónimo que representa el criterio
// de selección de un taxista
```

- **wko**: son objetos conocidos, porque están representando un concepto de negocio dentro de la aplicación. Esto ocurre cuando un objeto tiene un comportamiento específico, y nos interesa modelarlo en forma separada de otros objetos. El concepto de clase es muy útil pero si un lenguaje solo provee el mecanismo de instanciación con objetos se produce a) un sobrediseño (construimos una clase cuando pensamos en un solo objeto), b) toda una burocracia de crear la clase para luego instanciar un objeto y nada más.

- por último, las **clases** son importantes cuando se que existen múltiples objetos que comparten comportamiento y no tiene sentido que los nombre por separado: el viaje que hice ayer en colectivo, se parece mucho al viaje de la semana pasada. Si solo difieren en la información que guardan las referencias, el comportamiento se debe ubicar en un solo lugar para no repetir código. 

## Ojo cuando un método devuelve un bloque

Volviendo al tema de los objetos anónimos, fíjense lo que pasa cuando definimos un método así:

```scala
object pepita {
     var energia = 0
     method energia(_energia) = { energia = _energia }
}
```

El método energia(_energia) devuelve ... un bloque de código.
Entonces cuando ejecuten en el REPL:

```bash
>>> pepita.energia(2)
a Closure
```

Lo que les devuelve es un Closure, una lambda que lo que hace es:

```xtend
{ energia = 2 }
```

porque estamos enviando el mensaje energia(2). Es decir, **no ejecuta el código** sino que genera código para que lo podamos ejecutar. Esto es algo muy poderoso, pero que no queremos en un setter.

## Formas de instanciar un objeto

Los objetos anónimos se creaban en forma programática:

```xtend
const alumno = object {
	method estudia() = true
}
```

Mientras que los wko se instanciaban solos en el momento en que uno ejecuta la consola REPL, o bien un test, o un programa.

También están los literales de Wollok: objetos que tienen una sintaxis propia para construirse:

```xtend
2        // permite referenciar al número 2
"hola"   // construye un String cuyo valor es "hola"
[7]      // construye una lista con un único elemento: el número 7
```

¿Cómo instanciamos una fecha?

```xtend
>>> new Date()
```

Pero también podemos instanciarlo con valores:

```xtend
>>> new Date(17, 8, 2017)
```

## Introducción a Constructores

Para poder crear una instancia de una clase, necesitamos que esa clase defina al menos un **constructor**, un mecanismo que nos dice qué debe ocurrir cuando instanciamos un objeto.

Por defecto, cuando creamos una clase no debemos escribir ningún constructor, porque viene "de fábrica" con un constructor sin parámetros. Entonces al ver este código

```xtend
class Auto {
	var patente
	var kilometros = 0

	method esNuevo() = kilometros < 2000
}
```

sabemos que para crear un auto en la consola lo hacemos de esta manera:

```xtend
>>> const fierrito = new Auto()
```

Pero la patente es una referencia que debería ser constante, al fin y al cabo si no hay nada raro acompaña al auto en todo su ciclo de vida. Entonces cambiamos la definición de var a const:

```xtend
class Auto {
	const patente
	var kilometros = 0

	method esNuevo() = kilometros < 2000
}
```

Pero ahora nuestra clase Auto no compila. 

![images](images/autoConstPatente.png)

Debemos inicializar la referencia "patente". El problema es que si definimos un valor para patente, será un valor compartido para todos los autos.

## Constructores específicos

Escribimos entonces un constructor específico: para instanciar un auto necesitamos pasarle la patente:

```xtend
class Auto {
	const patente
	var kilometros = 0

	constructor(_patente) {
		patente = _patente
	}

	method esNuevo() = kilometros < 2000
}
```

También podríamos inicializar los kilómetros del auto en el constructor:


```xtend
class Auto {
	const patente
	var kilometros

	constructor(_patente) {
		patente = _patente
		kilometros = 0
	}

	method esNuevo() = kilometros < 2000
}
```

Mientras no definamos más constructores, no hay diferencia en inicializar en un lugar u otro. Si escribimos más constructores, es más cómodo dejarlo como estaba antes...

Entonces en la consola REPL para poder crear un auto necesitaremos pasarle la patente:

```
>>> new Auto()
ERROR: Wrong number of arguments. Should be new Auto(_patente) (line: 1)
>>> new Auto(2, "hola")
ERROR: Wrong number of arguments. Should be new Auto(_patente) (line: 1)
>>> new Auto("RVM363")
a Auto[patente=RVM363, kilometros=0]
```

Lo bueno es que Wollok nos avisa cuando nos faltan o nos sobran parámetros. Y como vemos, el constructor "por defecto" deja de ser válido cuando construimos nuestros propios constructores.

## Agregando constructores

Como dijimos antes medio al pasar, se pueden crear constructores adicionales. Por ejemplo, podríamos instanciar un auto con una patente y un kilometraje específico:

```xtend
>>> new Auto("RVM363")
a Auto[patente=RVM363, kilometros=0]
>>> new Auto("RVM363", 180000)
a Auto[patente=RVM363, kilometros=180000]
```

Esto se logra de la siguiente manera:

```xtend
class Auto {
	const patente
	var kilometros = 0

	constructor(_patente) {
		patente = _patente
	}

	constructor(_patente, _kilometros) {
		patente = _patente
		kilometros = _kilometros
	}
	
	method esNuevo() = kilometros < 2000
}
```

La pregunta que el lector podría hacerse es: ¿qué pasa si tengo un objeto con muchas referencias? ¿debo crear tantos constructores como referencias tenga?

## El caso de estudio: un viaje

Consideremos que un viaje tiene

- un chofer
- un auto
- varios pasajeros
- los kilómetros recorridos
- el costo
- la fecha del viaje

```xtend
class Viaje {
	var chofer
	var auto
	var pasajeros
	var kilometros
	var costo
	var fecha

}
```

Instanciar un viaje en un fixture no es algo simpático, estamos repitiendo una y otra vez el objeto receptor, y es algo incómodo:

```xtend
describe "tests de viajes" {

	const viajeACasanovas

	fixture {
		viajeACasanovas = new Viaje()
		viajeACasanovas.chofer(daniel), peugeot404, [susana], 5.9, 260, new Date())	
	}
```


## Objetos que construyen objetos

Vamos a definir un objeto que nos ayudará a construir un viaje. Es decir, vamos a definir un **Builder**.

Primero reemplazaremos el new Viaje() por un new ViajeBuilder()

```xtend
	fixture {
		viajeACasanovas = new ViajeBuilder(). ...	
```

El ViajeBuilder va a definir una responsabilidad para setear cada uno de los valores. Pero en lugar de utilizar un setter común, lo que nos obligaría a repetir una y otra vez el objeto receptor...

```xtend
	fixture {
		viajeACasanovas = new ViajeBuilder()
		viajeACasanovas.chofer(daniel)
		viajeACasanovas.kilometros(5.6)
		... etc ...
```

... que sería lo mismo que hacerlo con el viaje, vamos a introducir un pequeño cambio: al asignar una referencia vamos a devolver el mismo ViajeBuilder. Esto permite encadenar los mensajes uno por uno:

```xtend
	fixture {
		viajeACasanovas = new ViajeBuilder()
			.chofer(daniel)
			.kilometros(5.6)
			... etc ...
```

Y al final debería devolver el viaje propiamente dicho, para que la referencia viajeACasanovas apunte a un objeto viaje, y no a un ViajeBuilder:

```xtend
	fixture {
		viajeACasanovas = new ViajeBuilder()
			.chofer(daniel)
			.kilometros(5.6)
			... etc ...
			.build()
```


## Implementación del Builder

Una opción es que el Builder tenga una referencia al viaje:

```xtend
class ViajeBuilder {

	const viaje
	
	constructor() {
		viaje = new Viaje()
	}
}
```

Claro, esto requiere que viaje tenga nuevamente un constructor por defecto. La otra opción es que el ViajeBuilder tenga referencias particulares:

```xtend
class ViajeBuilder {

	var chofer
	var pasajeros
	... etc ...

	// no requiere constructor	
}
```

Cuando tenemos muchas referencias la segunda alternativa no solo es es un poco más incómoda sino que también produce repetición de código. Vamos entonces por la primera opción, generando un constructor vacío para Viaje.

## Ahora sí, la implementación de un Builder

```xtend
class ViajeBuilder {

	const viaje
	
	constructor() {
		viaje = new Viaje()
	}
	
	method chofer(_chofer) {
		viaje.chofer(_chofer)
		return self
	}

	method kilometros(_kilometros) {
		viaje.kilometros(_kilometros)
		return self
	}

	...

	method build() {
		return viaje
	}
	
}
```

Algunas observaciones

- cada setter devuelve además el objeto receptor, para poder encadenar los mensajes. Aquí vemos un ejemplo de un método que **si bien es acción, también está devolviendo valores**
- el lector puede sospechar que el ViajeBuilder no aporta ningún valor agregado a nuestra solución, ya que además debe implementar los setters de Viaje (que todavía no están definidos). Hasta ahora solo parece un *syntactic sugar*, pero el Builder provee algunas funcionalidades interesantes:
	1. permite construir setters simplificados, como al ingresar una fecha o cargar los pasajeros en forma individual (en lugar de tener que construir una lista aparte)
	2. el constructor puede inicializar valores diferentes al que tenga un viaje, para ciertos casos
	3. el método build() puede incorporar validaciones, para detectar inconsistencias en la creación de un viaje

Vemos la implementación final del Builder, con cada uno de los puntos

```xtend
class ViajeBuilder {

	const viaje
	
	constructor() {
		viaje = new Viaje()
		viaje.fecha(new Date())
	}
	
	method chofer(_chofer) {
		viaje.chofer(_chofer)
		return self
	}

	// 2. Agregamos los pasajeros en forma individual, 
	// esto evita generar una lista en el test o en el REPL
	method agregarPasajero(pasajero) {
		const pasajeros = viaje.pasajeros()
		pasajeros.add(pasajero)
		viaje.pasajeros(pasajeros) // otra opción es que exista el método agregarPasajero en Viaje, algo muy recomendable
		return self
	}

	// 2. Ingresamos una fecha con un setter específico
	method fecha(dia, mes, anio) {
		viaje.fecha(new Date(dia, mes, anio))
		return self
	}

	... más setters ...

	// 3. Agregamos validaciones en el build
	method build() {
		if (viaje.pasajeros().isEmpty()) {
			self.error("Viaje: debe ingresar pasajeros")
		}
		if (viaje.kilometros() <= 0) {
			self.error("Viaje: debe ingresar kilometraje")
		}
		if (viaje.costo() <= 0) {
			self.error("Viaje: debe ingresar el costo en $")
		}
		return viaje
	}
	
}

describe "tests de viajes" {

	const viajeACasanovas

	fixture {
		viajeACasanovas = new ViajeBuilder()
			.chofer(daniel)
			.agregarPasajero(susana)
			.agregarPasajero(manuel)
			.auto(peugeot404)
			.costo(260) // asignamos en el orden que queramos
			.kilometros(5.9)
			.build()
	}

	...
```

De hecho, mejoraremos el método build, porque vamos a delegar la responsabilidad a la clase Viaje, ya que le corresponde a ella definirlo:

```xtend
class ViajeBuilder {

	// 3. Delegamos al objeto viaje la validación
	method build() {
		viaje.validar()
		return viaje
	}

```

## Cambios al Viaje

Podríamos pensar en un objeto **inmutable**, es decir que nuestro viaje se debería construir y no modificarse a lo largo de su ciclo de vida:

```xtend
class Viaje {
	var chofer
	var auto
	var pasajeros
	var kilometros
	var costo
	var fecha

	constructor(_chofer, _auto, _pasajeros, _kilometros, _costo, _fecha) {
		chofer = _chofer
		auto = _auto
		pasajeros = _pasajeros
		kilometros = _kilometros
		costo = _costo
		fecha = _fecha
	}	
}
```

Esto podría traer serias consecuencias a mi fixture, pero vamos a adaptar la clase ViajeBuilder:

- en el constructor de ViajeBuilder ya no podemos crear el viaje, porque nos falta información
- por lo tanto, nuestra variable viaje pierde sentido, debemos tener referencias individuales
- la construcción del viaje se difiere hasta el build(), que es cuando puedo efectivamente generar un viaje

Vemos los cambios implementados en el Builder:

```
class ViajeBuilder {
	var chofer
	var auto
	var pasajeros = []
	var kilometros
	var costo
	var fecha

	method fecha(dia, mes, anio) {
		fecha = new Date(dia, mes, anio)
		return self
	}

	method agregarPasajero(pasajero) {
		pasajeros.add(pasajero)
		return self
	}

	// otros setters que devuelven self

	method build() {
		const viaje = new Viaje(chofer, auto, pasajeros, kilometros,costo, fecha)
		viaje.validar()
		return viaje
	}

}
```

¿Qué debemos modificar en nuestro fixture?
Nada.
Nada.
Volvemos a repetir: NADA.

```xtend
describe "tests de viajes" {

	const viajeACasanovas

	fixture {
		viajeACasanovas = new ViajeBuilder()
			.chofer(daniel)
			.agregarPasajero(susana)
			.agregarPasajero(manuel)
			.auto(peugeot404)
			.costo(260) // asignamos en el orden que queramos
			.kilometros(5.9)
			.build()
	}

```

> Y si tengo 5, 6 ó 10 viajes... es muy bueno que no tenga que modificar mi fixture, es lo que paga el precio de tener que construir un Builder.

## El builder no es para todos

El builder no es una solución perfecta

- está acoplado con el objeto que crea (es algo obvio pero vale recordarlo), por lo tanto...
- ...hay también cierta duplicación de lógica
- y la posibilidad de que el builder robe responsabilidades de instanciación (como la validación o la asignación de referencias)

Cuando el objeto a instanciar es trivial, el builder es una herramienta que agrega burocracia y es un síntoma de sobrediseño.

Pero...

![images](images/adapter.png)

No obstante, es un adaptador que **evita que los que usan al viaje se vean afectado cuando la interfaz de su construcción cambia**, algo muy probable cuando tengo muchas referencias internas que asignar, como en este caso pasó con el viaje.


