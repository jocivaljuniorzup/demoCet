package com.jocivaldias.endpoint.grpc

import com.jocivaldias.CetGrpcKt
import com.jocivaldias.CetRequest
import com.jocivaldias.CetResponse
import com.jocivaldias.service.CetService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class CetEndpoint(private val cetService: CetService): CetGrpcKt.CetCoroutineImplBase(){

    private val log: Logger = LoggerFactory.getLogger(CetEndpoint::class.java)

    override suspend fun calcularCet(request: CetRequest): CetResponse {
        log.info("Nova requisição para calculo: {}", request)
        return cetService.calcularCET(request)
    }
}