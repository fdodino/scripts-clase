import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class CobroSiniestroSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    describe("Tests Cobro Siniestro") {
        describe("Dado un cliente normal") {
            it("si no es moroso puede cobrar siniestro y no debe registrar la consulta del libre deuda") {
                // Arrange
                val clienteNoMoroso = ClienteNormal()
                // Assert
                clienteNoMoroso.puedeCobrarSiniestro() shouldBe  true
                clienteNoMoroso.tieneConsultas(LocalDate.now()) shouldBe false
            }
            it("si tiene deuda no puede cobrar siniestro y debe registrar la consulta del libre deuda") {
                // Arrange
                val clienteMoroso = ClienteNormal()
                // Act
                clienteMoroso.facturar(10)
                // Assert
                clienteMoroso.puedeCobrarSiniestro() shouldBe false
                clienteMoroso.tieneConsultas(LocalDate.now()) shouldBe true
            }
        }
    }
})