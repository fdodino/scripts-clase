# Clase, builders y herencia

## Repaso clases y objetos

19:00 - 20::00 Fer

Arrancamos repasando la diferencia entre objeto y clase.
¿Cuándo usar objetos anónimos, cuándo usar wko y cuándo clase?

- los objetos anónimos tienen un alcance acotado, por ejemplo un test, o la resolución de un requerimiento (un método y todo el encadenamiento de mensajes que sale de ese método). En el ejemplo de los taxistas construíamos pasajeros cuyo ciclo de vida era corto. Lo mismo pasa cuando evaluamos filter, o map de una colección: el objeto que representa al bloque no tiene nombre y no nos importa que lo tenga, existe para resolver el requerimiento puntual.
- wko: son objetos conocidos, porque están representando un concepto de negocio dentro de la aplicación. Esto ocurre cuando un objeto tiene un comportamiento específico, y nos interesa modelarlo en forma separada de otros objetos. El concepto de clase es muy útil pero si un lenguaje solo provee el mecanismo de instanciación con objetos se produce a) un sobrediseño (construimos una clase cuando pensamos en un solo objeto), b) toda una burocracia de crear la clase para luego instanciar un objeto y nada más.
- por último, la clase es importante cuando se que existen múltiples objetos que comparten comportamiento y no tiene sentido que los nombre por separado: el viaje que hice ayer en colectivo, se parece mucho al viaje de la semana pasada. Si solo difieren en la información que guardan las referencias, el comportamiento se debe ubicar en un solo lugar para no repetir código. 

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
pepita.energia(2)
a Closure
```

Lo que les devuelve es un Closure, una lambda que lo que hace es:

```xtend
{ energia = 2 }
```

porque estamos enviando el mensaje energia(2).

## Constructores

