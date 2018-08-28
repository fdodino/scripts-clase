# Clase 3 Objetos

por Fernando Dodino - Agosto 2017
Distribuido con licencia [Creative commons Share-a-like](https://creativecommons.org/licenses/by-sa/4.0/legalcode)

# Resumen Lenguajes OO Prototyped-based

> **Advertencia:** esto puede ser un poco fumado.

Hemos trabajado hasta aquí la noción de objeto como la primera forma de definir conceptos, agrupar comportamiento y encapsular estado.
Al objeto le puedo enviar un mensaje y el method lookup se resuelve porque el objeto receptor es el que responde al mensaje.

Esta forma de definir código en base a un objeto no es exclusiva de Wollok:

## Self 

La primera idea de tener la noción de objeto fue [**Self**](http://www.selflanguage.org/) en 1986, que nació en Xerox Parc Place como hermano menor de Smalltalk (comparten una sintaxis muy similar).

Cada objeto define

- slots o referencias
- métodos

![self](http://handbook.selflanguage.org/2017.1/_images/Chapter_2_Image_2.png)

Fue también el primero que introdujo la idea de manipulación enteramente visual de objetos:

![morphic](http://handbook.selflanguage.org/2017.1/_images/Figure1.png)

Te dejamos dos videos que muestran \
- [cómo funciona la delegación](https://www.youtube.com/watch?v=NYEKO7JcmLE) y 
- [cómo crear una cuenta bancaria](https://www.youtube.com/watch?v=5Jhi5yN9S1o)

## Javascript

Un tiempo más tarde nació Javascript (el [01/01/1997](https://www.ecma-international.org/publications/files/ECMA-ST-ARCH/ECMA-262,%201st%20edition,%20June%201997.pdf)), que popularizó el término _prototype-based_ para los lenguajes que trabajan exclusivamente con objetos (al menos hasta la versión ES6 que incorporó las clases como _syntatic sugar_). Vamos a hacer el ejemplo de pepita en javascript, que pueden probar en la consola de cualquier navegador (presionando F12)

```javascript
// pepita es un objeto...
var pepita = {
    
	energia: 0,   // que tiene energia

	volar: function(kilometros) {  // que sabe volar n kilómetros (function es equivalente al method de Wollok)
		this.energia = this.energia - (8 * kilometros)   // y eso le resta energia (this es equivalente al self de Wollok)
	},

	comer: function(gramos) {      // que sabe comer g gramos
		this.energia = this.energia + (4 * gramos)	
	},
	
	cantar: function() {	// que sabe cantar
		console.log("pri pri pri")
	}
}
```

Lo probamos en la consola del navegador

```javascript
// copiamos la definición anterior de pepita
pepita
pepita.energia   // puedo acceder a información de pepita
pepita.comer(50)
pepita
pepita.volar(10)
pepita.energia = 170  // incluso puedo asignar información de pepita sin mandar mensajes
pepita.descansar = function() { this.energia = 1000 }  // groso! Puedo definir comportamiento nuevo
// incluso puedo crear referencias nuevas
pepita.durmioSiesta = false
// pisamos la definición
pepita.descansar = function() { 
	this.durmioSiesta = true
	this.energia = 1000 
}
// y vemos qué pasa
pepita.durmioSiesta
pepita.descansar()
pepita.durmioSiesta
```

Similitudes con Wollok:

- no estoy obligado a definir los tipos. Yo puedo enviar cualquier mensaje que un objeto entienda (así funciona el method lookup básico).
- puedo definir objetos anónimos y referenciarlos mediante variables
- el objeto agrupa comportamiento y estado (el conjunto de variables)

Algunas diferencias respecto a Wollok

- javascript permite el acceso directo a las referencias de un objeto. Wollok nos obliga a hacerlo mediante accessors, para javacript tanto las variables como los métodos son cajoneras donde la única diferencia es que en las segundas guardamos expresiones lambda.
- javascript es dinámico: puedo agregar o modificar referencias y comportamiento sin que haya un "reinicio". En esto reside su gran poder.

En Wollok es necesario cambiar la referencia a un nuevo objeto para poder lograr que un mensaje pueda ser entendido:

```xtend
>>> var pepita = object { }
an Object[]
>>> pepita.jugar()
wollok.lang.MessageNotUnderstoodException: anonymousObject does not understand message jugar()

>>> pepita = object { method jugar() { } }
an Object[]
>>> pepita.jugar()
>>> 
```

Si quieren chusmear más pueden profundizar sobre

- [Diseño en Javascript 5](https://www.ecma-international.org/publications/files/ECMA-ST-ARCH/ECMA-262,%201st%20edition,%20June%201997.pdf)
- [Diseño en ES6](https://docs.google.com/document/d/1enl1DzdZPu7qiD0UZ1e9mBQJpkxTDaoQdvxjsyQlQnc/edit?usp=drive_web)

## Ioke

[Ioke](https://en.wikipedia.org/wiki/Ioke_(programming_language)) (06/11/2008) fue un proyecto basado en la VM de Java que proponía trabajar con prototipos, intercalando lenguajes como Io, Ruby y Lisp. Vemos la misma implementación de pepita en Ioke:

```ioke
pepita = Origin mimic do(
	energia = 0.0
	comer = method(gramos, self energia += 4 * gramos)
	volar = method(kilometros, self energia -= (kilometros + 10))
	show = method("Pepita energia: $#{energia}" println)
)


pepita show
"Pepita come 10 gramos" println
pepita comer(10)
"Pepita vuela 3 kilometros" println
pepita volar(3)
pepita show
```

El archivo pepita.ik tiene la definición de pepita y luego el script que prueba cómo come y vuela.

Lo evaluamos en la consola

```bash
fernando@fernando-laptop ~/apps/ioke/bin $ ./ioke pepita.ik
Pepita energia: $0.0
Pepita come 10 gramos
Pepita vuela 3 kilometros
Pepita energia: $27.0
```

## Ozono

Ozono (anteriormente llamado LOOP: Learning Object-Oriented Programming) fue una herramienta desarrollada por algunos docentes de esta facultad que permitía el trabajo con objetos antes de utilizar otras herramientas de Smalltalk.

Pueden ver [en esta página](http://www.pdep.com.ar/software/software-pharo/object-browser-ultima-version) una demostración funcionando (por lo general el primer TP de la materia se hacía en este entorno).

## Otras apariciones de object

En [Scala](http://www.scala-lang.org) aparece la noción de objeto pero está asociada a otra idea que contaremos más adelante.


