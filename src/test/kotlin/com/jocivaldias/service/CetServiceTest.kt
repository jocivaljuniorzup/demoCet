package com.jocivaldias.service

import com.jocivaldias.CetRequest
import com.jocivaldias.Pagamento
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

@MicronautTest
class CetServiceTest(val cetService: CetService) {

    @Test
    fun `dado uma requisicao valida com pagamento mensal variavel retorna o CET correto`(){
        val cetRequest = CetRequest.newBuilder()
                .setCreditoConcedido(113500.00)
                .setDataLiberacaoCredito("2016-05-27")
                .addPagamentos(Pagamento.newBuilder()
                        .setDataCobranca("2016-06-27")
                        .setValorCobrado(7467.08))
                .addPagamentos(Pagamento.newBuilder()
                        .setDataCobranca("2016-07-27")
                        .setValorCobrado(7824.97))
                .addPagamentos(Pagamento.newBuilder()
                        .setDataCobranca("2016-08-26")
                        .setValorCobrado(107824.97))
                .build()

        Assertions.assertEquals(41.61, cetService.calcularCET(cetRequest).cet)
    }

    @Test
    fun `dado uma requisicao valida com pagamento mensal fixo 10 meses retorna o CET correto`(){
        val cetRequestBuilder = CetRequest.newBuilder()
                .setCreditoConcedido(974.73)
                .setDataLiberacaoCredito("2021-01-01")

        for (month in 1..10){
            cetRequestBuilder.addPagamentos(Pagamento.newBuilder()
                    .setDataCobranca(LocalDate.of(2021, month, 1).toString())
                    .setValorCobrado(134.54))
        }

        val cetRequest = cetRequestBuilder.build()

        Assertions.assertEquals(153.69, cetService.calcularCET(cetRequest).cet)
        Assertions.assertDoesNotThrow{
            cetService.calcularCET(cetRequest).cet
        }
    }

    @Test
    fun `dado uma requisicao valida com pagamento mensal fixo 5 meses retorna o CET correto`(){
        val cetRequestBuilder = CetRequest.newBuilder()
                .setCreditoConcedido(63000.11)
                .setDataLiberacaoCredito("2021-01-01")

        for (month in 1..5){
            cetRequestBuilder.addPagamentos(Pagamento.newBuilder()
                    .setDataCobranca(LocalDate.of(2021, month, 1).toString())
                    .setValorCobrado(13280.29))
        }

        val cetRequest = cetRequestBuilder.build()

        Assertions.assertEquals(38.29, cetService.calcularCET(cetRequest).cet)
        Assertions.assertDoesNotThrow{
            cetService.calcularCET(cetRequest).cet
        }
    }

    @Test
    fun `dado uma requisicao invalida lanca exception`(){
        val invalidCETRequest = CetRequest.newBuilder()
                .setCreditoConcedido(113500.00)
                .setDataLiberacaoCredito("2016-05-27")
                .addPagamentos(Pagamento.newBuilder()
                        .setDataCobranca("2014-06-27")
                        .setValorCobrado(7467.08))
                .addPagamentos(Pagamento.newBuilder()
                        .setDataCobranca("2016-07-27")
                        .setValorCobrado(7824.97))
                .addPagamentos(Pagamento.newBuilder()
                        .setDataCobranca("2016-08-26")
                        .setValorCobrado(107824.97))
                .build()

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            cetService.calcularCET(invalidCETRequest)
        }
    }

}