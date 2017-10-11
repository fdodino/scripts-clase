# Introducción

Yo quiero cargar diferentes tipos de aparatos

- laptops
- iphones
- otros tipos de celulares

Mauro representa una central cargadora, tiene todos los celulares del mundo, su misión es 

- encontrar el cargador correcto, 
- conectarlo y 
- pedirle a Alexis que lo enchufe

Alexis representa una central de electricidad.
Tiene que

- verificar que haya luz (si no hay luz no puede cargar nada)
- verificar si puede enchufar

# Primeras líneas de código

En la clase CentralCargadora

```javascript
method carga(aparato) {
	self.validarCargadorPara(aparato)
    const cargador = self.cargadorPara(aparato)
    lo conecta
    edeluz.enchufar(cargador)
}
```

La clase Edeluz define este método

```javascript
method enchufar(cargador) {
	self.validarSiHayLuz()
    self.validarConector(cargador)
    self.conectar(cargador)
}
```

# Pruebas 

## Cargar un celular de un aparato común o Happy path

Primer paso, hacemos el camino feliz: el aparato tiene un cargador conocido. Encontramos un cargador y hay luz... todo funciona ok.

## Cargar un iphone: puede fallar...

Segundo paso, pedimos cargar un iPhone.
La central valida si hay un cargador, ...

```javascript
method validarCargadorPara(aparato) {
	if (!self.tieneCargadorPara(aparato)) {
		throw new Exception("No hay cargador")
	}
}
```

## Hagamos un test con eso

test "probar si tengo cargador para i-Phone" {
	assert.throwsWithMessage("No hay cargador", {
		centralDeCarga.carga(iphone)
	})
}

Esperamos entonces que falle el test con un mensaje representativo.

## Tercer intento: cargar una notebook

Tenemos cargador de la notebook, peero... no hay luz

```javascript
method validarSiHayLuz() {
	if (!self.hayLuz()) {
		throw new Exception("No hay luz")
	}
}
```

Lo reflejamos en el test:

```javascript
test "probar qué pasa si no tengo luz" {
	edeluz.cortar()
	assert.throwsWithMessage("No hay luz", {
		centralDeCarga.carga(notebookNahuel)
	})
}
```

Para que eso ocurra debemos tener en cuenta que la configuración debe evitar que explote el cargador previamente.
 
