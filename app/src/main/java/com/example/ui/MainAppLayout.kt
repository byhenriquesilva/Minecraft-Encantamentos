package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun MinecraftBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    // Generate static random positions for small twinkle stars
    val stars = remember {
        List(40) {
            Offset(
                x = Random.nextFloat(),
                y = Random.nextFloat()
            )
        }
    }

    // Gentle infinite animation loop for star twinkling glow
    val transition = rememberInfiniteTransition(label = "stars")
    val alphaMultiplier by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ObsidianBlack,
                        NetheriteDark,
                        Color(0xFF0D0A1C) // Nether background tone
                    )
                )
            )
            .drawBehind {
                // Paint scattered retro-pixel star points
                stars.forEachIndexed { index, starOffset ->
                    val glowFactor = if (index % 3 == 0) alphaMultiplier else 0.7f
                    val starColor = when (index % 4) {
                        0 -> DiamondCyan.copy(alpha = 0.8f * glowFactor)
                        1 -> EnchantmentPurpleNeon.copy(alpha = 0.8f * glowFactor)
                        2 -> ExperienceGreen.copy(alpha = 0.8f * glowFactor)
                        else -> GoldYellow.copy(alpha = 0.6f * glowFactor)
                    }

                    // Draw 4dp square pixel stars
                    val sizePx = if (index % 5 == 0) 8f else 4f
                    drawRect(
                        color = starColor,
                        topLeft = Offset(
                            starOffset.x * size.width,
                            starOffset.y * size.height
                        ),
                        size = androidx.compose.ui.geometry.Size(sizePx, sizePx)
                    )
                }
            },
        content = content
    )
}

