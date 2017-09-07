
# Instalar extensiones para nuestro editor

Agregale las extensiones para el Visual Studio Code según muestra [esta página](http://wiki.uqbar.org/wiki/articles/instalacion-de-entorno-javascript.html)

# Tecnologías

Para crear un proyecto desde cero vamos a tomar como base un proyecto existente, de manera muy similar a lo que hicimos en Algoritmos 2 con un proyecto en Xtend, solo que con herramientas similares.

- **npm** o node package manager: para resolver las dependencias (la función equivalente la cumplía Maven)
- **Jasmine** para definir los tests (el equivalente a JUnit) + **Karma** que nos permitirá automatizar las tareas mediante npm
- El código que vamos a escribir en Ecmascript 6 se traducirá a Javascript 5 que es compatible con más tecnologías, mediante un *transpilador* que se llama Babel (algo equivalente a lo que hace xtend traduciendo código xtend a Java)

![image](https://i1.wp.com/wipdeveloper.com/wp-content/uploads/2017/05/babel-es6-inaction1.gif?resize=940%2C330&ssl=1)

# Cómo iniciar el proyecto

## Clonar un ejemplo de otro repositorio

Desde el git bash o la consola nos posicionamos en un directorio y bajamos algún ejemplo base, por ejemplo el que muestra la herencia de clientes:

```bash
# git clone https://github.com/uqbar-project/clientesHerencia-es6
```

## Generar nuestro nuevo proyecto

Debemos construir un directorio paralelo que tenga esta estructura:

```bash
+ proyectoNuevo (el nombre que quieran)
  + src
  + spec
```

## Copiar archivos de definición de un proyecto

Del ejemplo base vamos a copiar dos archivos importantes al directorio raíz de nuestro proyecto:

- **package.json**: el equivalente al pom.xml, contiene información sobre el proyecto y sus dependencias
- **karma.conf.js**: contiene la configuración para ejecutar los tests desde un script
- **.travis.yml**: es el archivo que contiene la configuración para poder ejecutar en un server de Continuous Integration nuestro ejemplo

Nos debería quedar entonces la siguiente estructura:

```bash
+ proyectoNuevo (el nombre que quieran)
  + src
  + spec
- package.json
- karma.conf.js
- .travis.yml
```

## Editar el package.json

Abrimos el Visual Studio Code y editamos la información del package.json

![image](./img/editarPackageJSONVisualStudio)

Hay que cambiar estos atributos:

- name
- description
- keywords
- repository, y dentro la url

## 