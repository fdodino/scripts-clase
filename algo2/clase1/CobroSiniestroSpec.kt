package ar.edu.unsam.algo2.seguros

import ClienteNormal
import Flota
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

// En los tests prescindimos de utilizar las mismas constantes que en cliente
// para que si esos valores cambian los test se rompan (está hecho por diseño).
//
// https://kotest.io/
class CobroSiniestroSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    describe("Tests Cobro Siniestro") {
        describe("Dado un cliente normal") {
            it("si no es moroso puede cobrar siniestro y no debe registrar la consulta del libre deuda") {
                // Arrange
                val clienteNoMoroso = ClienteNormal()
                // Assert
                clienteNoMoroso.puedeCobrarSiniestro() shouldBe true
            }
            it("si tiene deuda no puede cobrar siniestro y debe registrar la consulta del libre deuda") {
                // Arrange
                val clienteMoroso = ClienteNormal()
                // Act
                clienteMoroso.facturar(10)
                // Assert
                clienteMoroso.puedeCobrarSiniestro() shouldBe false
            }
        }
        describe("Dada una flota con muchos autos") {
            it("si tiene mucha deuda no puede cobrar siniestro") {
                // Arrange
                val flotaMuchaDeudaMuchosAutos = Flota(autos = 6)
                // Act
                flotaMuchaDeudaMuchosAutos.facturar(10001)
                // Assert
                flotaMuchaDeudaMuchosAutos.puedeCobrarSiniestro() shouldBe false
            }
            it("si no tiene poca deuda puede cobrar siniestro") {
                // Arrange
                val flotaPocaDeudaMuchosAutos = Flota(autos = 6)
                // Act
                flotaPocaDeudaMuchosAutos.facturar(10000)
                // Assert
                flotaPocaDeudaMuchosAutos.puedeCobrarSiniestro() shouldBe true
            }
        }
        describe("Dada una flota con pocos autos") {
            it("si tiene mucha deuda no puede cobrar siniestro") {
                // Arrange
                val flotaMuchaDeudaPocosAutos = Flota(autos = 5)
                // Act
                flotaMuchaDeudaPocosAutos.facturar(5001)
                // Assert
                flotaMuchaDeudaPocosAutos.puedeCobrarSiniestro() shouldBe false
            }
            it("si no tiene poca deuda puede cobrar siniestro") {
                // Arrange
                val flotaPocaDeudaPocosAutos = Flota(autos = 5)
                // Act
                flotaPocaDeudaPocosAutos.facturar(5000)
                // Assert
                flotaPocaDeudaPocosAutos.puedeCobrarSiniestro() shouldBe true
            }
        }
    }
})
