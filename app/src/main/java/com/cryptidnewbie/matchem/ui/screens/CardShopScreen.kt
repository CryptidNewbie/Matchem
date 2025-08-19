package com.cryptidnewbie.matchem.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cryptidnewbie.matchem.R
import com.cryptidnewbie.matchem.data.CardBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardShopScreen(
    onBackClick: () -> Unit
) {
    val availableCardBacks = remember {
        listOf(
            CardBack("default", "Classic", android.R.drawable.ic_dialog_info, true, "Free"),
            CardBack("red", "Red Pattern", android.R.drawable.ic_dialog_alert, false, "$0.99"),
            CardBack("blue", "Blue Pattern", android.R.drawable.ic_dialog_info, false, "$0.99"),
            CardBack("green", "Green Pattern", android.R.drawable.ic_menu_compass, false, "$0.99"),
            CardBack("purple", "Purple Pattern", android.R.drawable.ic_menu_gallery, false, "$0.99")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.card_shop)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Customize your card backs",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableCardBacks) { cardBack ->
                    CardBackItem(
                        cardBack = cardBack,
                        onPurchase = { /* TODO: Handle purchase */ },
                        onEquip = { /* TODO: Handle equip */ }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* TODO: Handle unlock all */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.unlock_all))
                        Text("$2.99", style = MaterialTheme.typography.bodySmall)
                    }
                }
                
                OutlinedButton(
                    onClick = { /* TODO: Handle restore purchases */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.restore_purchases))
                }
            }
        }
    }
}

@Composable
fun CardBackItem(
    cardBack: CardBack,
    onPurchase: () -> Unit,
    onEquip: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card preview placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎴",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            
            Text(
                text = cardBack.name,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            when {
                cardBack.isOwned && cardBack.id == "default" -> {
                    FilledTonalButton(
                        onClick = onEquip,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.equipped))
                    }
                }
                cardBack.isOwned -> {
                    Button(
                        onClick = onEquip,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Equip")
                    }
                }
                else -> {
                    OutlinedButton(
                        onClick = onPurchase,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.purchase))
                            Text(cardBack.price, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}