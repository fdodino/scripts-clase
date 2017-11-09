
# Sueños

El enunciado es [éste](https://docs.google.com/document/d/1vtru1ysNDA5N3eVt33hD9IexzrL7rnQbEofYYrlp7yE/edit?usp=sharing)

## Punto 1 : Cumplir 

> "Hacer que una persona cumpla un sueño, lo cual implica validaciones previas"

Decisiones que hay que tomar

- ¿el sueño es una abstracción? Sí, porque en algún momento soñamos con lograr algo y eventualmente ese sueño podemos cumplirlo o tenerlo pendiente
- ¿el sueño es WKO o clase? Para recibirse de una carrera en particular, necesitamos referenciar a la carrera. Entonces cada sueño se modela como una instancia de **la clase Sueño**
- Viajar a Chapadmalal o a Tahití, ¿en qué se diferencian? Solo en el lugar, entonces podemos parametrizar eso (con una referencia o variable de instancia).
- Lo mismo al conseguir un laburo de _x_ plata
- ojo con mezclar información del sueño y de la persona. Una cosa es querer adoptar un hijo, otro es cuántos hijos tiene. Lo mismo con los felicidonios: las personas tienen felicidonios, los sueños saben cuántos felicidonios proporcionan a la persona cuando los cumple.
- ¿cómo manejar las validaciones? Algunas son generales (el sueño debe estar pendiente para esa persona), otras están asociadas al sueño. 

```javascript
class Persona {
	const suenios = []
	
	method cumplir(suenio) {
		if (!self.sueniosPendientes().contains(suenio)) {
			error.throwExceptionWithMessage("El sueño " + suenio + " no está pendiente")
		}
		suenio.cumplir(self)
	}
	
	method sueniosPendientes() = suenios.filter { suenio => suenio.estaPendiente() }
}

class Suenio {
	var cumplido = false
	method estaPendiente() = !cumplido
}
```

Para modelar los sueños cumplidos tendremos un flag booleano en Sueño. Otra opción podría ser tener dos colecciones con los estados diferentes, algo totalmente válido.

## Cumplir un sueño

Para cumplir un sueño, ya pasamos las validaciones generales, tenemos entonces tres pasos:

- validar
- realizar el sueño
- generar el efecto sobre el sueño (cumplido) y la persona (felicidonios)

Lo que sigue depende de cada sueño, por lo que podemos aplicar un **Template method**:

```javascript
class Suenio {
	var felicidonios = 0
	
	method cumplir(persona) {
		self.validar(persona)
		self.realizar(persona)
		self.cumplir()
		persona.sumarFelicidad(felicidonios)
	}
	
	method cumplir() { cumplido = true }
}
```

Los métodos validar y realizar son abstractos, aunque validar podría también ser un método vacío, para cuando algún sueño no tenga validaciones (es más difícil justificar que el método relizar no tenga comportamiento, entonces qué sentido tendría modelar una subclase de Sueño).

## Adopción 

Tomemos como ejemplo _adoptar un hijo_, que es un sueño y por lo tanto debemos abstraerlo como subclase de sueño. ¿Cómo funciona la validación y el cumplimiento?

```javascript
class AdoptarHijo inherits Suenio {
	const hijosAAdoptar // const si los sueños son inmutables, o var si queremos que se modifique
	
	method validar(persona) {
		if (persona.tieneHijos()) {
			error.throwExceptionWithMessage("No se puede adoptar si se tiene un hijo")
		}
	}
	
	method realizar(persona) {
		persona.agregarHijos(hijosAAdoptar)
	}
}
```

Los métodos que falta definir en Persona son fáciles:

```javascript
class Persona {
	var cantidadHijos = 0
	...
	
	method tieneHijos() = cantidadHijos > 0
	
	method agregarHijos(cantidad) {
		cantidadHijos += cantidad
	}
}
```

## Viajar a un lugar

Viajar a un lugar es una subclase de Sueño y sabe el lugar adonde queremos ir (en nuestro caso lo definimos constante porque queremos modelar un sueño inmutable). No tiene validaciones, solo debe incorporar a la lista de lugares visitados por la persona el lugar adonde quería ir.

```javascript
class Viajar inherits Suenio {
	const lugar
	
	method validar(persona) {
		// Sin comportamiento
	}
	
	method realizar(persona) {
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
	const carrera  // o var, como dijimos antes si queremos inmutabilidad/mutabilidad
	
	method validar(persona) {
		if (!persona.quiereEstudiar(carrera)) {
			error.throwExceptionWithMessage(persona.toString() + " no quiere estudiar " + carrera)
		}
		if (persona.completoCarrera(carrera)) {
			error.throwExceptionWithMessage(persona.toString() + " ya completó los estudios de " + carrera)
		}
	}
	
	method realizar(persona) {
		persona.completarCarrera(carrera)
	}
}
```

Más métodos fáciles y aburridos definidos en Persona:

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

## De yapa... modelaron un patrón

La idea de tener un objeto que representa una acción y que puede realizarse o pasarse como parámetro para que otro la realice es un patrón de diseño que se llama **Command** y que pueden estudiar aparte.

# Punto 2 : Sueño múltiple

El sueño múltiple implica poder tener un conjunto de sueños, de manera que ahora aparece

- el sueño común o simple
- el sueño agrupado o compuesto

Ah, claro, finalmente tenemos una jerarquía de ramas y hojas polimórficas, donde las ramas son sueños múltiples y las hojas los sueños simples, lo que pueden haber visto que es un **Composite pattern**.

¿Qué es lo más importante? Mantener la misma interfaz entre sueño compuesto y simple. Es decir, buscamos el _polimorfismo_. El único tema es que Suenio tiene una referencia _felicidonios_ que el sueño múltiple no tiene. Entonces generamos una jerarquía nueva:

- Sueño 
 - Sueño simple
 - Sueño compuesto

En Sueño eliminamos la referencia a felicidonios y utilizamos un método felicidonios que ahora es abstracto.

```javascript
class Suenio {
	method cumplir(persona) {
		self.validar(persona)
		self.realizar(persona)
		self.cumplir()
		persona.sumarFelicidad(self.felicidonios())
	}
	
	method felicidonios()
}


class SuenioSimple {
	var felicidonios = 0
	method felicidonios() = felicidonios
}

class SuenioMultiple inherits Suenio {
	const suenios = []
	
	method felicidonios() = suenios.sum { suenio => suenio.felicidonios() } 
	// los sueños múltiples deben sumar los felicidonios de sus sueños

	method validar(persona) {
		suenios.forEach { suenio => suenio.validar(persona) }
	}
	
	method realizar(persona) {
		suenios.forEach { suenio => suenio.realizar(persona) }
	}	
}
```

De esta manera, si alguno de los sueños no se puede cumplir estamos impidiendo que se cumplan todos los que forman el sueño múltiple.

#  Punto 3 y 6 : Tipos de personas

Los tipos de personas pueden ser

- realistas
- alocados
- obsesivos

Al decir "pueden aparecer a futuro nuevos tipos de persona" eso implica que una solución con ifs (condicionales) no solo trae como consecuencia una baja cohesión en Persona, sino la seguridad de que ese método se puede tornar inmantenible. Y el punto 6 nos pide que podamos cambiar los tipos de persona, entonces queremos que no esté atado a la jerarquía de Persona, sino tener dos jerarquías por separado: las personas y los tipos de personalidad. La composición nos permite que aparezca el polimorfismo y por ende la posibilidad de intercambiarlos, así termina apareciendo el **Strategy pattern**.

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

Para saber si una persona es feliz, le preguntamos a la persona... 

```javascript
class Persona {
	method esFeliz() = felicidonios > sueniosPendientes.sum { suenio => suenio.felicidonios() } 
	// sirve así para sueños simples o múltiples
}
```

# Punto 5

Para determinar si una persona ambiciosa, hay que delegar...

```javascript
class Persona {
	method esAmbiciosa() = self.sueniosAmbiciosos().size() > 3
	method sueniosAmbiciosos() = suenios.filter { suenio => suenio.esAmbicioso() }
}

class Suenio {
	method esAmbicioso() = self.felicidonios() > 100  // sirve así para sueños simples o múltiples
}
```

# Diagrama de clases final

![image](http://www.plantuml.com/plantuml/img/fP91JiCm44NtEOKLDYsY2zWXLP6wICK65RM8_SIUegaSExATWeeu56VWOZYLIsgtHKZ9plxwxtzdXakmbZsXiLe1-yXOz_6sRYIPqJWwhbfKf4k9hoJqGKCxHqcx_ZYhA-AaBVYhpopZUxwtVd_fW8_fqobKrXdWhYZO5YG9E2iPzDhklBRs5RWlvUlPqzZ7LZfg939yTwX4qYQEJ5B-1g-eleWbvI9txrqYfugCJOs15aQGz8gQ_Q9U23PWXdDcGZSEp0Djz12OVpEQDN1apzJePAUmsvkmGyVPomvX7VY9ij7YvcfUti_koh6VwZwwHkLimZj1XLT8pK7e_u8kSXQLHKjRFQHHTj0c1Skly_0t-rkXtrGdtUR7f2nPs4da_W40)
