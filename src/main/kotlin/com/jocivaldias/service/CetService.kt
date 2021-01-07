package com.jocivaldias.service

import com.jocivaldias.CetRequest
import com.jocivaldias.CetResponse
import com.jocivaldias.Pagamento
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Singleton
import kotlin.math.pow

@Singleton
class CetService {

    private val valorMaximo = 10000.00
    private val precisao = 0.000001
    private val diasNoAno = 365.00

    private var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val log: Logger = LoggerFactory.getLogger(CetService::class.java)

    fun calcularCET(cetRequest: CetRequest): CetResponse {
        val taxaAnual = calcularCETAnual(cetRequest.creditoConcedido,
                LocalDate.parse(cetRequest.dataLiberacaoCredito, dateFormatter),
                cetRequest.pagamentosList)

        return CetResponse.newBuilder()
                .setCet(BigDecimal(taxaAnual).setScale(2, RoundingMode.HALF_UP).toDouble())
                .build()
    }

    private fun calcularCETAnual(creditoConcedido: Double, dataLiberacaoCredito: LocalDate, pagamentosList: List<Pagamento>
    ): Double {
        var cet = 0.0

        while (true) {
            var aproximacaoCreditoConcedido = 0.0
            pagamentosList.forEach{ pagamento ->
                val dataPagamento = LocalDate.parse(pagamento.dataCobranca, dateFormatter)
                val diasEntreConcessaoEPagamento = ChronoUnit.DAYS.between(dataLiberacaoCredito, dataPagamento)

                if (diasEntreConcessaoEPagamento < 0){
                    log.error("Data de pagamento anterior a data de concessão do crédito: {} - {}", dataPagamento, dataLiberacaoCredito)
                    throw IllegalArgumentException("Data de pagamento anterior a data de concessão do crédito")
                }

                aproximacaoCreditoConcedido += pagamento.valorCobrado / (1.0 + cet).pow(diasEntreConcessaoEPagamento / diasNoAno)
            }
            cet += precisao

            if (cet >= valorMaximo) {
                log.error(
                        "Não foi possível realizar o cálculo para as seguintes informações: {}, {}, {}",
                        creditoConcedido,
                        dataLiberacaoCredito,
                        pagamentosList
                )
                return -1.0
            }

            if ( aproximacaoCreditoConcedido - creditoConcedido <= 0) {
                break
            }

            cet *= aproximacaoCreditoConcedido / creditoConcedido
        }

        return cet * 100.0
    }

}
