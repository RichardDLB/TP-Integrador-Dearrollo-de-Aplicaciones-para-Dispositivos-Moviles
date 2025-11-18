package com.example.ahorramaxlogin

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ahorramaxlogin.databinding.ActivityOffersBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

class OffersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOffersBinding
    private lateinit var adapter: OfertaAdapter
    // ✅ CORREGIDO: Inicialización de la lista como MutableList
    private var masterOfertasList: MutableList<Oferta> = mutableListOf()
    private var isShowingFavorites: Boolean = false

    private var currentSupermercadoFilter: String? = null
    private var currentDiaFilter: String? = null

    private val PREFS_NAME = "OfertasPrefs"
    private val FAVORITES_KEY = "favorite_offer_ids"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOffersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. DATA SETUP
        masterOfertasList = createDummyData().toMutableList()
        loadFavoriteStatus() // ✅ Función implementada abajo

        adapter = OfertaAdapter(masterOfertasList)
        binding.rvOfertas.layoutManager = LinearLayoutManager(this)
        binding.rvOfertas.adapter = adapter

        // Configurar el listener de favorito (estrella)
        adapter.onFavoriteClickListener = { oferta ->
            toggleFavorite(oferta) // ✅ Función implementada abajo
            adapter.notifyItemChanged(masterOfertasList.indexOf(oferta))
            applyFilters() // ✅ Función implementada abajo
            updateFavoriteButtonAppearance() // ✅ Función implementada abajo
        }

        // Configurar el listener de la tarjeta completa (Para abrir el URL)
        adapter.onItemClickListener = { oferta ->
            oferta.urlOferta?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }

        // --- SOLUCIONES DE DISEÑO ---
        binding.root.setBackgroundColor(Color.WHITE)
        binding.rvOfertas.setBackgroundColor(Color.TRANSPARENT)

        binding.spinnerContainer.visibility = View.VISIBLE

        // 2. LÓGICA DE PERFIL/CERRAR SESIÓN (ivProfile)
        binding.ivProfile.setOnClickListener {
            val popup = PopupMenu(this, binding.ivProfile)
            popup.menuInflater.inflate(R.menu.profile_menu, popup.menu) // Asume que tienes este menú

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_logout -> {
                        val intent = Intent(this, MainActivity::class.java) // Asume que existe MainActivity
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        // 3. LÓGICA DEL FILTRADO (Spinners)
        binding.spinnerSupermercado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentSupermercadoFilter = if (position > 0) parent?.getItemAtPosition(position).toString() else null
                isShowingFavorites = false
                applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerDia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentDiaFilter = if (position > 0) parent?.getItemAtPosition(position).toString() else null
                isShowingFavorites = false
                applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 4. LÓGICA DEL BOTÓN "FAVORITOS"
        binding.btnFavoritos.setOnClickListener {
            isShowingFavorites = !isShowingFavorites
            applyFilters()
            updateFavoriteButtonAppearance()
        }

        updateFavoriteButtonAppearance()
    }

    // --- FAVORITOS / PERSISTENCIA ---

    private fun loadFavoriteStatus() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val favoriteIdsJson = prefs.getString(FAVORITES_KEY, "[]")

        // ✅ CORREGIDO: Manejo de tipos para GSON
        val type = object : TypeToken<Set<String>>() {}.type
        val favoriteIds: Set<String> = Gson().fromJson(favoriteIdsJson, type) ?: emptySet()

        masterOfertasList.forEach { oferta ->
            oferta.isFavorite = favoriteIds.contains(oferta.id)
        }
    }

    private fun saveFavoriteStatus() {
        val favoriteIds = masterOfertasList.filter { it.isFavorite }.map { it.id }.toSet()
        val favoriteIdsJson = Gson().toJson(favoriteIds)

        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(FAVORITES_KEY, favoriteIdsJson)
            .apply()
    }

    private fun toggleFavorite(oferta: Oferta) {
        oferta.isFavorite = !oferta.isFavorite
        saveFavoriteStatus()
    }

    private fun updateFavoriteButtonAppearance() {
        if (isShowingFavorites) {
            binding.btnFavoritos.text = "Todos"
        } else {
            binding.btnFavoritos.text = "Favoritos ❤️"
        }
    }

    // --- LÓGICA DE FILTRADO ---
    private fun applyFilters() {
        var filteredList = masterOfertasList.toList() // Copia de la lista

        // 1. Filtrar por Favoritos
        if (isShowingFavorites) {
            filteredList = filteredList.filter { it.isFavorite }
        }

        // 2. Filtrar por Supermercado
        currentSupermercadoFilter?.let { filter ->
            filteredList = filteredList.filter { it.supermercado == filter }
        }

        // 3. Filtrar por Día
        currentDiaFilter?.let { filter ->
            filteredList = filteredList.filter { it.diasValidos.contains(filter) }
        }

        // Actualizar el RecyclerView con la lista filtrada
        adapter.updateList(filteredList)

        // Actualizar el título si solo se muestra un día (opcional)
        // binding.tvDayTitle.text = if (currentDiaFilter != null) "Día $currentDiaFilter" else "Todas las Ofertas"
    }

    // --- GENERACIÓN DE DATOS (Con URL y correcciones de null) ---
    private fun createDummyData(): List<Oferta> {
        return listOf(
            // 1. Jumbo / Banco Ciudad
            Oferta(
                id = UUID.randomUUID().toString(),
                supermercado = "Jumbo",
                porcentajeDescuento = "30%",
                descripcionCorta = "En un pago con tarjetas de crédito Visa, Mastercard y Cabal",
                descripcionLarga = "De descuento.",
                diasValidos = listOf("Lunes"),
                logoBancoResId = R.drawable.banco_ciudad_logo,
                logoTarjetaResId = R.drawable.jumbo_logo,
                urlOferta = "https://www.jumbo.com.ar/",
                bancoCuotasResId = null,
                bancoCuotas2ResId = null
            ),


            Oferta(
                id = UUID.randomUUID().toString(),
                supermercado = "Carrefour",
                porcentajeDescuento = "15%",
                descripcionCorta = "Monto Mínimo: \$30.000 - Tope: \$10.000.",
                descripcionLarga = "De Descuento.",
                diasValidos = listOf("Martes"),
                logoBancoResId = R.drawable.mercado_pago_logo,
                logoTarjetaResId = R.drawable.carrefour_logo,
                urlOferta = "https://www.carrefour.com.ar/",
                bancoCuotasResId = null,
                bancoCuotas2ResId = null
            ),


            Oferta(
                id = UUID.randomUUID().toString(),
                supermercado = "Dia",
                porcentajeDescuento = "30%",
                descripcionCorta = "De descuento en el acto sin tope pagando con mi Tarjeta Carrefour",
                descripcionLarga = "Válido de Jueves\na domingo.",
                diasValidos = listOf("Jueves", "Viernes", "Sábado", "Domingo"),
                logoBancoResId = R.drawable.carrefour_tarjeta_logo,
                logoTarjetaResId = R.drawable.dia_logo,
                urlOferta = "https://www.supermercadosdia.com.ar/",
                bancoCuotasResId = null,
                bancoCuotas2ResId = null
            ),


            Oferta(
                id = UUID.randomUUID().toString(),
                supermercado = "Coto",
                esCuotas = true,
                numeroCuotas = "3 Cuotas",
                porcentajeDescuento = null, // ✅ CORREGIDO: Debe ser null si es cuotas
                descripcionCorta = "3 cuotas sin interés en toda la compra.",
                descripcionLarga = "Válido de Jueves\na domingo.",
                diasValidos = listOf("Jueves", "Viernes", "Sábado", "Domingo"),
                bancoCuotasResId = R.drawable.coto_logo,
                bancoCuotas2ResId = R.drawable.galicia_logo,
                urlOferta = "https://www.coto.com.ar/",
                logoBancoResId = null,
                logoTarjetaResId = null
            ),


            Oferta(
                id = UUID.randomUUID().toString(),
                supermercado = "Carrefour",
                porcentajeDescuento = "20%",
                descripcionCorta = "Todos los Miércoles con tu Tarjeta Carrefour",
                descripcionLarga = "No acumulable.",
                diasValidos = listOf("Miércoles"),
                logoBancoResId = R.drawable.carrefour_tarjeta_logo,
                logoTarjetaResId = R.drawable.carrefour_logo,
                urlOferta = "https://www.carrefour.com.ar/",
                bancoCuotasResId = null,
                bancoCuotas2ResId = null
            ),

            Oferta(
                id = UUID.randomUUID().toString(),
                supermercado = "Jumbo",
                porcentajeDescuento = "10%",
                descripcionCorta = "Todos los días con banco XYZ",
                descripcionLarga = "Sin tope.",
                diasValidos = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"),
                logoBancoResId = R.drawable.banco_ciudad_logo,
                logoTarjetaResId = R.drawable.jumbo_logo,
                urlOferta = "https://www.jumbo.com.ar/",
                bancoCuotasResId = null,
                bancoCuotas2ResId = null
            )
        )
    }
}