package ar.edu.unsam.algo2.tarjetaCredito

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ClienteSpec: DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    val montoMaximoSafeShopCliente = 80

    describe("Dado un cliente sin condiciones comerciales") {
        val cliente = ClientePosta(50)
        it("al pagar el vencimiento deja de ser moroso") {
            cliente.esMoroso() shouldBe true
            cliente.pagarVencimiento(50)
            cliente.esMoroso() shouldBe false
        }
        it("al comprar sube el saldo") {
            cliente.comprar(50)
            cliente.saldo shouldBe 100
        }
    }

    describe("Dado un cliente que tiene únicamente promoción como condición comercial") {
        val cliente = ClientePosta(40).apply {
            adheridoPromocion = true
        }
        it("al comprar por debajo del límite necesario para acumular puntos, no acumula puntos de promoción") {
            cliente.comprar(50)
            cliente.puntosPromocion shouldBe 0
        }
        it("al comprar por arriba del monto necesario para acumular puntos, acumula puntos de promoción") {
            cliente.comprar(60)
            cliente.puntosPromocion shouldBe 15
        }
    }

    describe("Dado un cliente que tiene únicamente safe shop como condición comercial") {
        val cliente = ClientePosta(50).apply {
            adheridoSafeShop = true
            montoMaximoSafeShop = montoMaximoSafeShopCliente
        }
        it("no debe poder comprar por más del valor permitido ni debe aumentar el saldo") {
            shouldThrow<BusinessException> { -> cliente.comprar(montoMaximoSafeShopCliente + 1) }
            cliente.saldo shouldBe 50
        }
        it("debe poder comprar hasta el valor límite") {
            cliente.comprar(montoMaximoSafeShopCliente)
            cliente.saldo shouldBe 50 + montoMaximoSafeShopCliente
        }
    }

    describe("Dado un cliente que tiene tanto safe shop como promoción como condiciones comerciales") {
        val cliente = ClientePosta(50).apply {
            adheridoSafeShop = true
            montoMaximoSafeShop = montoMaximoSafeShopCliente
            adheridoPromocion = true
        }
        it("Al comprar por arriba del límite de promoción y por debajo del safe shop, acumula puntos y la compra funciona ok") {
            cliente.comprar(60)
            cliente.saldo shouldBe 110
            cliente.puntosPromocion shouldBe 15
        }
        it("Al comprar por arriba del límite de safe shop, la compra se cancela y no acumula puntos") {
            shouldThrow<BusinessException> { -> cliente.comprar(montoMaximoSafeShopCliente + 1) }
            cliente.saldo shouldBe 50
            cliente.puntosPromocion shouldBe 0
        }
    }

})