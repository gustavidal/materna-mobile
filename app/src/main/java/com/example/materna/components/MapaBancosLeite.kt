package com.example.materna.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.materna.R
import com.example.materna.models.responses.InstituicaoProxima
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun MapaBancosLeite(
    instituicoes: List<InstituicaoProxima>,
    localizacaoDoadora: Pair<Double, Double>?,
    onInstituicaoClick: (InstituicaoProxima) -> Unit = {},
    pontosRota: List<GeoPoint> = emptyList(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(localizacaoDoadora) {
        Log.d(
            "MaternaMapa",
            "Localização da doadora: $localizacaoDoadora"
        )
    }

    val mapView = remember(context) {
        Configuration.getInstance().apply {
            userAgentValue = "Materna/1.0 (contato: gustavovidalgva@gmail.com)"
        }

        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)

            controller.setZoom(12.0)
            minZoomLevel = 6.0
            maxZoomLevel = 18.0

            setScrollableAreaLimitLatitude(
                MapView.getTileSystem().maxLatitude,
                MapView.getTileSystem().minLatitude,
                0
            )

            setScrollableAreaLimitLongitude(
                -180.0,
                180.0,
                0
            )

            isHorizontalMapRepetitionEnabled = false
            isVerticalMapRepetitionEnabled = false
        }
    }

    val linhaRota = remember(mapView) {
        Polyline(mapView).apply {
            outlinePaint.color = ContextCompat.getColor(
                context,
                R.color.route_blue
            )
            outlinePaint.strokeWidth = 8f
        }
    }

    // Acompanha o ciclo de vida da tela.
    DisposableEffect(mapView, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        if (
            lifecycleOwner.lifecycle.currentState
                .isAtLeast(Lifecycle.State.RESUMED)
        ) {
            mapView.onResume()
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onPause()
        }
    }

    DisposableEffect(mapView) {
        onDispose {
            mapView.onDetach()
        }
    }

    LaunchedEffect(mapView, localizacaoDoadora, instituicoes) {
        // Remove os marcadores anteriores para evitar duplicações.
        mapView.overlays
            .filterIsInstance<Marker>()
            .toList()
            .forEach { mapView.overlays.remove(it) }

        val pontosBancos = mutableListOf<GeoPoint>()
        val todosOsPontos = mutableListOf<GeoPoint>()

        // Marcador da casa da doadora.
        localizacaoDoadora?.let { (latitude, longitude) ->
            if (
                latitude.isFinite() &&
                longitude.isFinite() &&
                latitude in -90.0..90.0 &&
                longitude in -180.0..180.0
            ) {
                val pontoDoadora = GeoPoint(latitude, longitude)
                todosOsPontos.add(pontoDoadora)

                val marcadorDoadora = Marker(mapView).apply {
                    position = pontoDoadora
                    title = "Meu endereço cadastrado"
                    snippet = "Localização aproximada da doadora"

                    icon = ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_pin_doadora
                    )?.mutate()

                    setAnchor(
                        Marker.ANCHOR_CENTER,
                        Marker.ANCHOR_BOTTOM
                    )
                }

                mapView.overlays.add(marcadorDoadora)
            }
        }

        // Marcadores dos bancos de leite.
        instituicoes.forEach { instituicao ->
            val latitude = instituicao.latitude.toDoubleOrNull()
            val longitude = instituicao.longitude.toDoubleOrNull()

            if (
                latitude != null &&
                longitude != null &&
                latitude.isFinite() &&
                longitude.isFinite() &&
                latitude in -90.0..90.0 &&
                longitude in -180.0..180.0
            ) {
                val ponto = GeoPoint(latitude, longitude)
                pontosBancos.add(ponto)
                todosOsPontos.add(ponto)

                val marcadorBanco = Marker(mapView).apply {
                    position = ponto
                    title = instituicao.nome
                    snippet = "${instituicao.logradouro}, " +
                            "${instituicao.numero} - ${instituicao.bairro}"

                    icon = ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_pin_banco
                    )?.mutate()

                    setAnchor(
                        Marker.ANCHOR_CENTER,
                        Marker.ANCHOR_BOTTOM
                    )

                    setOnMarkerClickListener { marker, _ ->
                        onInstituicaoClick(instituicao)
                        true
                    }
                }

                mapView.overlays.add(marcadorBanco)
            }
        }

        // Define a posição inicial da câmera.
        localizacaoDoadora?.let { (latitude, longitude) ->
            if (
                latitude.isFinite() &&
                longitude.isFinite() &&
                latitude in -90.0..90.0 &&
                longitude in -180.0..180.0
            ) {
                mapView.controller.setCenter(
                    GeoPoint(latitude, longitude)
                )
            }
        }

        // Define o enquadramento do mapa.
        when {
            todosOsPontos.size > 1 -> {
                mapView.post {
                    if (mapView.width > 0 && mapView.height > 0) {
                        mapView.zoomToBoundingBox(
                            BoundingBox.fromGeoPoints(todosOsPontos),
                            true,
                            60
                        )
                    }
                }
            }

            todosOsPontos.size == 1 -> {
                mapView.controller.setZoom(15.0)
                mapView.controller.setCenter(todosOsPontos.first())
            }
        }

        mapView.invalidate()
    }

    LaunchedEffect(mapView, pontosRota) {
        mapView.overlays.remove(linhaRota)

        if (pontosRota.size >= 2) {
            linhaRota.setPoints(pontosRota)
            mapView.overlays.add(0, linhaRota)
        }

        mapView.invalidate()
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize()
    )
}