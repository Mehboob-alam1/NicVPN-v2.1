package com.nicadevelop.nicavpn.http.request

import io.michaelrocks.paranoid.Obfuscate

@Obfuscate
object Client {

    // Tamaño del búfer de recepción de la aplicación / tamaño máximo de datos en la cola
    const val TAM_RECEIVE_BUFFER_APP: Int = 1024
    const val TAM_BUFFER_SERVER_RESPONSE_CONTENT = 1024

    // Tamaño máximo que puede ser la respuesta del encabezado http enviado
    const val MAX_LEN_SERVER_RESPONSE_HEAD = 1024 * 1000

    // Tamaño del búfer de recepción para la aplicación base64
    const val LENGTH_RECEIVE_BUFFER_APP_BASE64 =
        4 * TAM_RECEIVE_BUFFER_APP / 3 + 3 and 3.inv()

    // Tamaño máximo de texto en base64 enviado en una solicitud GET (por encabezado X-Data)
    const val MAX_QTD_LENGTH_TO_SEND = 11000

    // Número máximo de bytes de aplicación que se pueden enviar a la vez en una solicitud
    const val MAX_QTD_BYTES_TO_SEND = MAX_QTD_LENGTH_TO_SEND * 3 / 4
}