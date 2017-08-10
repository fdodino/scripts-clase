# Clase 2 Objetos

por Fernando Dodino - Agosto 2017
Distribuido con licencia [Creative commons Share-a-like](https://creativecommons.org/licenses/by-sa/4.0/legalcode)

## Repaso

- objeto
- mensaje vs. método
- ambiente
- interfaz vs. implementación

## Ejercicio - Viajes en taxi:

Conceptos a ver

- Polimorfismo
- Referencias
- Intro a testing

Presentamos a los choferes y la forma en que deciden subir o no un pasajero

- Daniel lleva a todos los que no sean jóvenes (cada pasajero se autodefine como joven o no-joven)
- Alejandro lleva a todos
- Luciana tiene 37, y lleva a un pasajero si tiene una diferencia de menos de 5 años con ese pasajero

## Cómo lo implementamos

```scala
object daniel {
	method llevaA(pasajero) = !pasajero.esJoven()
}

object alejandro {
	method llevaA(pasajero) = true
}

object luciana {
        var edad = 37
	
	method llevaA(pasajero) = 
		self.diferenciaDeEdadCon(pasajero) < 5

	method diferenciaDeEdadCon(pasajero) = 
		(pasajero.edad() - edad).abs()
}
```

El = en los métodos refuerza los métodos que son preguntas (llevan el =) vs. los que son acciones (que pueden tener cuerpo entre llaves, ojo que hay métodos de más de una línea que son preguntas pero necesitan las llaves y el return)

¿Hay polimorfismo?
Sí, los tres objetos en algún contexto son polimórficos, pero si un árbol se cae y nadie lo oye... ¿verdaderamente es polimórfico? O algo así...

¿Qué sabemos de pasajero?

- sabe decirnos si es joven o no, un booleano
- sabe decirnos su edad, un número

Vamos a meterle algo más de picante.

## Pasajeros

- Susana no tiene edad (es 0) pero siempre es joven
- Adriel tiene 35 y él considera que los jóvenes son los menores de 40
- Juana tiene 21 y considera que ya es vieja, porque a partir de los 20 chau... (¿chau qué? Chau, estoy saludando a un señor que se va, diría Dolina)

Los implementamos:

```javascript
object susana {
	method edad() = 0
	method esJoven() = true
}

object adriel {
    var edad = 35
	method edad() = edad
	method esJoven() = edad < 40
}

object juana {
	var edad = 21
	method edad() = edad
	method esJoven() = edad < 20
}
```

¿No se repite código?
Ah, claro, qué descuidado que fui.
Lo anotamos en nuestra máquina de escribir invisible.

## Cómo lo probamos

- En el REPL. Un embole porque si al probar encontramos errores, tenemos que volver a probar nuevamente, volviendo a dejar exactamente igual el estado de los objetos. Aquí vemos una ventaja de los paradigmas que delimitan el alcance del efecto colateral. 

Tampoco estamos seguros de probar todas las variantes.

- Creamos un programa. Existen en Wollok, pero para ver que funciona ok tengo que escribir muchos println, requiere esfuerzo de programación pero cada vez que corremos el programa tenemos que ver la salida por la consola.

- Vamos a crear tests unitarios, de la misma manera que lo vinimos haciendo anteriormente.

Sobre el proyecto creamos un archivo "taxis.wtest".
¿Qué vamos a probar?

- Que Daniel no lleva a Susana (porque es joven) ni a Adriel, pero sí a Juana (porque se cree vieja).
- Que Alejandro lleva a Susana, Adriel y Juana (lleva a todos).
- Que Luciana lleva a Adriel (tiene diferencia menor a 5 años) pero no a Susana ni a Juana.

¿Cómo lo podemos probar?
Vamos a arrancar con algo simple. El primer test dice que Daniel lleva a Susana. Para eso, si tenemos dos archivos wlk
- taxistas.wlk
- pasajeros.wlk
Deben relacionarse con los tests a través del mecanismo de imports:

```scala
import taxistas.*
import pasajeros.*
```

Podemos hacerlo individualmente

```javascript
import taxistas.daniel
import taxistas.alejandro
...
```

Pero es más rápido con la versión que tiene asteriscos. Si encuentran algún problema de performance nos avisan, internamente hacen cosas distintas.

Ahora sí, escribimos el primer test, dentro de un contenedor que se llama describe y que necesita una descripción de la agrupación de tests que vamos a crear:

```javascript

describe "Tests de taxistas" {
	
	test "Saber si Daniel lleva a Susana" {
		assert.notThat(daniel.llevaA(susana))
	}

}
```

Fíjense que como daniel y susana son wko, no necesitamos hacer nada más que usarlos en el contexto de un test. En este caso global es bueno. Por otra parte estuvo bueno no hacer en daniel:

```scala
object daniel {
	method llevaA() = !susana.esJoven()
}
```

Porque si bien eso compila, no permite preguntar si Daniel lleva a otro pasajero. En ese caso global es **malo**.

Como consejo

> Hay que evitar tener referencias a wko dentro de otro wko

Escapan de esta situación los programas y los tests.

Volvemos sobre el test (siempre dentro del *describe*)

```javascript
test "Saber si Daniel lleva a Susana" {
	assert.notThat(daniel.llevaA(susana))
}
```

1. El test tiene una descripción, encerrada entre comillas para poder ser lo más expresivo posible. Yo por lo general prefiero no escribir "Daniel lleva a Susana", porque si con nuevas definiciones de negocio Daniel deja de llevar a Susana, son dos lugares donde hay que cambiar. Hay una duplicación menos evidente, pero duplicación al fin. Entonces yo escribo qué es lo que estamos probando, sin entrar en detalles en cómo (de hecho el test no debería saber cómo se implementa, simplemente debe DELEGAR a los objetos)

2. El wko assert forma parte de lo que trae Wollok, y permite evitar esto:

```javascript
test "Saber si Daniel lleva a Susana" {
	if (!daniel.llevaA(susana)) {
		console.println("Todo bien!")
	} else {
		console.println("Todo mal!")
	}
}
```

Que de hecho no es lo que uno haría, sino algo más interesante como:

```javascript
test "Saber si Daniel lleva a Susana" {
	if (daniel.llevaA(susana)) {
		self.error("El test 'Saber si Daniel lleva a Susana' falló")
	}
}
```

El objeto assert permite que luego de correr los tests aparezca un semáforo 

- rojo: hubo un error al llamar a daniel.llevaA(susana), puede ser una división por cero, un mensaje que algún objeto no entienda, etc.
- amarillo: hubo una diferencia entre lo que yo esperaba que pasara y lo que finalmente ocurrió. Por ejemplo, si Daniel llevara a Susana, eso implica que lo que yo anticipo no se cumple. El test entonces falla (failure), algo que no es bueno pero es menos grave que un error.
- verde: funcionó el caso de prueba que yo quería.

## Evitando la repetición de código

Recordemos estas definiciones

```javascript
object adriel {
    var edad = 35
	method edad() = edad
	method esJoven() = edad < 40
}

object juana {
	var edad = 21
	method edad() = edad
	method esJoven() = edad < 20
}
```

Si yo solo quiero usar a adriel y a juana en el contexto de los tests, por ejemplo para probar que Daniel lleva a Juana:

```javascript
test "Saber si Daniel lleva a Juana" {
	assert.that(daniel.llevaA(juana))	
}
```

Podemos evitar la creación del wko Juana y reemplazarlo por un objeto anónimo. Al igual que en las funciones anónimas de Haskell, los objetos anónimos no pueden ser utilizados en otro contexto (ya que no tienen nombre), aunque sí tenemos la posibilidad de guardarla en una referencia, pasarla como parámetro, etc.

```javascript
test "Saber si Daniel lleva a Juana" {
    // Borramos 
	const juana = object {
		method edad() = 21
		method esJoven() = false
	}
	assert.that(daniel.llevaA(juana))	
}
```

Claro, ¡estamos sobresimplificando! No parece que esté bueno eso, porque ahora creamos una abstracción que perdió las reglas de negocio. Ojo con eso en el TP, pero sobre todo ojo con eso en los trabajos...

La segunda variante es embeber la definición de Juana en el test:

```javascript
test "Saber si Daniel lleva a Juana" {
    // Borramos 
	const juana = object {
		var edad = 21
		method edad() = edad
		method esJoven() = edad < 20
	}
	assert.that(daniel.llevaA(juana))	
}
```

Y fíjense que hay polimorfismo entre wko y objetos anónimos, mientras compartan una interfaz común. El problema es que ahora así no puedo aprovechar la definición de Juana en el siguiente test:

```javascript
test "Saber si Alejandro lleva a Juana" {
    // Borramos el wko juana y creamos a Juana acá
	const juana = object {
		var edad = 21
		method edad() = edad
		method esJoven() = edad < 20
	}
	assert.that(alejandro.llevaA(juana))	
}
```

¡Malísimo! No queremos eso. Lo que nos vendría bien es pensar una abstracción que cree un objeto anónimo... bueno, eso ya existe, se llama método. En el archivo de test hacemos

```javascript
import pasajeros.*
import taxistas.*


method crearJuana() = 
	object {
		var edad = 21
		method edad() = edad
		method esJoven() = edad < 20
	}

test "Saber si Daniel lleva a Juana" {
    const juana = self.crearJuana()
	assert.that(daniel.llevaA(juana))	
}

test "Saber si Alejandro lleva a Juana" {
	const juana = self.crearJuana()
	assert.that(alejandro.llevaA(juana))	
}
```

Pero hay algo mejor, ¿por qué no pasamos como parámetro la edad que tiene y la edad que considera la persona para ser vieja?
Eso nos permite crear cualquier tipo de pasajero, por ejemplo

- a Juana
- y a Adriel

```javascript
import pasajeros.*
import taxistas.*

describe "Tests de taxistas" {

	// primero van los métodos
	method crearPasajero(edadQueTiene, edadParaSerViejo) = 
		object {
			method edad() = edadQueTiene
			method esJoven() = self.edad() < edadParaSerViejo
		}

	method crearAdriel() = self.crearPasajero(35, 40)
	method crearJuana() = self.crearPasajero(21, 20)

	// luego los tests
	test "Saber si Daniel lleva a Juana" {
	    const juana = self.crearJuana()
		assert.that(daniel.llevaA(juana))	
	}

	test "Saber si Alejandro lleva a Juana" {
	    const juana = self.crearJuana()
		assert.that(alejandro.llevaA(juana))	
	}

	test "Saber si Alejandro lleva a Adriel" {
	    const adriel = self.crearAdriel()
		assert.that(alejandro.llevaA(adriel))	
	}

}
```

Fíjense que para preguntar si un pasajero es joven

- antes usábamos la referencia edad (lo que se llama *acceso directo*)
- ahora usamos el accessor edad() (lo que se llama *acceso indirecto*)

## BONUS: Testeo con colecciones

Podemos probar quiénes llevan a un pasajero para un viaje, pensando en un conjunto de choferes.

Un conjunto se define

```javascript
const conjunto = #{2, 7, 4}
const choferes = #{alejandro, daniel, luciana}
```

¿Por qué conjunto y no lista? Porque no me interesa tenerlos ordenados.

Entonces nuestro test podría ser que a Juana la llevan Daniel y Alejandro, pero no Luciana.

Entonces, si tuviéramos un conjunto de choferes, y filramos aquellos que llevan a Juana, nos quedaría el conjunto #{daniel, alejandro} (o Alejandro y Daniel, la comparación será indistinta).

Aquí necesitaremos:

- crear a Juana en alguna de las variantes que hemos visto
- definir el conjunto de choferes
- enviar el mensaje filter al conjunto, para saber qué choferes llevan a Juana
- utilizar el mensaje equals() para el wko assert

```javascript
import pasajeros.*
import taxistas.*

describe "Tests de taxistas" {

	// primero van los métodos
	method crearPasajero(edadQueTiene, edadParaSerViejo) = 
		object {
			method edad() = edadQueTiene
			method esJoven() = self.edad() < edadParaSerViejo
		}

	method crearAdriel() = self.crearPasajero(35, 40)
	method crearJuana() = self.crearPasajero(21, 20)

	// luego los tests
	test "Saber qué choferes llevan a Juana" {
	    const juana = self.crearJuana()
	    const choferes = #{daniel, alejandro, luciana}
	    const choferesQueLlevanAJuana = choferes.filter { chofer => chofer.llevaA(juana) }
		assert.equals(#{daniel, alejandro}, choferesQueLlevanAJuana)
	}
}
```

Fíjense cómo estamos enviando el mismo mensaje a cada uno de los choferes. Cada uno responde con su comportamiento específico, de eso se trata el **polimorfismo**. No significa que sean iguales, de hecho no lo son, y ciertamente pueden tener interfaces diferentes (uno puede tener más métodos que otro por ejemplo). Pero en el contexto del filter, yo puedo enviar mensajes sin importarme si hablo cn Daniel, con Alejandro o con Luciana.


¿Y si quiero reutilizar la lista de choferes?

- Definen un método y lo usan en cada test
- Usan un *describe* con *fixture*, que veremos más adelante.

