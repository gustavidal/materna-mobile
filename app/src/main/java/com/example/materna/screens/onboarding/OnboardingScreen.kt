package com.example.materna.screens.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.materna.R
import com.example.materna.components.OnboardingCard
import com.example.materna.data.local.MaternaDatabaseProvider
import kotlin.math.abs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val LAST_STEP = 2

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val settingsDao = remember {
        MaternaDatabaseProvider.getDatabase(context).appSettingsDao()
    }

    val images =
        listOf(
            R.drawable.img_onboarding1,
            R.drawable.img_onboarding2,
            R.drawable.img_onboarding3,
        )

    val titles =
        listOf(
            "Sua doação pode ajudar",
            "Entenda cada etapa",
            "Encontre um serviço",
        )

    val descriptions =
        listOf(
            "O leite humano é essencial para bebês prematuros. Uma única doação pode alimentar vários recém-nascidos e salvar vidas.",
            "O aplicativo acompanha você durante todo o processo, com orientações claras sobre higiene, coleta e armazenamento.",
            "Localize bancos de leite e serviços de coleta próximos de você em poucos toques.",
        )

    fun completeOnboarding() {
        scope.launch {
            withContext(Dispatchers.IO) {
                settingsDao.saveSettings(
                    com.example.materna.data.local.AppSettings(
                        onboardingCompleted = true,
                    ),
                )
            }

            navController.navigate("login") {
                popUpTo("onboarding") {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    val currentStep by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2

            layoutInfo.visibleItemsInfo
                .minByOrNull { item ->
                    abs(
                        (item.offset + item.size / 2) - viewportCenter,
                    )
                }
                ?.index
                ?.coerceIn(0, LAST_STEP) ?: 0
        }
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize().background(colorResource(R.color.background_cream)),
    ) {
        val compactHeight = maxHeight < 700.dp
        val horizontalPadding = if (maxWidth < 360.dp) 20.dp else 32.dp

        val availableCardWidth = maxWidth - horizontalPadding * 2

        val cardWidth = availableCardWidth * 0.78f

        val sidePadding = (availableCardWidth - cardWidth) / 2

        Text(
            text = "Pular",
            fontSize = 14.sp,
            color = colorResource(R.color.secundary_gray),
            fontWeight = FontWeight.SemiBold,
            modifier =
                Modifier.align(Alignment.TopEnd).padding(top = 18.dp, end = 26.dp).clickable {
                    completeOnboarding()
                },
        )

        Column(
            modifier =
                Modifier.align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(
                        vertical = if (compactHeight) 8.dp else 16.dp,
                    ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding =
                    PaddingValues(
                        horizontal = horizontalPadding + sidePadding,
                    ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                flingBehavior =
                    rememberSnapFlingBehavior(
                        lazyListState = listState,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                itemsIndexed(images) { index, image ->
                    OnboardingCard(
                        imageRes = image,
                        title = titles[index],
                        description = descriptions[index],
                        modifier = Modifier.width(cardWidth),
                        compact = compactHeight,
                    )
                }
            }
        }

        Column(
            modifier =
                Modifier.align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp,
                        vertical = if (compactHeight) 12.dp else 20.dp,
                    ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.height(24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(3) { index ->
                    val selected = currentStep == index

                    val indicatorWidth by
                        animateDpAsState(
                            targetValue = if (selected) 18.dp else 8.dp,
                            animationSpec = tween(250),
                            label = "pageIndicator$index",
                        )

                    Box(
                        modifier =
                            Modifier.padding(horizontal = 4.dp)
                                .width(indicatorWidth)
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selected) {
                                        colorResource(R.color.main_pink)
                                    } else {
                                        colorResource(R.color.background_cream)
                                    },
                                ),
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(
                        if (compactHeight) 8.dp else 12.dp,
                    ),
            )

            Button(
                onClick = {
                    if (currentStep < LAST_STEP) {
                        scope.launch {
                            listState.animateScrollToItem(
                                currentStep + 1,
                            )
                        }
                    } else {
                        completeOnboarding()
                    }
                },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.main_pink),
                    ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(54.dp),
            ) {
                Text(
                    text =
                        if (currentStep == LAST_STEP) {
                            "Começar"
                        } else {
                            "Continuar"
                        },
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
