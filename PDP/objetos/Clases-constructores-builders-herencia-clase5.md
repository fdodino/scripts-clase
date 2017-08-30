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

Para poder crear una instancia de una clase, necesitamos que esa clase defina al menos un **constructor**, un mecanismo que nos dice qué debe ocurrir cuando inicializamos un objeto.

Por defecto, cuando creamos una clase no debemos escribir ningún constructor, porque viene "de fábrica" con un constructor sin parámetros. Entonces al ver este código

```xtend
class Auto {
	var patente
	var kilometros = 0

	method esNuevo() = kilometros < 2000
}
```

yo se que puedo crear un auto en la consola de esta manera:

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

Debemos inicializar la referencia "patente". El problema es que si escribimos un valor para patente, será un valor compartido para todos los autos.

## Constructores específicos

Escribiremos entonces un constructor específico para autos: para instanciar un auto necesitamos pasarle la patente:

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
