# Repaso Objetos (30 minutos fumados en cañón)

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


## Javascript

Un tiempo más tarde nació Javascript (el [01/01/1997](https://www.ecma-international.org/publications/files/ECMA-ST-ARCH/ECMA-262,%201st%20edition,%20June%201997.pdf)), que popularizó el término prototype-based para los lenguajes que trabajan exclusivamente con objetos (al menos hasta la versión ES6 que incorporó la idea de clases). Vamos a hacer el ejemplo de pepita en javascript, que pueden probar en la consola de cualquier navegador (presionando F12)

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

Vemos algunas diferencias respecto a Wollok

- javascript permite el acceso directo a las referencias de un objeto (Wollok nos obliga a hacerlo mediante accessors)
- javascript es dinámico: puedo agregar o modificar referencias y comportamiento sin que haya un "reinicio". En esto reside su gran poder.

Si quieren chusmear más pueden profundizar sobre

- [Diseño en Javascript 5](https://www.ecma-international.org/publications/files/ECMA-ST-ARCH/ECMA-262,%201st%20edition,%20June%201997.pdf)
- [Diseño en ES6](https://docs.google.com/document/d/1enl1DzdZPu7qiD0UZ1e9mBQJpkxTDaoQdvxjsyQlQnc/edit?usp=drive_web)

## Ioke

[Ioke](https://en.wikipedia.org/wiki/Ioke_(programming_language)) (06/11/2008) fue un proyecto basado en la VM de Java que proponía trabajar con prototipos. Vemos por ejemplo la implementación de factorial

```ioke
// definición
0      fact = 1
Number fact = method(self * (self - 1) fact)

// uso
10 fact println
```

Y una cuenta de banco donde podemos depositar plata:

```ioke
// Definición de la cuenta bancaria
CuentaBancaria = Origin mimic do(
  saldo = 0.0
  depositar = method(plata, self saldo += plata)
  mostrar = method("Saldo de la cuenta: $#{saldo}" println)
)

// Uso
"Inicio: " print
CuentaBancaria mostrar

"Depositamos $10" println
CuentaBancaria depositar(10.0)

"Final: " print
CuentaBancaria mostrar
```

## Otras apariciones de object

En [Scala](www.scala-lang.org) aparece la noción de objeto pero está asociada a otra idea que contaremos más adelante.


# 10 minutos - Hablamos del TP

Cosas útiles:

- En Wollok, levantamos un REPL y evaluamos

```xtend
>>> new Date() // nos da un objeto que representa la fecha de hoy
>>> new Date(8, 9, 2017)  // nos da un objeto que representa el 8 de septiembre, no el 9 de agosto, jeje
```

En particular Ctrl + Shift + F3, permite buscar Date... y ahí pueden navegar el código para ver si hay algo que les sirva.
Parados sobre el archivo con Ctrl + O pueden navegar definiciones y métodos... espero les sea útil.

# Intro a Colecciones

