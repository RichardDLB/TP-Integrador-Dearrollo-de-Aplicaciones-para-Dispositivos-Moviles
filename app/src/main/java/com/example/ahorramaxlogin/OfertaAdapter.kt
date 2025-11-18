package com.example.ahorramaxlogin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OfertaAdapter(private var ofertas: List<Oferta>) :
    RecyclerView.Adapter<OfertaAdapter.OfertaViewHolder>() {

    var onFavoriteClickListener: ((Oferta) -> Unit)? = null
    var onItemClickListener: ((Oferta) -> Unit)? = null // ✅ NUEVO: Click para toda la tarjeta

    // Dimensiones de los logos en DP
    private val LARGE_WIDTH_DP = 160
    private val LARGE_HEIGHT_DP = 80
    private val SMALL_WIDTH_DP = 120
    private val SMALL_HEIGHT_DP = 60

    private val LARGE_LOGO_SUPERMARKETS = listOf("Carrefour", "Coto", "Dia")

    class OfertaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPorcentajeCuotas: TextView = itemView.findViewById(R.id.tv_porcentaje_cuotas)
        val tvLabelDescuentoCuotas: TextView = itemView.findViewById(R.id.tv_label_descuento_cuotas)
        val tvDescripcionCorta: TextView = itemView.findViewById(R.id.tv_descripcion_corta)
        val tvDescripcionLarga: TextView = itemView.findViewById(R.id.tv_descripcion_larga)
        val ivLogoBanco1: ImageView = itemView.findViewById(R.id.iv_logo_banco_1)
        val ivLogoBanco2: ImageView = itemView.findViewById(R.id.iv_logo_banco_2)
        val ivLogoTarjeta: ImageView = itemView.findViewById(R.id.iv_logo_tarjeta)
        val ivFavoriteStar: ImageView = itemView.findViewById(R.id.iv_favorite_star)
    }

    private fun setLogoSize(view: ImageView, isLarge: Boolean) {
        val layoutParams = view.layoutParams
        val widthDp = if (isLarge) LARGE_WIDTH_DP else SMALL_WIDTH_DP
        val heightDp = if (isLarge) LARGE_HEIGHT_DP else SMALL_HEIGHT_DP

        val density = view.context.resources.displayMetrics.density
        layoutParams.width = (widthDp * density).toInt()
        layoutParams.height = (heightDp * density).toInt()

        view.layoutParams = layoutParams
    }

    private fun getSupermarketNameFromLogoId(resId: Int): String? {
        return when (resId) {
            R.drawable.carrefour_tarjeta_logo -> "Carrefour"
            R.drawable.coto_logo -> "Coto"
            R.drawable.dia_logo -> "Dia"
            else -> null
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfertaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_oferta, parent, false)
        return OfertaViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfertaViewHolder, position: Int) {
        val oferta = ofertas[position]

        val isSupermercadoLarge = LARGE_LOGO_SUPERMARKETS.contains(oferta.supermercado)

        // --- Configurar Texto ---
        if (oferta.esCuotas) {
            holder.tvPorcentajeCuotas.text = oferta.numeroCuotas
            holder.tvLabelDescuentoCuotas.text = "Cuotas"
            holder.tvDescripcionCorta.text = oferta.descripcionCorta
        } else {
            holder.tvPorcentajeCuotas.text = oferta.porcentajeDescuento
            holder.tvLabelDescuentoCuotas.text = "De Descuento"
            holder.tvDescripcionCorta.text = oferta.descripcionCorta
        }
        holder.tvDescripcionLarga.text = oferta.descripcionLarga

        // --- LÓGICA DE LOGOS Y ESCALADO ---
        if (oferta.esCuotas) {
            if (oferta.bancoCuotasResId != null) {
                holder.ivLogoBanco1.setImageResource(oferta.bancoCuotasResId)
                setLogoSize(holder.ivLogoBanco1, isSupermercadoLarge)
                holder.ivLogoBanco1.visibility = View.VISIBLE
            } else {
                holder.ivLogoBanco1.visibility = View.GONE
            }

            if (oferta.bancoCuotas2ResId != null) {
                holder.ivLogoBanco2.setImageResource(oferta.bancoCuotas2ResId)
                setLogoSize(holder.ivLogoBanco2, false)
                holder.ivLogoBanco2.visibility = View.VISIBLE
            } else {
                holder.ivLogoBanco2.visibility = View.GONE
            }
            holder.ivLogoTarjeta.visibility = View.GONE

        } else {
            if (oferta.logoBancoResId != null) {
                holder.ivLogoBanco1.setImageResource(oferta.logoBancoResId)
                val isBancoLogoSupermarket = LARGE_LOGO_SUPERMARKETS.contains(getSupermarketNameFromLogoId(oferta.logoBancoResId))
                setLogoSize(holder.ivLogoBanco1, isBancoLogoSupermarket)
                holder.ivLogoBanco1.visibility = View.VISIBLE
            } else {
                holder.ivLogoBanco1.visibility = View.GONE
            }

            if (oferta.logoTarjetaResId != null) {
                holder.ivLogoTarjeta.setImageResource(oferta.logoTarjetaResId)
                setLogoSize(holder.ivLogoTarjeta, isSupermercadoLarge)
                holder.ivLogoTarjeta.visibility = View.VISIBLE
            } else {
                holder.ivLogoTarjeta.visibility = View.GONE
            }

            holder.ivLogoBanco2.visibility = View.GONE
        }


        val starIcon = if (oferta.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_empty
        holder.ivFavoriteStar.setImageResource(starIcon)

        // Listener para la estrella
        holder.ivFavoriteStar.setOnClickListener {
            onFavoriteClickListener?.invoke(oferta)
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(oferta)
        }
    }

    override fun getItemCount(): Int = ofertas.size

    fun updateList(newList: List<Oferta>) {
        ofertas = newList
        notifyDataSetChanged()
    }
}