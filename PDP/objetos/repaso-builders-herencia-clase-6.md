# Repaso Builders y Herencia

por Fernando Dodino - Septiembre 2017
Distribuido con licencia [Creative commons Share-a-like](https://creativecommons.org/licenses/by-sa/4.0/legalcode)

# Repaso Builder - Fer

La clase pasada vimos que el builder podía servir como un intermediario entre

- el *describe*, que necesita instanciar su fixture o juego de datos
- y un objeto de dominio que es complejo de inicializar

Vuelven las plastilinas para mostrar que el builder facilita la construcción y además hace de adaptador para utilizar una interfaz menos cómoda:

![image](http://cnx.org/resources/0b8560122a15756735c562d49fadeec9bdd04cdb/graphics1.png)

## Resumen de alternativas 

Pero ojo, el builder es el último escalón de una gama de variantes posibles para dejar un objeto correctamente inicializado:

### Referencias inicializadas y constructor default

Cuando el objeto es lo suficientemente simple, nos alcanza con tener variables inicializadas en su definición y dejar el constructor vacío

```xtend
class Ave {
	var energia = 0

	// y ya
}
```

### Constructor propio + objeto inmutable

Cuando el objeto tiene varias referencias y no tenemos valores iniciales buenos, podemos pensar en tener un constructor definido por nosotros que inicialice correctamente dicho objeto (como en la clase Punto y en este ejemplo):

```xtend
class Persona {
	const nombre
	const apellido

	constructor(_nombre, _apellido) {
		nombre = _nombre
		apellido = _apellido
	}

	// Solo el getter , sin el setter
	method nombre() = nombre

}
```

Aquí vemos que modelamos la Persona como objeto inmutable. 

### Constructor default + referencias variables

Otra opción sería dejar las referencias variables, no definir un constructor y forzar a que el fixture haga la inicialización:

```xtend
fixture {
	const lito = new Persona()
	lito.nombre("Jose")
	lito.apellido("Guerrero")
}
```

Esto tiene como contra que para inicializar 10 personas, hay 30 lineas de código repetitivas y en algún momento el objeto Persona queda inconsistente (tiene referencias nulas). ¿Cuál es el problema?

```xtend
fixture {
	const lito = new Persona()
	lito.nombre("Jose")
	lito.apellido().length()  // Crash boom bang!
```

Ah, ah, ah.

### Cambiar la interfaz de los accessors

Una tercera opción consiste en modificar al objeto, de manera de proveer formas más cómodas de instanciar. En el caso del viaje, podríamos pensar en algo como:

```xtend
class Viaje {
	var chofer
	const pasajeros = []

	method chofer(_chofer) {
		chofer = _chofer
		return self
	}

	method agregarPasajero(pasajero) {
		pasajeros.add(pasajero)
		return self
	}
}
```

Esto permite que en el fixture yo lo construya de esta manera:

```xtend
fixture {
	const viajeALugano = new Viaje()
		.chofer(daniel)
		.agregarPasajero(susana)
		.agregarPasajero(gerardo)
}
```

No es necesario definir un método build, porque el último mensaje devuelve la referencia al objeto viaje que estamos creando. Esta opción es menos burocrática (necesita un objeto menos), lo cual puede ser mejor o peor... ¿por qué? Hagamos aparecer al

### El Builder conocido

Builder, como un objeto que está separado del objeto original que estamos creando. Una desventaja del builder es que me obliga a repetir la estructura o bien a tomar responsabilidades como el método agregarPasajero que puede ser mejor que las tenga el viaje. La ventaja es que puedo tener un objeto prototipo, una instancia previa, antes de hacer el build y construirlo efectivamente (esto en una pantalla es algo que puede ser bueno). Otra ventaja del builder es cuando tengo que instanciar un objeto sobre el cual no tengo el poder de modificarlo (porque aunque tenga el código es un objeto que pertenece a una clase de algún framework). Eso me da la libertad de darle valores default sin cambiar el objeto original, de ahí la ventaja de generar el adapter.

En resumen, el builder es una idea de Diseño que en algún momento les puede ser útil, pero no es la única opción para instanciar objetos.

# Resumen Herencia - Juan

La herencia es un mecanismo que nos ayuda a evitar la repetición de ideas. Por ejemplo:

- un auto es viejo si tiene más de 150.000 kms
- un auto clásico es viejo si tiene más de 150.000 kms. y si hace 2 años que no lo lustran

```xtend
class Auto {
	var kilometraje = 0

	method esViejo() = kilometraje > 150000
}
```

Para codificar si un auto clásico es viejo, no queremos volver a escribir "kilometraje mayor a 150000", entonces basamos la definición de AutoClasico en Auto:

```xtend
class AutoClasico inherits Auto {
	var fechaUltimaLustrada

	override method esViejo() = super() && (self.tiempoUltimaLustrada() > 2)

	method tiempoUltimaLustrada() = (new Date() - fechaUltimaLustrada).div(365)
}
```

## Self y super

- ¿Cuándo uso self? Siempre que quiero enviar un mensaje a mí mismo.
- ¿Cuándo uso super? Cuando no puedo usar self. Super no es una referencia, sino una orden de resolver el mensaje en el que estoy parado pero cambiando el **method lookup**: en lugar de comenzar por la clase en donde estoy parado comenzaré por la superclase.

En otros lenguajes puedo hacer esto:

```xtend
class AutoViejo inherits Auto {
	var fechaUltimaLustrada
	override method esViejo() = super.esViejo() && (...

```

En Wollok no, y el sentido es que yo no pueda libremente usar super para llamar a otro método:

```xtend
class AutoViejo inherits Auto {
	var fechaUltimaLustrada
	override method esViejo() = super.fechaUltimaLustrada() < ...
```

# Los WKO también pueden heredar de una clase

¿Qué pasa si tengo un peugeot404 que no entra en la categoría de auto ni de auto clásico? Tiene su propio comportamiento:

> El peugeot404 es viejo si tiene más de 150000 kms. y si su dueño tiene más de 50 años

Escribimos la definición del wko 

```xtend
object peugeot404 inherits Auto {
	var duenio
	override method esViejo() = super() && duenio.edad() > 50
}
```

Wow! Un objeto puede heredar de una clase, con lo cual

- tiene su propio juego de referencias (kilometraje)
- y comparte el mismo comportamiento

En un REPL pueden probar

```bash
>>> peugeot404
peugeot404[duenio=null, kilometraje=0]
```

¡Pero ojo! En el TP muchos definieron que joaquin, lucia y luisAlberto heredaban de Musico. Esto puede eventualmente ocurrir más adelante si las definiciones cambian, pero para la entrega 1 no había una necesidad: la herencia no es útil si solamente queremos compartir un atributo en común (ej. el grupo para joaquín y lucía, no hay comportamiento diferencial). **Es necesario que haya más cosas en común para justificar una relación de jerarquía**

# Antes de seguir... objetos con múltiples constructores - Fer

Una fecha de Wollok puede crearse de dos maneras:

- sin parámetros: con la fecha del día
- con parámetros: día, mes, año

Y este diseño es correcto. No obstante no siempre es útil definir muchos constructores para un objeto. Consideremos este caso:

```xtend
class Persona {
	const nombre
	const apellido

	constructor() {
		nombre = ""
		apellido = ""
	}

	constructor(_nombre) {
		nombre = _nombre
		apellido = ""
	}

	constructor(_nombre, _apellido) {
		nombre = _nombre
		apellido = _apellido
	}
}
```

Lo primero que vemos que nos molesta es la repetición de ideas: estamos diciendo cómo inicializar las referencias una y otra vez. Evitamos esa duplicación delegando dentro del constructor:

```xtend
class Persona {
	var nombre
	var apellido

	constructor() = self("", "")

	constructor(_nombre) = self(_nombre, "")

	constructor(_nombre, _apellido) {
		nombre = _nombre
		apellido = _apellido
	}
}
```

De todas maneras, **debemos considerar seriamente si esta alternativa no produce confusión en el que quiera instanciar una Persona**. ¿Cuál es el constructor por defecto que debería utilizar? ¿Por qué hay otros constructores? ¿Qué valor agregado tienen esos constructores?


# Herencia de constructores - Fer

Podemos modificar el constructor default, para que el auto ahora tenga un constructor con el kilometraje:

```xtend
class Auto {
	var kilometraje // no es necesario inicializarlo a un valor

	constructor(_kilometraje) {
		kilometraje = _kilometraje
	}

	method esViejo() = kilometraje > 150000
}
```

Ya no es posible construir un auto sin indicar el kilometraje

```bash
>>> new Auto()
ERROR: Wrong number of arguments. Should be new Auto(_kilometraje) (line: 1)
>>> const fierrito = new Auto(3000)
a Auto[kilometraje=3000]
```

¿Qué pasa con el AutoClasico? Mientras no defina un constructor, **hereda los constructores de la superclase**:

```bash
>>> const palio = new AutoClasico(3000)
a AutoClasico[kilometraje=3000, fechaUltimaLustrada=null]
```

Esto es algo cómodo que no todos los lenguajes tienen (por ejemplo Java, que te obliga a definir constructores para las subclases si la superclase modifica el constructor vacío).

## Redefinición de Constructores

Ahora bien, si queremos escribir un constructor del auto clásico indicando la fecha de la última lustrada, aparece una idea repetida:

```xtend
class AutoClasico inherits Auto {
	var fechaUltimaLustrada

	constructor(_kilometraje, _fechaUltimaLustrada) = super() {
		kilometraje = _kilometraje  // Idea repetida
		fechaUltimaLustrada = _fechaUltimaLustrada
	}

	...
}
```

Para esto vamos a definir el constructor en función del constructor del padre:

```xtend
class AutoClasico inherits Auto {
	var fechaUltimaLustrada

	constructor(_kilometraje, _fechaUltimaLustrada) = super(_kilometraje) {
		fechaUltimaLustrada = _fechaUltimaLustrada
	}
}
```

De esta manera estamos definiendo el constructor

- llamando primero al constructor con un parámetro del padre, que puede hacer otras cosas además de inicializar la referencia al kilometraje
- y luego inicializa la referencia fechaUltimaLustrada


## Límite para heredar constructores - Juan

Repasemos esta última definición

```xtend
class AutoClasico inherits Auto {
	var fechaUltimaLustrada

	constructor(_kilometraje, _fechaUltimaLustrada) = super(_kilometraje) {
		fechaUltimaLustrada = _fechaUltimaLustrada
	}
}
```

¿Cuántos constructores tiene AutoClasico? Solo uno, porque **cuando escribimos un constructor Wollok inhabilita los constructores heredados**. Esto tiene sentido... al escribir un constructor estamos diciendo cómo debe instanciarse e inicializarse un objeto, entonces cualquier definición de la superclase pierde valor (especialmente cuando tenemos referencias constantes)

```bash
>>> new AutoClasico(1000)
ERROR: Wrong number of arguments. Should be new AutoClasico(_kilometraje,_fechaUltimaLustrada) (line: 1)
```

Dentro de la definición del constructor de 2 parámetros de AutoClasico sí estamos llamando al constructor de la superclase, pero no es visible desde afuera (una vez que escribimos el constructor para AutoClasico).


# Definición de un WKO cuando hay constructores con parámetros

Volviendo al objeto peugeot404, tenemos un problema: no hay constructor default, por lo tanto esta definición da error

```xtend
object peugeot404 inherits Auto {
	...
}
```

Para poder crear el wko necesitamos pasarle valores concretos a un constructor:

```xtend
object peugeot404 inherits Auto(100000) {
	...
}
```

Ahora sí podemos usar nuestro wko peugeot404:

```bash
>>> peugeot404
peugeot404[duenio=null, kilometraje=100000]
```