@Composable
fun MainAppLayout(viewModel: EnchantmentViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selections by viewModel.uiState.collectAsStateWithLifecycle()
    val saveStatus by viewModel.saveStatus.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        // Portal Background
        MinecraftBackground {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "screen_navigation"
            ) { target ->
                when (target) {
                    is AppScreen.Home -> IntroScreen(onStart = {
                        viewModel.navigateTo(AppScreen.Main)
                    })
                    is AppScreen.Main -> MainScreen(
                        categories = viewModel.categories,
                        selections = selections,
                        onSelectItem = { catId ->
                            viewModel.navigateTo(AppScreen.Detail(catId))
                        },
                        onResetAll = {
                            viewModel.resetAllProgress()
                        },
                        savedScrollIndex = viewModel.savedScrollIndex,
                        savedScrollOffset = viewModel.savedScrollOffset,
                        onSaveScroll = { index, offset ->
                            viewModel.savedScrollIndex = index
                            viewModel.savedScrollOffset = offset
                        }
                    )
                    is AppScreen.Detail -> {
                        val category = viewModel.categories.find { it.id == target.categoryId }
                        if (category != null) {
                            DetailScreen(
                                category = category,
                                checkedEnchantments = selections[target.categoryId] ?: emptySet(),
                                onBack = { viewModel.navigateTo(AppScreen.Main) },
                                onToggle = { enchantName, isChecked ->
                                    viewModel.toggleEnchantment(category.id, enchantName, isChecked)
                                },
                                onSave = {
                                    viewModel.triggerExplicitSaveFeedback(category.id)
                                }
                            )
                        } else {
                            viewModel.navigateTo(AppScreen.Main)
                        }
                    }
                }
            }
        }

        // Beautiful Interactive Magical Popup Alerts
        AnimatedVisibility(
            visible = saveStatus != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 16.dp, start = 20.dp, end = 20.dp)
        ) {
            saveStatus?.let { msg ->
                LaunchedEffect(msg) {
                    delay(3000)
                    viewModel.dismissSaveStatus()
                }

                Surface(
                    color = DarkCardBg,
                    border = BorderStroke(2.dp, ExperienceGreen),
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 8.dp,
                    shadowElevation = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "🧪",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodyMedium,
                                color = OffWhiteText,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        IconButton(
                            onClick = { viewModel.dismissSaveStatus() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fechar aviso",
                                tint = MutedSlateText,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IntroScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Glowing portal animation block
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_scale"
        )

        // Minecraft Enchanting Table Mock Icon / Canvas Drawing
        Box(
            modifier = Modifier
                .size(160.dp)
                .drawBehind {
                    // Draw a neon purple radiating aura
                    drawCircle(
                        Brush.radialGradient(
                            colors = listOf(
                                EnchantmentPurpleNeon.copy(alpha = 0.4f * scale),
                                Color.Transparent
                            )
                        ),
                        radius = size.width * 0.7f
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Draw a highly styled Enchanting book using Pixel matrix
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.logomine),
                    contentDescription = "Enchanting Table",
                    modifier = Modifier.size(160.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title Block with shadow
        Text(
            text = "MINECRAFT",
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = OffWhiteText,
                shadow = Shadow(
                    color = LapisBlue,
                    offset = Offset(4f, 4f),
                    blurRadius = 2f
                )
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = "ENCANTAMENTOS",
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DiamondCyan,
                shadow = Shadow(
                    color = EnchantmentPurpleNeon,
                    offset = Offset(3f, 3f),
                    blurRadius = 1f
                )
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Subtitle
        Text(
            text = "Gerencie seus encantamentos de armadura e ferramentas com precisão lendária.",
            style = MaterialTheme.typography.bodyLarge,
            color = MutedSlateText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Minecraft RPG Style Button with Artistic Flair gradient
        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("comecar_button")
                .shadow(12.dp, RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(ExperienceGreen, DiamondCyan)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = ObsidianBlack
            ),
            contentPadding = PaddingValues(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "ABRIR MESA DE ENCANTAMENTOS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun MainScreen(
    categories: List<CategoryConfig>,
    selections: Map<String, Set<String>>,
    onSelectItem: (String) -> Unit,
    onResetAll: () -> Unit,
    savedScrollIndex: Int = 0,
    savedScrollOffset: Int = 0,
    onSaveScroll: (Int, Int) -> Unit = { _, _ -> }
) {
    // Search and filter capabilities for a pro UX layout
    var searchQuery by remember { mutableStateOf("") }

    // Create grid state with saved scroll position
    val listState = rememberLazyGridState(
        initialFirstVisibleItemIndex = savedScrollIndex,
        initialFirstVisibleItemScrollOffset = savedScrollOffset
    )

    // Save scroll position when it changes
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        onSaveScroll(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset)
    }

    val filteredCategories = remember(searchQuery, categories) {
        if (searchQuery.trim().isEmpty()) {
            categories
        } else {
            categories.filter {
                it.displayName.contains(searchQuery, ignoreCase = true) ||
                        it.enchantments.any { enc -> enc.name.contains(searchQuery, ignoreCase = true) }
            }
        }
    }

    // Calculations of global progress variables
    val totalEnchants = categories.sumOf { it.getMaxSelectableEnchantments() }
    val checkedCount = categories.sumOf { selections[it.id]?.size ?: 0 }
    val overallPercentage = if (totalEnchants > 0) (checkedCount.toFloat() / totalEnchants * 100).toInt() else 0

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(ObsidianBlack.copy(alpha = 0.8f))
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Mesa de Magia",
                            style = MaterialTheme.typography.titleLarge,
                            color = OffWhiteText
                        )
                        Text(
                            text = "Inventário de Sobrevivência",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedSlateText
                        )
                    }

                    // Reset button styled as classic stone anvil icon
                    IconButton(
                        onClick = onResetAll,
                        modifier = Modifier
                            .background(RedstoneRed.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .border(1.dp, RedstoneRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Limpar Progresso",
                            tint = RedstoneRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Beautiful overall level indicators (acts as experience points level!)
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCardBg),
                    border = BorderStroke(1.dp, ExperienceGreen.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Level Orb Indicator
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFF1B3D23), RoundedCornerShape(24.dp))
                                .border(2.dp, ExperienceGreen, RoundedCornerShape(24.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = checkedCount.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                color = ExperienceGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Nível de Progressão",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MutedSlateText
                                )
                                Text(
                                    text = "$overallPercentage%",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = ExperienceGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            // Modern Experience-Bar styling
                            LinearProgressIndicator(
                                progress = { checkedCount.toFloat() / totalEnchants.toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = ExperienceGreen,
                                trackColor = Color(0xFF1B2E1E)
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            // Search Input field representing Command Block coding style
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                placeholder = { Text("Procurar item ou feitiço...", color = MutedSlateText) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MutedSlateText) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EnchantmentPurpleNeon,
                    unfocusedBorderColor = LightGrayBorder,
                    focusedTextColor = OffWhiteText,
                    unfocusedTextColor = OffWhiteText,
                    focusedContainerColor = DarkCardBg.copy(alpha = 0.5f),
                    unfocusedContainerColor = DarkCardBg.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (filteredCategories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🕸️", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Nenhum templo encontrado com esse nome.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MutedSlateText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    state = listState,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize().padding(bottom = 16.dp)
                ) {
                    items(filteredCategories) { cat ->
                        val activeList = selections[cat.id] ?: emptySet()
                        val maxSelectable = cat.getMaxSelectableEnchantments()
                        val percent = if (maxSelectable > 0) {
                            (activeList.size.toFloat() / maxSelectable * 100).toInt()
                        } else {
                            0
                        }
                        val isComplete = percent == 100

                        CategoryCard(
                            category = cat,
                            activeCount = activeList.size,
                            totalCount = maxSelectable,
                            percent = percent,
                            isComplete = isComplete,
                            onClick = { onSelectItem(cat.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: CategoryConfig,
    activeCount: Int,
    totalCount: Int,
    percent: Int,
    isComplete: Boolean,
    onClick: () -> Unit
) {
    // Selection outline glows beautifully when completed
    val animatedBorderColor = if (isComplete) ExperienceGreen else LightGrayBorder
    val activeColorTheme = if (isComplete) ExperienceGreen else DiamondCyan

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .border(
                width = if (isComplete) 1.5.dp else 1.dp,
                color = animatedBorderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkCardBg.copy(alpha = 0.85f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // High fidelity native pixel art representation
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFF24213B), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                MinecraftPixelIcon(
                    itemId = category.id,
                    baseColor = activeColorTheme,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = category.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = OffWhiteText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Text counters
            Text(
                text = if (isComplete) "Completo! ✨" else "$activeCount / $totalCount Ativo",
                style = MaterialTheme.typography.labelLarge,
                color = if (isComplete) ExperienceGreen else MutedSlateText,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Glassmorphic item progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(Color(0xFF1E1D2D), RoundedCornerShape(3.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(percent.toFloat() / 100f)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    activeColorTheme,
                                    if (isComplete) ExperienceGreen else EnchantmentPurpleNeon
                                )
                            ),
                            RoundedCornerShape(3.dp)
                        )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(   
    category: CategoryConfig,
    checkedEnchantments: Set<String>,
    onBack: () -> Unit,
    onToggle: (String, Boolean) -> Unit,
    onSave: () -> Unit
) {
    val totalCount = category.getMaxSelectableEnchantments()
    val activeCount = checkedEnchantments.size
    val percent = if (totalCount > 0) (activeCount.toFloat() / totalCount * 100).toInt() else 0
    val isComplete = percent == 100

    BackHandler { onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.titleLarge,
                        color = OffWhiteText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = OffWhiteText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ObsidianBlack.copy(alpha = 0.8f)
                )
            )
        },
        containerColor = Color.Transparent,
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = ObsidianBlack.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, LightGrayBorder)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Porcentagem",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedSlateText
                        )
                        Text(
                            text = "$percent%",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isComplete) ExperienceGreen else DiamondCyan
                        )
                    }

                    // Save State trigger with premium Artistic Flair styling gradient
                    Button(
                        onClick = onSave,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = ObsidianBlack
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                        modifier = Modifier
                            .testTag("salvar_estado_button")
                            .shadow(12.dp, RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(ExperienceGreen, DiamondCyan)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Text(
                            text = "SALVAR ESTADO",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 80.dp)
        ) {
            // Header Hero slot representing state-of-the-art detail graphic
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .border(
                            1.dp,
                            if (isComplete) ExperienceGreen.copy(alpha = 0.5f) else EnchantmentPurpleNeon.copy(alpha = 0.3f),
                            RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = DarkCardBg.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Drawing icon slot
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .background(Color(0xFF24213B), RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            MinecraftPixelIcon(
                                itemId = category.id,
                                baseColor = if (isComplete) ExperienceGreen else DiamondCyan,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (isComplete) "✨ ITEM COM COROA EXTRAORDINÁRIA ✨" else "Ativar Encantamentos",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isComplete) ExperienceGreen else GoldYellow,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = category.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedSlateText,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom Artistic Flair simulation badges
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(ExperienceGreen.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                    .border(BorderStroke(1.dp, ExperienceGreen.copy(alpha = 0.3f)), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "GAMER PREMIUM",
                                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 9.sp),
                                    color = ExperienceGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(EnchantmentPurpleNeon.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                    .border(BorderStroke(1.dp, EnchantmentPurpleNeon.copy(alpha = 0.3f)), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isComplete) "ENCANTADA" else "SOBREVIVÊNCIA",
                                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 9.sp),
                                    color = EnchantmentPurpleNeon,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress percentage bar inside detail card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progresso Geral",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedSlateText
                            )
                            Text(
                                text = "$activeCount / $totalCount",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isComplete) ExperienceGreen else OffWhiteText,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF1E1D2D))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(percent.toFloat() / 100f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                DiamondCyan,
                                                if (isComplete) ExperienceGreen else EnchantmentPurpleNeon
                                            )
                                        )
                                    )
                            )
                        }

                        // Complete celebration alert
                        if (isComplete) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Surface(
                                color = Color(0xFF1A3320),
                                border = BorderStroke(1.dp, ExperienceGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                               ) {
                                    Text(
                                        text = "⚔️ CONCLUÍDO - ITEM PERFEITO",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = ExperienceGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                               }
                            }
                        }
                    }
                }
            }

            // List Title
            item {
                Text(
                    text = "Encantamentos Disponíveis",
                    style = MaterialTheme.typography.titleMedium,
                    color = OffWhiteText,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            // Encantamentos list
            items(category.enchantments) { enchant ->
                val isChecked = checkedEnchantments.contains(enchant.name)
                
                EnchantmentRow(
                    enchantment = enchant,
                    isChecked = isChecked,
                    onToggle = { isNewChecked ->
                        onToggle(enchant.name, isNewChecked)
                    }
                )
            }

        }
    }
}

@Composable
fun EnchantmentRow(
    enchantment: EnchantmentItem,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isChecked) }
            .shadow(if (isChecked) 6.dp else 2.dp, RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = if (isChecked) ExperienceGreen.copy(alpha = 0.5f) else LightGrayBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .drawBehind {
                if (isChecked) {
                    // Left highlight stripe/indicator mimicking the .border-l-4 in the theme
                    drawRect(
                        color = ExperienceGreen,
                        topLeft = Offset(0f, 0f),
                        size = androidx.compose.ui.geometry.Size(12f, size.height)
                    )
                }
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isChecked) Color(0xFF1E1E2C) else DarkCardBg.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = enchantment.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isChecked) SkillColorFor(enchantment.name) else OffWhiteText
                        )
                    )
                }

                if (enchantment.exclusiveWith != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Exclusivo: não se acumula com ${enchantment.exclusiveWith}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = RedstoneRed.copy(alpha = 0.8f)
                    )
                }
            }

            // Customize checklist checkbox
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isChecked) ExperienceGreen else Color(0xFF1E1D2D))
                    .border(
                        1.5.dp,
                        if (isChecked) Color.White else MutedSlateText,
                        RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isChecked) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selecionado",
                        tint = ObsidianBlack,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// Custom function yielding different enchantment text colors to enhance UI visual scanning read speeds
@Composable
fun SkillColorFor(name: String): Color {
    return when {
        name.contains("PROTEÇÃO") -> DiamondCyan
        name.contains("AFIAÇÃO") || name.contains("FORÇA") -> RedstoneRed
        name.contains("REMENDO") || name.contains("INFINIDADE") -> GoldYellow
        name.contains("TOQUE SUAVE") || name.contains("FORTUNA") -> ExperienceGreen
        else -> EnchantmentPurpleNeon
    }
}
