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
