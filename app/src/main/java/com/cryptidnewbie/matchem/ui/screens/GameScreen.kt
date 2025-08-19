package com.cryptidnewbie.matchem.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cryptidnewbie.matchem.R
import com.cryptidnewbie.matchem.data.Card
import com.cryptidnewbie.matchem.data.CardType
import com.cryptidnewbie.matchem.data.GameDifficulty
import com.cryptidnewbie.matchem.ui.theme.CardBack
import com.cryptidnewbie.matchem.ui.theme.CardFront
import com.cryptidnewbie.matchem.ui.theme.MatchedCard
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    difficulty: GameDifficulty,
    onGameComplete: (moves: Int, timeInSeconds: Int) -> Unit,
    onBackClick: () -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    val context = LocalContext.current
    
    LaunchedEffect(difficulty) {
        viewModel.startNewGame(difficulty)
    }
    
    LaunchedEffect(gameState.isGameComplete) {
        if (gameState.isGameComplete && gameState.endTime != null) {
            val timeInSeconds = ((gameState.endTime - gameState.startTime) / 1000).toInt()
            delay(1000) // Brief delay to show final state
            onGameComplete(gameState.moves, timeInSeconds)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("${stringResource(R.string.moves)}: ${gameState.moves}")
                        Text("${stringResource(R.string.matches)}: ${gameState.matches}/${gameState.difficulty.pairs}")
                        GameTimer(startTime = gameState.startTime, isRunning = !gameState.isPaused && !gameState.isGameComplete)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { 
                            if (gameState.isPaused) viewModel.resumeGame() else viewModel.pauseGame()
                        }
                    ) {
                        Icon(
                            Icons.Default.Pause, 
                            contentDescription = if (gameState.isPaused) "Resume" else stringResource(R.string.pause)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            if (gameState.isPaused) {
                // Pause overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card {
                        Text(
                            text = stringResource(R.string.game_paused),
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(difficulty.columns),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(gameState.cards) { card ->
                        GameCard(
                            card = card,
                            onClick = { viewModel.flipCard(card.id) },
                            modifier = Modifier.aspectRatio(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameCard(
    card: Card,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFlipping by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped || card.isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        finishedListener = { isFlipping = false },
        label = "card_rotation"
    )
    
    LaunchedEffect(card.isFlipped, card.isMatched) {
        if (card.isFlipped || card.isMatched) {
            isFlipping = true
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = !card.isFlipped && !card.isMatched && !isFlipping) { 
                onClick()
            }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            // Card back - show BigfootRingFloor image
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.BigfootRingFloor),
                    contentDescription = "Card back",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            // Card front
            val backgroundColor = when {
                card.isMatched -> MatchedCard
                card.type == CardType.BAD_CARD -> MaterialTheme.colorScheme.error
                else -> CardFront
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .graphicsLayer { rotationY = 180f },
                contentAlignment = Alignment.Center
            ) {
                when (card.type) {
                    CardType.BAD_CARD -> {
                        Text(
                            text = "💀",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    CardType.NORMAL -> {
                        // Show the actual card image
                        card.imageResource?.let { imageRes ->
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = "Card image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        } ?: run {
                            // Fallback if image resource is null
                            Text(
                                text = "${card.pairId + 1}",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameTimer(
    startTime: Long,
    isRunning: Boolean
) {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    
    LaunchedEffect(isRunning) {
        while (isRunning) {
            currentTime = System.currentTimeMillis()
            delay(1000)
        }
    }
    
    val elapsed = if (startTime > 0) {
        ((currentTime - startTime) / 1000).toInt()
    } else 0
    
    val minutes = elapsed / 60
    val seconds = elapsed % 60
    
    Text(
        text = "${stringResource(R.string.time)}: ${String.format("%02d:%02d", minutes, seconds)}",
        style = MaterialTheme.typography.bodyMedium
    )
}