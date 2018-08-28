# Clase 3 Objetos

por Fernando Dodino - Agosto 2017
Distribuido con licencia [Creative commons Share-a-like](https://creativecommons.org/licenses/by-sa/4.0/legalcode)

Cosas útiles:

- En Wollok, levantamos un REPL y evaluamos

```xtend
>>> new Date() // nos da un objeto que representa la fecha de hoy
>>> new Date(8, 9, 2017)  // nos da un objeto que representa el 8 de septiembre, no el 9 de agosto, jeje
```

En particular Ctrl + Shift + F3, permite buscar Date... y ahí pueden navegar el código para ver si hay algo que les sirva.
Parados sobre el archivo con Ctrl + O pueden navegar definiciones y métodos... espero les sea útil.

# Intro a Colecciones (10 minutos)

Una colección es una agrupación de objetos. Tenemos dos estilos diferentes para crear series de objetos:

- si nos importa el orden, creamos una lista con el literal `[]`
- si no queremos duplicados y no nos importa el orden, creamos un conjunto con el literal `#{}`

Por ejemplo podemos crear una lista de choferes en el REPL

```xtend
>>> const choferes = [daniel, alejandro, luciana]
```

Esto implica que hay un orden: primero Daniel, luego Alejandro y finalmente Luciana.

## Agregar y sacar elementos a una colección

Puedo agregar un elemento (es dinámico, a diferencia del vector):

```xtend
>>> const choferes = [daniel, luciana]
[daniel[], luciana[edad=37]]
>>> choferes.add(alejandro)
>>> choferes
[daniel[], luciana[edad=37], alejandro[]]
>>> 
```

Pero además, veremos que las listas y los conjuntos son **polimórficos**, si cambio la primera línea que instancia los choferes, ¡también funciona el mensaje add!

```xtend
>>> const choferes = #{daniel, luciana}
#{luciana[edad=37], daniel[]}
>>> choferes.add(alejandro)
>>> choferes
#{alejandro[], luciana[edad=37], daniel[]}
```

Solo que Alejandro aparece primero que Luciana y Daniel, porque el orden en el que se agregan es indistinto.

## Un uso práctico (1 hora)

Ahora queremos agregar el siguiente requerimiento: "¿Con quién prefiere viajar un pasajero dada una lista de choferes?"

- Susana quiere viajar siempre con el último de los choferes que le ofrecen
- Norma lleva a cualquiera que la lleve a ella
- Beto elige al que cobra más caro

### Susana elige al último chofer

Esto nos dice algunas cosas:

- necesitamos pensar efectivamente en una lista, no tiene sentido el último en un conjunto porque podría ser uno en algún caso y otro en otro caso
- tenemos que ver qué mensajes soporta una lista, una opción sigue siendo Ctrl + Shift + F3, buscar List e investigar el mismo código de Wollok (y su documentación), pero si se marean con eso pueden en todo caso acceder a la famosa [Guía de lenguajes](https://docs.google.com/document/d/1oJ-tyQJoBtJh0kFcsV9wSUpgpopjGtoyhJdPUdjFIJQ/edit). Allí vemos que existe last() como mensaje.
- el método ¿responde una pregunta o es una acción? Lo primero, entonces usamos el = (también podemos usar llaves + return, es a gusto de ustedes)

```scala
object susana {
	
	method elegirChofer(choferes) = choferes.last()

}
```

### Norma

Norma tiene estas definiciones de edad y esJoven:

- nació el 13/05/1964
- para ella los jóvenes son las personas que tienen menos de 60

Instanciamos un Date , y calculamos la edad en base a un cálculo aproximado:

```scala
object norma {
	const fechaNacimiento = new Date(13, 5, 1964) // ¿por qué const? Porque no vamos a querer cambiar esa referencia

	method edad() = (new Date() - fechaNacimiento) / 365 // aproximadamente

	method esJoven() = self.edad() < 60
}
```

Queremos además que sea polimórfica a Susana respecto a saber elegir un chofer:

- **hay que elegir cualquiera:** para eso se debe buscar un mensaje que se adapte a "cualquier elemento de una lista". Existe, se llama anyOne() pero no está en la guía de lenguajes.
- **que la lleve a ella:** esto implica filtrar aquellos choferes que la llevarían. El mensaje filter() nos va a servir

```xtend
>>objeto norma
method elegirChofer(choferes) = choferes.filter { chofer => chofer.llevaA(self) }.anyOne()
```

Primero aplicamos el mensaje filter, que necesita... bueno, para qué engañarlos... es una lambda que volvió en forma de fichas. La expresión recibe un chofer y el criterio para elegir cada uno de los choferes es que pueda llevarla a ella misma.

Aquí vemos un **uso concreto de polimorfismo**: ¿qué objetos intervienen? Cualquiera a los que le pueda enviar el mensaje llevaA(unPasajero).

> Y recalcamos que el polimorfismo es para el que usa los objetos polimórficos, no para Daniel o Alejandro.

```xtend
>>> norma.elegirChofer([daniel, alejandro, luciana])
alejandro[]
```

(para poder hacer esto hay que correr el REPL desde el archivo pasajeros.wlk pero debe tener el import de los taxistas, guiño guiño o no van a poder utilizar esos wko)

¿Qué pasa si ponemos en la colección un objeto que no entiende el mensaje llevaA(unPasajero)?

```xtend
>>> norma.elegirChofer([daniel, alejandro, susana, luciana])
wollok.lang.MessageNotUnderstoodException: susana[] does not understand llevaA(p0)
   at (/home/fernando/workspace/wollok-2017/taxistasStubs/src/pasajeros.wlk:1)
```

Ah, se rompe.
Claro, tiene sentido, ¿no?

### Beto elige al que cobra más caro

Mmm... ¿cómo sabemos quién cobra más caro?

Tenemos que agregar esa información en cada taxista, esto implica una tarea de análisis. Luego del relevamiento sabemos cuánto cobra cada taxista:

- Daniel cobra 50 $ cada viaje
- Alejandro cobra 15 $ a los jóvenes ó $ 20 en caso contrario
- Luciana cobra 100 $ - la edad del pasajero cada viaje

Agregamos ese comportamiento

```scala
object daniel {
	...
	method valorViaje(pasajero) = 50
}

object alejandro {
	...
	method valorViaje(pasajero) = if (pasajero.esJoven()) 15 else 20
}

object luciana {
	...
	method valorViaje(pasajero) = 100 - pasajero.edad()		
}
```

Ahora sí, Beto tiene que elegir el taxista que le cobra más, por suerte tenemos un mensaje max():

```scala
object beto {
	method elegirChofer(choferes) = choferes.max { chofer => chofer.valorViaje(self) }
}
```

Lo evaluamos y...

```xtend
>>> beto.elegirChofer([luciana, alejandro, daniel])
wollok.lang.MessageNotUnderstoodException: beto[] does not understand edad()
```

¡Se rompe porque no definimos la edad de Beto!
Beto tiene 42 años y dice que nadie es joven.

Agregamos estas definiciones:

```scala
object beto {
	
	method edad() = 42
	method esJoven() = false
	method elegirChofer(choferes) = choferes.max { chofer => chofer.valorViaje(self) }
	
}
```

Y ya podemos evaluar

```xtend
>>> beto.elegirChofer([luciana, alejandro, daniel])
luciana[edad=37]
```

## El diagrama de secuencia: otra vista para entender un requerimiento

Podemos mostrar el flujo de envío de mensajes en un diagrama de secuencia que cuenta:

- qué objeto dispara la pregunta (salvo en el mensaje inicial que sale del REPL)
- qué objeto responde
- y a qué objetos le pide ayuda cada objeto para resolver cada requerimiento
- no estamos escribiendo todos los parámetros porque en este caso no hay métodos con diferente aridad, y mantener sincronizado el mensaje con sus argumentos es más costoso (y muestra en cierta forma una duplicidad)
- como resultado, vemos el ciclo de vida o *timeline* de los objetos

![diagrama_secuencia](http://www.plantuml.com/plantuml/img/TSyn2W8n40NGVawHKWjX4GIniB7p03PMuyvNiuG4ncpElt5nao3QJjwVFxbkZkQ66Cp1mE5XzVGCT-XgJSHzj8aZvdUQKSR2ti9bdjW5lLWUaB4YZkhDhgC2sM5WBng-RLrS25NStFCwXmHVjsyx4lm8bqetzfOz_o_y05fWKoOipJ_sRTEIQcVls0G0)

Si el sistema tiene responsabilidades bien definidas para cada objeto, no hay un solo objeto que hace las preguntas sino que los que preguntan y los que responden van cambiando de rol, eso es lo que vemos en este diagrama.

Claramente es la que más le cobra:

- Daniel le cobra 50
- Alejandro le cobra 20 porque no es joven
- Luciana le cobra 100 - 42 (la edad) = 58

# Después del recreo - Ejercicio práctico (1 hora 10')

Daniel nos encargó una serie de requerimientos que debemos incorporar:

## Registrar viajes

Queremos registrar todos los viajes que hizo, donde sepa

- a qué localidad fue
- los kilómetros recorridos
- las personas que viajaron

```scala
object daniel {
	method agregarViaje(viaje) {
		viajes.add(viaje)
	}
}
```

## Localidades visitadas

Queremos saber las localidades visitadas por Daniel, en base a los viajes que hizo. No debe haber repetidos.

```xtend
method localidadesVisitadas() =	viajes.map { viaje => viaje.localidad() }.asSet()
```

## Kilómetros recorridos

Queremos saber el total de kilómetros recorridos por Daniel en base a sus viajes.

```xtend
method kilometrosRecorridos() =	viajes.sum { viaje => viaje.kilometros() }

method kilometrosRecorridos2() = viajes.fold(0, { acum, viaje => acum + viaje.kilometros() })
```

## Saber si llevó a una persona

Queremos saber si Daniel llevó a un pasajero.

```xtend
method llevo(pasajero) = viajes.any { viaje => viaje.pasajeros().contains(pasajero) }
```

## Saber qué viajes largos hizo

Queremos saber qué viajes largos hizo Daniel. Un viaje largo es aquel de 20 ó más kilómetros.

```ruby
method llevo(pasajero) = viajes.filter { viaje => viaje.esLargo() }
```

Nótese cómo delegamos en viaje la responsabilidad. Eso... lo dejamos para más adelante, porque ahí es donde tener varios objetos viaje no termina de cerrar, porque todos parecen comportarse igual.

