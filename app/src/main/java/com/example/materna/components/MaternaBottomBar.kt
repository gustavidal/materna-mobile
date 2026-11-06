package com.example.materna.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.materna.R

@Composable
fun MaternaBottomBar(
    navController: NavController
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value

    val currentRoute = navBackStackEntry?.destination?.route

    val mainPink = colorResource(R.color.main_pink)
    val secondaryGray = colorResource(R.color.secundary_gray)

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = androidx.compose.ui.unit.Dp.Hairline
    ) {
        val items = listOf(
            Triple("dashboard", "Início",   painterResource(R.drawable.ic_full_house)),
            Triple("jornada",   "Jornada",  painterResource(R.drawable.ic_full_route)),
            Triple("servicos",  "Serviços", painterResource(R.drawable.ic_full_pin_location)),
            Triple("perfil",    "Perfil",   painterResource(R.drawable.ic_full_profile))
        )

        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    if (currentRoute != route) {
                        navController.navigate(route)
                    }
                },
                icon = {
                    Icon(
                        painter = icon,
                        contentDescription = label,
                        modifier = Modifier
                            .size(22.dp)
                    )
                },
                label = {
                    Text(text = label)
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = mainPink,
                    selectedTextColor = mainPink,
                    indicatorColor = colorResource(R.color.pink_beige),
                    unselectedIconColor = secondaryGray,
                    unselectedTextColor = secondaryGray
                )
            )
        }
    }
}
