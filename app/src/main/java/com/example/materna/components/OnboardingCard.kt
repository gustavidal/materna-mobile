package com.example.materna.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R

@Composable
fun OnboardingCard(
    imageRes: Int,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 360.dp)
                .aspectRatio(if (compact) 1.25f else 1f)
                .clip(RoundedCornerShape(32.dp))
                .background(colorResource(R.color.pink_beige))
                .padding(if (compact) 16.dp else 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(if (compact) 16.dp else 28.dp))

        Text(
            text = title,
            color = colorResource(R.color.main_text_color),
            fontSize = if (compact) 21.sp else 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(if (compact) 8.dp else 12.dp))

        Text(
            text = description,
            color = colorResource(R.color.secundary_gray),
            fontSize = if (compact) 13.sp else 15.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = if (compact) 18.sp else 22.sp,
            textAlign = TextAlign.Center
        )
    }
}
