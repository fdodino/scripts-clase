abstract class Cliente(var deuda: Int = 0) {
    fun facturar(monto: Int) {
        deuda = deuda + monto
    }
    abstract fun puedeCobrarSiniestro(): Boolean
    fun esMoroso() = deuda > 0
}

class ClienteNormal : Cliente() {
    override fun puedeCobrarSiniestro() = !esMoroso()
}

const val LIMITE_FLOTA_MUCHOS_AUTOS = 5

class Flota(var autos: Int) : Cliente() {
    override fun puedeCobrarSiniestro() = deuda <= maximoDeuda()

    fun maximoDeuda() = if (tieneMuchosAutos()) 10000 else 5000
    fun tieneMuchosAutos() = autos > LIMITE_FLOTA_MUCHOS_AUTOS
}