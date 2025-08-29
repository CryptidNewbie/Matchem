package com.cryptidnewbie.matchem.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import com.cryptidnewbie.matchem.R
import com.cryptidnewbie.matchem.ui.ads.BannerAdView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onPlayClick: () -> Unit,
    onLeaderboardsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCardShopClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(R.drawable.background_main),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Foreground UI
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                )
            },
            bottomBar = {
                BannerAdView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )
            },
            containerColor = Color.Transparent // <-- THIS LINE MAKES THE BACKGROUND VISIBLE!
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 14.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cryptid_md_logo_blue_outline),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(80.dp)
                                .padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Welcome to Match'em!",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Test your memory by matching pairs of cards",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ElevatedButton(
                            onClick = onPlayClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.play),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        FilledTonalButton(
                            onClick = onLeaderboardsClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Leaderboard, contentDescription = "Leaderboards")
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.leaderboards),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        FilledTonalButton(
                            onClick = onCardShopClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Card Shop")
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.card_shop),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        FilledTonalButton(
                            onClick = onSettingsClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.settings),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}