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
}
```

Aquí vemos que modelamos la Persona como objeto inmutable. 

### Constructor default + referencias variables

Otra opción sería dejar las referencias variables, no definir un constructor y forzar a que el fixture haga la inicialización:

```xtend
fixture {
	const lito = new Persona()
	lito.nombre = "Jose"
	lito.apellido = "Guerrero"
}
```

Esto tiene como contra que para inicializar 10 personas, hay 30 lineas de código repetitivas y en algún momento el objeto Persona queda inconsistente (tiene referencias nulas). ¿Cuál es el problema?

```xtend
fixture {
	const lito = new Persona()
	lito.nombre = "Jose"
    lito.nombre.length()  // Crash boom bang!
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
