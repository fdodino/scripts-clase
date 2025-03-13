import java.time.LocalDate

abstract class Cliente {
    protected var deuda = 0

    abstract fun puedeCobrarSiniestro(): Boolean

    fun esMoroso() = deuda > 0

    fun facturar(monto: Int) {
        deuda += monto
    }
}

class ClienteNormal : Cliente() {
    private val diasDeConsulta = mutableListOf<LocalDate>()

    fun registrarConsulta() {
        val ultimaConsulta = diasDeConsulta.last()
        if (esMoroso() && ultimaConsulta === LocalDate.now() ) {
            diasDeConsulta.add(LocalDate.now())
        }
    }

    fun tieneConsultas(dia: LocalDate) = diasDeConsulta.any { it === dia }

    override fun puedeCobrarSiniestro(): Boolean {
        registrarConsulta()
        return !esMoroso()
    }
}