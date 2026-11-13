package com.example.materna.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.materna.models.responses.InstituicaoProxima
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapaBancosLeite(
    instituicoes: List<InstituicaoProxima>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val mapView = remember(context) {
        Configuration.getInstance().apply {
            userAgentValue = "Materna/1.0 (contato: gustavovidalgva@gmail.com)"
        }

        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)

            controller.setZoom(12.0)
            controller.setCenter(
                GeoPoint(-23.5505, -46.6333)
            )
        }
    }

    DisposableEffect(mapView) {
        mapView.onResume()

        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        update = { mapa ->

            mapa.overlays
                .filterIsInstance<Marker>()
                .toList()
                .forEach { mapa.overlays.remove(it) }

            val pontos = mutableListOf<GeoPoint>()

            instituicoes.forEach { instituicao ->

                val latitude = instituicao.latitude.toDoubleOrNull()
                val longitude = instituicao.longitude.toDoubleOrNull()

                if (
                    latitude != null &&
                    longitude != null &&
                    latitude in -90.0..90.0 &&
                    longitude in -180.0..180.0
                ) {
                    val ponto = GeoPoint(latitude, longitude)
                    pontos.add(ponto)

                    Marker(mapa).apply {
                        position = ponto
                        title = instituicao.nome
                        snippet = "${instituicao.logradouro}, " +
                                "${instituicao.numero} - ${instituicao.bairro}"

                        setAnchor(
                            Marker.ANCHOR_CENTER,
                            Marker.ANCHOR_BOTTOM
                        )

                        mapa.overlays.add(this)
                    }
                }
            }

            when (pontos.size) {
                0 -> Unit

                1 -> {
                    mapa.controller.setZoom(15.0)
                    mapa.controller.setCenter(pontos.first())
                }

                else -> {
                    mapa.post {
                        mapa.zoomToBoundingBox(
                            BoundingBox.fromGeoPoints(pontos),
                            true,
                            60
                        )
                    }
                }
            }

            mapa.invalidate()
        }
    )
}