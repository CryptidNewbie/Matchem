package com.cryptidnewbie.matchem.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cryptidnewbie.matchem.R
import com.cryptidnewbie.matchem.data.Card
import com.cryptidnewbie.matchem.data.CardType
import com.cryptidnewbie.matchem.data.GameDifficulty
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    difficulty: GameDifficulty,
    onGameComplete: (moves: Int, timeInSeconds: Int) -> Unit,
    onBackClick: () -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val gameState by viewModel.gameState.collectAsState()

    LaunchedEffect(difficulty) {
        viewModel.startNewGame(difficulty)
    }

    LaunchedEffect(gameState.isGameComplete) {
        if (gameState.isGameComplete && gameState.endTime != null) {
            val timeInSeconds = ((gameState.endTime!! - gameState.startTime) / 1000).toInt()
            delay(1000)
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .background(Color(0xFF333333))
                .padding(8.dp)
        ) {
            if (gameState.isPaused) {
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
                    columns = GridCells.Fixed(gameState.difficulty.columns),
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
    val cardColors = remember {
        listOf(
            Color(0xFFE57373), Color(0xFF81C784), Color(0xFF64B5F6), Color(0xFFFFB74D),
            Color(0xFFBA68C8), Color(0xFF4DB6AC), Color(0xFFE0E0E0), Color(0xFFFFF176)
        )
    }

    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped || card.isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "card_rotation"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = !card.isFlipped && !card.isMatched) {
                onClick()
            }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            // Card back
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bigfoot_ring_floor),
                    contentDescription = "Card back",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            // Card front
            val backgroundColor = if (card.pairId != -1) {
                cardColors.getOrElse(card.pairId % cardColors.size) { Color.LightGray }
            } else {
                Color.Transparent
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
                        Image(
                            painter = painterResource(id = R.drawable.loser_card),
                            contentDescription = "Bad card",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                    CardType.NORMAL -> {
                        card.imageResource?.let { imageRes ->
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = "Card image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
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
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

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
        text = "${stringResource(R.string.time)}: ${String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)}",
        style = MaterialTheme.typography.bodyMedium
    )
}