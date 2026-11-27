package com.example.materna.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.materna.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.min

private const val TAMANHO_SAIDA = 800
private const val LADO_MAXIMO_DECODIFICACAO = 2000

/**
 * Tela de corte quadrado da foto de perfil.
 * Arraste para posicionar e use pinça para dar zoom. O recorte é circular
 * apenas visualmente; o arquivo final é um JPEG quadrado de 800x800.
 */
@Composable
fun FotoPerfilCropDialog(
    uri: Uri,
    onConfirmar: (Uri) -> Unit,
    onCancelar: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var bitmap by remember(uri) { mutableStateOf<Bitmap?>(null) }
    var erroCarregar by remember(uri) { mutableStateOf(false) }
    var salvando by remember { mutableStateOf(false) }

    var escala by remember(uri) { mutableFloatStateOf(1f) }
    var deslocamento by remember(uri) { mutableStateOf(Offset.Zero) }
    var tamanhoViewport by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(uri) {
        bitmap = withContext(Dispatchers.IO) { carregarBitmap(context, uri) }
        erroCarregar = bitmap == null
    }

    // Quando a imagem e a área de corte estão prontas, começa preenchendo o quadrado
    LaunchedEffect(bitmap, tamanhoViewport) {
        val bmp = bitmap
        if (bmp != null && tamanhoViewport > 0f) {
            escala = tamanhoViewport / min(bmp.width, bmp.height).toFloat()
            deslocamento = Offset.Zero
        }
    }

    fun escalaMinima(bmp: Bitmap): Float =
        tamanhoViewport / min(bmp.width, bmp.height).toFloat()

    fun limitarDeslocamento(bmp: Bitmap, esc: Float, off: Offset): Offset {
        val larg = bmp.width * esc
        val alt = bmp.height * esc
        val maxX = max(0f, (larg - tamanhoViewport) / 2f)
        val maxY = max(0f, (alt - tamanhoViewport) / 2f)
        return Offset(
            off.x.coerceIn(-maxX, maxX),
            off.y.coerceIn(-maxY, maxY)
        )
    }

    Dialog(
        onDismissRequest = { if (!salvando) onCancelar() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = !salvando,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Ajustar foto",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Arraste e use dois dedos para ampliar",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .onSizeChanged { tamanhoViewport = it.width.toFloat() }
                        .clipToBounds(),
                    contentAlignment = Alignment.Center
                ) {
                    val bmp = bitmap

                    when {
                        erroCarregar -> Text(
                            text = "Não foi possível abrir a imagem.",
                            color = Color.White
                        )

                        bmp == null -> CircularProgressIndicator(
                            color = colorResource(R.color.main_pink)
                        )

                        else -> Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(bmp) {
                                    detectTransformGestures { _, pan, zoom, _ ->
                                        if (tamanhoViewport <= 0f) return@detectTransformGestures

                                        val minimo = escalaMinima(bmp)
                                        val novaEscala = (escala * zoom)
                                            .coerceIn(minimo, minimo * 5f)

                                        escala = novaEscala
                                        deslocamento = limitarDeslocamento(
                                            bmp,
                                            novaEscala,
                                            deslocamento + pan
                                        )
                                    }
                                }
                        ) {
                            val larg = bmp.width * escala
                            val alt = bmp.height * escala
                            val topoEsq = Offset(
                                (size.width - larg) / 2f + deslocamento.x,
                                (size.height - alt) / 2f + deslocamento.y
                            )

                            drawImage(
                                image = bmp.asImageBitmap(),
                                srcOffset = IntOffset.Zero,
                                srcSize = IntSize(bmp.width, bmp.height),
                                dstOffset = IntOffset(
                                    topoEsq.x.toInt(),
                                    topoEsq.y.toInt()
                                ),
                                dstSize = IntSize(larg.toInt(), alt.toInt())
                            )

                            // Escurece o que fica fora do círculo
                            val raio = size.width / 2f
                            val mascara = Path().apply {
                                addRect(androidx.compose.ui.geometry.Rect(Offset.Zero, size))
                            }
                            val circulo = Path().apply {
                                addOval(
                                    androidx.compose.ui.geometry.Rect(
                                        center = Offset(size.width / 2f, size.height / 2f),
                                        radius = raio
                                    )
                                )
                            }
                            val fora = Path.combine(PathOperation.Difference, mascara, circulo)

                            drawPath(fora, Color.Black.copy(alpha = 0.6f))
                            drawCircle(
                                color = Color.White,
                                radius = raio,
                                center = Offset(size.width / 2f, size.height / 2f),
                                style = Stroke(width = 3f)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancelar,
                        enabled = !salvando,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text("Cancelar", color = Color.White)
                    }

                    Button(
                        onClick = {
                            val bmp = bitmap ?: return@Button
                            if (tamanhoViewport <= 0f) return@Button

                            salvando = true

                            scope.launch {
                                val resultado = withContext(Dispatchers.IO) {
                                    recortarESalvar(
                                        context = context,
                                        origem = bmp,
                                        escala = escala,
                                        deslocamento = deslocamento,
                                        tamanhoViewport = tamanhoViewport
                                    )
                                }

                                salvando = false

                                if (resultado != null) {
                                    onConfirmar(resultado)
                                } else {
                                    erroCarregar = true
                                }
                            }
                        },
                        enabled = bitmap != null && !salvando,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.main_pink)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        if (salvando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text("Usar foto", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

/** Decodifica a imagem respeitando a rotação EXIF (API 28+) e limitando o tamanho. */
private fun carregarBitmap(context: android.content.Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)

            ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                val maior = max(info.size.width, info.size.height)

                if (maior > LADO_MAXIMO_DECODIFICACAO) {
                    val fator = LADO_MAXIMO_DECODIFICACAO.toFloat() / maior
                    decoder.setTargetSize(
                        (info.size.width * fator).toInt(),
                        (info.size.height * fator).toInt()
                    )
                }
            }
        } else {
            val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }

            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, limites)
            }

            var amostra = 1
            while (max(limites.outWidth, limites.outHeight) / amostra > LADO_MAXIMO_DECODIFICACAO) {
                amostra *= 2
            }

            val opcoes = BitmapFactory.Options().apply { inSampleSize = amostra }

            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, opcoes)
            }
        }
    } catch (e: Exception) {
        android.util.Log.e("FotoCrop", "Erro ao carregar imagem", e)
        null
    }
}

