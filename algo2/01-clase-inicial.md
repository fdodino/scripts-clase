# Script Inicial Algo2

## 18:30 - 19:00 Presentación

- De qué se trata la materia: Diseño, qué es diseñar, armar la columna vertebral: Algo1, Algo2, Algo3, PHM y tangencialmente Bases de Datos, PHM, Labo 1 y Labo 2
- Metáfora asociada: ya saben mover las piezas del ajedrez (Algo1), ahora vamos a ver distintas interacciones, que forman soluciones conocidas (patrones). También vamos a ver cómo diseñar cuando tenemos componentes externos que escapan a nuestro control y algunas técnicas de testeo unitario.
- El lenguaje es Kotlin, que van a ver que se parece mucho a Wollok.
- La materia es tanto teórica como práctica. Necesitan aprobar el TP para poder rendir el parcial.
- Modalidad del parcial: lo hacen en el aula, se los corregimos y lo tienen que defender en un coloquio. Es importante que puedan defender lo que hicieron, si no saben por qué hicieron algo nosotros no podemos aprobarles la materia.
- Algo2 no traiciona a Algo1, lo complementa.
- Algo2 es la base para lo que sigue que es mucho más tecnológico.
- Ojo! Otros años hemos guardado la nota, Secretaría Académica ya nos bajó línea de que no podemos hacerlo porque tenemos una cantidad de matriculados que no se sostiene. Tienen que ir a hablar a Alumnos para que los inscriban oficialmente en Guaraní, no podemos guardar nota porque estamos yendo en contra de la política que nos comunicaron.
- Presentación de ayudantes.

## 19:00 -> Les damos el enunciado de Seguros. Creamos un proyecto.

- Qué es Gradle => https://wiki.uqbar.org/wiki/articles/gradle.html
  reifica un proyecto, maneja dependencias (repositorios), y automatiza tareas (mediante plugins)
- New project > Gradle Kotlin, vamos a reemplazar el default por [este archivo](./clase1/build.gradle.kts). Activamos el build automático.
- Componente = Artefacto = Proyecto, ponerle groupId + artifactId + version (no meterse en versionado semántico)
- Maven Central vs. otros repositorios
- Probar descargar una dependencia que no tengamos en el proyecto => veamos que la guarda en /home/dodain/.gradle/caches/modules-2/files-2.1
- Probar descargar una dependencia que no exista
- Tipos de dependencia: para desarrollo, para testing (testImplementation), para ejecutar (runtime => implementation)
- Plugins -> definen tareas, como el plugin de Kotlin que permite compilar de Kotlin a Java (más adelante lo veremos)
- No meterse en armar tareas ni Gradle Wrapper

## 19:30 TDD con algo sencillo

- Creamos un test con Kotest, que valide que se puede pagar un siniestro para un cliente normal que no sea moroso

```kotlin
class CobroSiniestroSpec : DescribeSpec({
    // NO escribirlo isolationMode = IsolationMode.InstancePerTest

    it("si no es moroso puede cobrar siniestro y no debe registrar la consulta del libre deuda") {
        // Arrange
        val clienteNoMoroso = Cliente()
        // Assert
        clienteNoMoroso.puedeCobrarSiniestro() shouldBe true
    }
}
```

Hablamos del formato de Kotest => es simpático porque el syntatic sugar que te da se acerca al lenguaje humano => es lo que llamamos DSL (Domain Specific Language)

- No pensamos en subclasificar, o poner un flag, ni siquiera construimos la clase Cliente, eso viene después, primero la interfaz del objeto
- Corremos el test, obviamente no pasa. Lo que hacemos ahora es implementar lo mínimo indispensable para que no falle el test:

```kotlin
class Cliente() {
    fun puedeCobrarSiniestro() = true
}
```

- Ejecutamos el test, pasa. Nos vamos al recreo con la gente indignada pensando que la engañaron.

## 20:20 Post recreo -> JDK vs. JRE

- https://wiki.uqbar.org/wiki/articles/jdkVsJre.html
- Importante, que entiendan la diferencia entre el runtime (JRE) vs. el que compila y que nos permite debuggear una vez que el compilador de Kotlin (kotlinc) actuó.
- Veamos el directorio build/classes/kotlin ... el compilado => solo para que entiendan, no va a hacer falta que lo miremos hasta PHM
- 

## 20:30 Seguimos con el ejemplo del Seguro una hora más

- Evolucionamos hasta resolver https://wiki.uqbar.org/wiki/articles/kotest-testeo-unitario-avanzado.html
  - Primero, un test para verificar que un cliente no moroso puede cobrar un siniestro. Eso hace que falle el segundo test.
  - Lo corregimos poniendo una variable saldo (protected/private/public), vemos los métodos (fun) y también puede que surja el constructor, o bien un Act en el test y hablemos de AAA (Arrange, Act, Assert). Si no lo hablamos después.
  - Pasamos a una flota, tenemos que ver qué hacemos. Es la flota parte de un auto? Comparten algo más que la deuda? Es suficiente para construir una solución con herencia? Ver qué eligen, si el if o la herencia. Hablar de que el if es una solución que tiene cosas buenas: es simple y barata cuando no tenemos información suficiente, pero que hay que hacer las preguntas necesarias al usuario. Si hay tiempo podríamos elegir resolverlo con ifs y después pasar a una solución con herencia sin cambiar los tests y viendo que siguen funcionando.
  - Hacemos el test de una flota con muchos autos para ver cuándo puede cobrar un siniestro.
  - Luego el test de una flota con muchos autos para ver cuándo no puede cobrar un siniestro.
  - Y cerramos con una flota con pocos autos para cuando puede...
  - ... y cuando no puede cobrar un siniestro
  - una posible solución: [los tests](./clase1/CobroSiniestroSpec.kt) y [la jerarquía de cliente](./clase1/Cliente.kt)
- AAA => Arrange / Act / Assert si no lo vimos antes
- Repasamos: clases de equivalencia para que los tests tengan sentido, valores límite para elegir cuánta deuda y cuántos autos elegir.
- Evitar duplicaciones, incluso con los nombres de los describes ("flota con pocos autos vs. flota con 5 autos)
- isolationMode = IsolationMode.InstancePerTest => importante para limpiar el estado de todo el describe antes de ejecutar los tests
- Se pueden anidar describes, ojo con el estado de cada describe, tener factory methods es más prolijo y fácil de mantener

Y listo. No creo que entre CI y menos coverage.
