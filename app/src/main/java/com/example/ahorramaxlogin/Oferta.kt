package com.example.ahorramaxlogin

data class Oferta(

    val id: String,
    val supermercado: String,
    val porcentajeDescuento: String?,
    val descripcionCorta: String,
    val descripcionLarga: String,
    val logoBancoResId: Int? = null,
    val logoTarjetaResId: Int? = null,
    val esCuotas: Boolean = false,
    val numeroCuotas: String? = null,
    val bancoCuotasResId: Int? = null,
    val bancoCuotas2ResId: Int? = null,
    val diasValidos: List<String>,
    var isFavorite: Boolean = false,
    val urlOferta: String? = null
)


