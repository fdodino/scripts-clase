
# Sueños

El enunciado es [éste](https://docs.google.com/document/d/1vtru1ysNDA5N3eVt33hD9IexzrL7rnQbEofYYrlp7yE/edit?usp=sharing)

## Punto 1 : Cumplir 

> "Hacer que una persona cumpla un sueño, lo cual implica validaciones previas"

Decisiones que hay que tomar

- ¿el sueño es una abstracción? Sí, porque en algún momento soñamos con lograr algo y eventualmente ese sueño puede cumplirse o no, o pasárselo a nuestros hijos (el 

famoso mandato)
- ¿el sueño es WKO o clase? Para recibirse de una carrera en particular, necesitamos referencias a la carrera. Entonces necesitamos que sean instancias de **clases**
- Viajar a Chapadmalal o a Tahití, ¿en qué se diferencian? Solo en el lugar, entonces podemos parametrizar eso.
- Lo mismo al conseguir un laburo de x plata
- ojo con mezclar información del sueño y de la persona. La persona quiere recibirse de Abogado, y si no se recibió no estudió abogacía.
- ¿cómo manejar las validaciones? ¿están asociadas al sueño? Así parece, entonces no tiene sentido buscar una solución general, ya sea con condicionales (ifs) o bien 

con una colección de strategies, porque cada sueño sabe su propia validación

```javascript
class Persona {
    const sueniosCumplidos = []
	const sueniosPendientes = []
	
	method cumplir(suenio) {
		suenio.cumplir(self)
		sueniosPendientes.remove(suenio)
		sueniosCumplidos.add(suenio)
	}
```

Para modelar los sueños cumplidos, podríamos tener un flag booleano en Sueño, eso es tan válido como tener dos colecciones con los estados diferentes.

## Validaciones generales del sueño

Para cumplir un sueño, vamos a incorporar que el sueño debe estar pendiente para la persona, tendremos una clase Sueño para poner las validaciones generales:

```javascript
class Suenio {
	method cumplir(persona) {
		if (!persona.tieneSuenioPendiente(self)) {
			error.throwExceptionWithMessage("El sueño " + self + " no está pendiente para " + persona)
		}
		...
	}
}
```

Lo que sigue depende de cada sueño, por lo que podemos aplicar un **Template method**:

```javascript
class Suenio {
	method cumplir(persona) {
		if (!persona.tieneSuenioPendiente(self)) {
			error.throwExceptionWithMessage("El sueño " + self + " no está pendiente para " + persona)
		}
		self.doCumplir(persona)
	}
}
```

## Adopción 

Tomemos como ejemplo Adoptar un hijo, que es un sueño y por lo tanto debemos abstraerlo como subclase de sueño. ¿Qué hace el doCumplir?

```javascript
class AdoptarHijo inherits Suenio {
	const hijosAAdoptar
	constructor(_hijosAAdoptar) { hijosAAdoptar = _hijosAAdoptar }
	method doCumplir(persona) {
		if (persona.tieneHijos()) {
			error.throwExceptionWithMessage("No se puede adoptar si se tiene un hijo")
		}
		persona.agregarHijos(hijosAAdoptar)
	}
}
```

Definimos al sueño como inmutable.

Los métodos que falta definir en Persona son fáciles:

```javascript
class Persona {
	var cantidadHijos = 0
	...
	method tieneHijos() = cantidadHijos > 0
	method agregarHijo(cantidad) {
		cantidadHijos += cantidad
	}
}
```

## Viajar a un lugar

Nuevamente podemos tener un Viaje, que es subclase de Sueño y que tiene como referencia inmutable el lugar adonde queremos ir. No tiene validaciones, solo debe 

incorporar a la lista de lugares visitados pro la persona el lugar adonde quería ir.

```javascript
class Viajar {
	const lugar
	... constructor ...
	method doCumplir(persona) {
		persona.viajarA(lugar)
	}
}
```

El método viajarA() en Persona es también fácil:

```javascript
class Persona {
	const lugaresVisitados = []
	...
	method viajarA(lugar) {
		lugaresVisitados.add(lugar)
	}
}
```

## Recibirse

Recibirse es otra subclase de Sueño, y ya se va haciendo mecánica la resolución.

```javascript
class Recibirse inherits Suenio {
	const carrera
	constructor(_carrera) { carrera = _carrera }
	method doCumplir(persona) {
		if (!persona.quiereEstudiar(carrera)) {
			error.throwExceptionWithMessage(persona.toString() + " no quiere estudiar " + carrera)
		}
		if (persona.completoCarrera(carrera)) {
			error.throwExceptionWithMessage(persona.toString() + " ya completó los estudios de " + carrera)
		}
		persona.completarCarrera(carrera)
	}
}
```

Más métodos en Persona fáciles:

```javascript
class Persona {
	const carrerasQueQuiereEstudiar = []
	const carrerasCompletadas = []
	method quiereEstudiar(carrera) = carrerasQueQuiereEstudiar.contains(carrera)
	method completoCarrera(carrera) = carrerasCompletadas.contains(carrera)
	method completarCarrera(carrera) {
		carrerasCompletadas.add(carrera)
	}
}
```

¿Y los setters y getters de los atributos de persona? No se escriben.
En el parcial **no se escriben**.

## ¡Los felicidonios!

Casi nos olvidamos, "cada sueño brinda a la persona que lo cumple un nivel de felicidad o felicidonios". ¿Dónde ubicamos esta responsabilidad?

- En la clase sueño debemos decirle a la persona que sume felicidad...
- ... que sale de una referencia que todos los sueños deben tener


```javascript
class Suenio {
	var felicidonios = 0
	method cumplir(persona) {
		if (!persona.tieneSuenioPendiente(self)) {
			error.throwExceptionWithMessage("El sueño " + self + " no está pendiente para " + persona)
		}
		self.doCumplir(persona)
		persona.aumentarFelicidad(felicidonios)
	}
}
```

Si pasamos las validaciones, aumentamos la felicidad de la persona.
Hay algo que se nos rompió: sí , efectivamente, los constructores de cada subclase. No importa, es un detalle.
La codificación de aumentarFelicidad() se las dejamos a ustedes.

## De yapa... modelaron un patrón

La idea de tener un objeto que representa una acción y que puede realizarse (o pasarse como parámetro para que otro la realice) es un patrón de diseño que se llama 

**Command** y que pueden estudiar aparte.

# Punto 2 : Sueño múltiple

El sueño múltiple implica poder tener un conjunto de sueños, de manera que ahora aparece

- el sueño común o simple
- el sueño agrupado o compuesto

Ah, claro, finalmente tenemos una jerarquía de ramas y hojas polimórficas, donde las ramas son sueños múltiples y las hojas los sueños simples, lo que pueden haber 

visto que es un **Composite pattern**.

¿Qué es lo más improtante? Mantener la misma interfaz entre sueño compuesto y simple. Es decir, buscamos el _polimorfismo_


```javascript
class SuenioMultiple inherits Suenio {
	const suenios = []
	method cumplir(persona) {
		suenios.forEach [ suenio => suenio.cumplir(persona) ]
	}
}
```

Claro, el problema es que "si alguno de los sueños no se puede cumplir debería tirar error". Esto nos obliga a cambiar ligeramente la implementación que teníamos:

```javascript
class Suenio {
	method cumplir(persona) {
		self.validar(persona)
		self.doCumplir(persona)
		persona.aumentarFelicidad(self.felicidonios()) // lo reemplazamos por un mensaje que puede redefinir Sueño múltiple
	}
	method validar(persona) {
		if (!persona.tieneSuenioPendiente(self)) {
			error.throwExceptionWithMessage("El sueño " + self + " no está pendiente para " + persona)
		}
		self.doValidar(persona)
	}
	method felicidonios() = felicidonios
}

class AdoptarHijo inherits Suenio {
	const hijosAAdoptar
	constructor(_hijosAAdoptar) { hijosAAdoptar = _hijosAAdoptar }
	method doCumplir(persona) {
		persona.agregarHijos(hijosAAdoptar)
	}
	method doValidar(persona) {
		if (persona.tieneHijos()) {
			error.throwExceptionWithMessage("No se puede adoptar si se tiene un hijo")
		}
	}
}

```

etc.


Entonces con eso ya podemos corregir el tema de la validación en Sueño múltiple:

```javascript
class SuenioMultiple inherits Suenio {
	const suenios = []
	method cumplir(persona) {
		suenios.forEach [ suenio => suenio.validar(persona) ]
		suenios.forEach [ suenio => suenio.doCumplir(persona) ]
	}
	method felicidonios() = suenios.sum { suenio => suenio.felicidonios() }
}
```

De esta manera, si alguno de los sueños no se puede cumplir estamos impidiendo que se cumplan todos los que forman el sueño múltiple.

#  Punto 3 y el 6 : Tipos de personas

Los tipos de personas pueden ser

- realistas
- alocados
- obsesivos

La clave es cuando dice "pueden aparecer a futuro nuevos tipos de persona".  Y el punto 6 nos pide que podamos cambiar los tipos de persona, entonces queremos que no 

esté atado a la jerarquía de Persona, sino tener dos jerarquías por separado: las personas y los tipos de personalidad. La composición nos permite que aparezca el 

polimorfismo y por ende la posibilidad de intercambiarlos (y termina resolviéndose mediante un **Strategy pattern**).

Comenzamos resolviendo el requerimiento principal: "Cumplir el sueño más preciado para una persona".

```javascript
class Persona {
	method cumplirSuenioElegido() {
		const suenioElegido = tipoPersona.elegirSuenio(sueniosPendientes)
		self.cumplir(suenioElegido)
	}
}

object realista { // strategy stateless
	method elegirSuenio(sueniosPendientes) {
		sueniosPendientes.max { suenio => suenio.felicidonios() }
	}
}

object alocado { // strategy stateless
	method elegirSuenio(sueniosPendientes) {
		sueniosPendientes.anyOne()
	}
}

object obsesivo { // strategy stateless
	method elegirSuenio(sueniosPendientes) {
		sueniosPendientes.first()
	}
}
```

Como los strategies no necesitan estado (referencias), son stateless y se pueden implementar con _well-known objects_.

# Punto 4

Para saber si una persona es feliz, le preguntamos a la persona... este punto es tranquilo una vez que encaramos los primeros puntos.

```javascript
class Persona {
	method esFeliz() = felicidonios > sueniosPendientes.sum { suenio => suenio.felicidonios() } // sirve así para sueños simples o múltiples
}
```

# Punto 5

Para determinar si una persona ambiciosa, hay que delegar...

```javascript
class Persona {
	method esAmbiciosa() = self.suenios().filter { suenio => suenio.esAmbicioso() } > 3
	method suenios() = sueniosPendientes().union(sueniosCumplidos)
}

class Suenio {
	method esAmbicioso() = self.felicidonios() > 100  // sirve así para sueños simples o múltiples
}
```

# Diagrama de clases final