private fun recortarESalvar(
    context: android.content.Context,
    origem: Bitmap,
    escala: Float,
    deslocamento: Offset,
    tamanhoViewport: Float
): Uri? {
    return try {
        val larg = origem.width * escala
        val alt = origem.height * escala

        val topoEsqX = (tamanhoViewport - larg) / 2f + deslocamento.x
        val topoEsqY = (tamanhoViewport - alt) / 2f + deslocamento.y

        val lado = (tamanhoViewport / escala).toInt().coerceAtMost(min(origem.width, origem.height))
        val x = (-topoEsqX / escala).toInt().coerceIn(0, origem.width - lado)
        val y = (-topoEsqY / escala).toInt().coerceIn(0, origem.height - lado)

        val recorte = Bitmap.createBitmap(origem, x, y, lado, lado)
        val final = Bitmap.createScaledBitmap(recorte, TAMANHO_SAIDA, TAMANHO_SAIDA, true)

        val pasta = File(context.cacheDir, "fotos_perfil").apply { mkdirs() }
        val arquivo = File(pasta, "perfil_${System.currentTimeMillis()}.jpg")

        FileOutputStream(arquivo).use {
            final.compress(Bitmap.CompressFormat.JPEG, 90, it)
        }

        Uri.fromFile(arquivo)
    } catch (e: Exception) {
        android.util.Log.e("FotoCrop", "Erro ao recortar imagem", e)
        null
    }
}
