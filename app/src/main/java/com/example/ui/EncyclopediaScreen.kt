package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class EnchantmentInfo(
    val name: String,
    val maxLevel: Int,
    val description: String,
    val incompatibilities: List<String> = emptyList(),
    val applicableItems: List<String>
)

val ALL_ENCHANTMENTS = listOf(
    EnchantmentInfo(
        name = "Afiação",
        maxLevel = 5,
        description = "Aumenta o dano causado pelo item.",
        incompatibilities = listOf("Julgamento", "Ruína dos Artrópodes"),
        applicableItems = listOf("Espada", "Machado", "Lança")
    ),
    EnchantmentInfo(
        name = "Afinidade Aquática",
        maxLevel = 1,
        description = "Aumenta a velocidade de mineração dentro d'água.",
        applicableItems = listOf("Capacete")
    ),
    EnchantmentInfo(
        name = "Alcance",
        maxLevel = 3,
        description = "Aumenta a área de ataque da espada.",
        applicableItems = listOf("Espada")
    ),
    EnchantmentInfo(
        name = "Aspecto Flamejante",
        maxLevel = 2,
        description = "Põe fogo no alvo atingido.",
        applicableItems = listOf("Espada", "Maça", "Lança")
    ),
    EnchantmentInfo(
        name = "Carga Rápida",
        maxLevel = 3,
        description = "Diminui o tempo necessário para recarregar a besta.",
        applicableItems = listOf("Besta")
    ),
    EnchantmentInfo(
        name = "Chama",
        maxLevel = 1,
        description = "A flecha ateia fogo nos alvos.",
        applicableItems = listOf("Arco")
    ),
    EnchantmentInfo(
        name = "Condutividade",
        maxLevel = 1,
        description = "Tridente produz trovões em certas condições.",
        incompatibilities = listOf("Correnteza"),
        applicableItems = listOf("Tridente")
    ),
    EnchantmentInfo(
        name = "Correnteza",
        maxLevel = 3,
        description = "Permite usar o tridente como meio de transporte rápido.",
        incompatibilities = listOf("Lealdade", "Condutividade"),
        applicableItems = listOf("Tridente")
    ),
    EnchantmentInfo(
        name = "Densidade",
        maxLevel = 5,
        description = "Aumenta o dano da maça ao cair de alturas maiores.",
        incompatibilities = listOf("Ruptura", "Julgamento", "Ruína dos Artrópodes"),
        applicableItems = listOf("Maça")
    ),
    EnchantmentInfo(
        name = "Durabilidade",
        maxLevel = 3,
        description = "Reduz a chance de um item sofrer dano. Níveis mais altos reduzem ainda mais a chance.",
        applicableItems = listOf("Capacete", "Peitoral", "Calças", "Botas", "Espada", "Machado", "Maça", "Lança", "Arco", "Besta", "Tridente", "Picareta", "Pá", "Vara de Pesca", "Élitros", "Escudo")
    ),
    EnchantmentInfo(
        name = "Eficiência",
        maxLevel = 5,
        description = "Aumenta a velocidade de mineração.",
        applicableItems = listOf("Picareta", "Pá", "Machado")
    ),
    EnchantmentInfo(
        name = "Espinhos",
        maxLevel = 3,
        description = "Reflete alguns dos danos sofridos ao ser atingido, ao custo de reduzir a durabilidade.",
        applicableItems = listOf("Capacete", "Peitoral", "Calças", "Botas")
    ),
    EnchantmentInfo(
        name = "Estocada",
        maxLevel = 3,
        description = "Aumenta o dano da lança. Incompatível com Remendo.",
        incompatibilities = listOf("Remendo"),
        applicableItems = listOf("Lança")
    ),
    EnchantmentInfo(
        name = "Força",
        maxLevel = 5,
        description = "Aumenta o dano de flechas.",
        applicableItems = listOf("Arco")
    ),
    EnchantmentInfo(
        name = "Fortuna",
        maxLevel = 3,
        description = "Aumenta certas quedas de bloco. Níveis mais altos aumentam as chances.",
        incompatibilities = listOf("Toque Suave"),
        applicableItems = listOf("Picareta", "Pá", "Machado")
    ),
    EnchantmentInfo(
        name = "Impacto",
        maxLevel = 2,
        description = "Joga o inimigo para trás (similar ao encantamento de repulsão).",
        applicableItems = listOf("Arco")
    ),
    EnchantmentInfo(
        name = "Infinidade",
        maxLevel = 1,
        description = "O disparo não consome flechas comuns.",
        incompatibilities = listOf("Remendo"),
        applicableItems = listOf("Arco")
    ),
    EnchantmentInfo(
        name = "Isca",
        maxLevel = 3,
        description = "Diminui o tempo de espera até que o peixe morda. Níveis mais altos aumentam a velocidade.",
        applicableItems = listOf("Vara de Pesca")
    ),
    EnchantmentInfo(
        name = "Julgamento",
        maxLevel = 5,
        description = "Aumenta o dano em mortos-vivos.",
        incompatibilities = listOf("Afiação", "Ruína dos Artrópodes"),
        applicableItems = listOf("Espada", "Machado", "Maça", "Lança")
    ),
    EnchantmentInfo(
        name = "Lealdade",
        maxLevel = 3,
        description = "O tridente retorna à mão do jogador. Quanto maior o nível, menor o tempo de espera.",
        incompatibilities = listOf("Correnteza"),
        applicableItems = listOf("Tridente")
    ),
    EnchantmentInfo(
        name = "Maldição do Desaparecimento",
        maxLevel = 1,
        description = "O item irá desaparecer do inventário ao morrer.",
        applicableItems = listOf("Capacete", "Peitoral", "Calças", "Botas", "Espada", "Machado", "Maça", "Lança", "Arco", "Besta", "Tridente", "Picareta", "Pá", "Vara de Pesca", "Élitros", "Escudo")
    ),
    EnchantmentInfo(
        name = "Maldição do Ligamento",
        maxLevel = 1,
        description = "Os itens não podem ser removidos dos espaços de armadura, exceto no modo criativo ou devido à morte ou quebra.",
        applicableItems = listOf("Capacete", "Peitoral", "Calças", "Botas", "Élitros")
    ),
    EnchantmentInfo(
        name = "Passos Gelados",
        maxLevel = 2,
        description = "Transforma a água abaixo do jogador em gelo fosco e evita o dano por ficar sobre bloco de magma.",
        incompatibilities = listOf("Passos Profundos"),
        applicableItems = listOf("Botas")
    ),
    EnchantmentInfo(
        name = "Passos Profundos",
        maxLevel = 3,
        description = "Aumenta a velocidade na água.",
        incompatibilities = listOf("Passos Gelados"),
        applicableItems = listOf("Botas")
    ),
    EnchantmentInfo(
        name = "Penetração",
        maxLevel = 5,
        description = "Tridente causa dano adicional a criaturas que surgem naturalmente no oceano.",
        applicableItems = listOf("Tridente")
    ),
    EnchantmentInfo(
        name = "Perfuração",
        maxLevel = 4,
        description = "Atravessa mais de uma criatura. Disponível apenas em bestas.",
        incompatibilities = listOf("Rajada"),
        applicableItems = listOf("Besta")
    ),
    EnchantmentInfo(
        name = "Peso-pena",
        maxLevel = 4,
        description = "Diminui o dano causado por queda e por pérolas de ender.",
        applicableItems = listOf("Botas")
    ),
    EnchantmentInfo(
        name = "Proteção",
        maxLevel = 4,
        description = "Reduz a maioria dos tipos de dano em 4% por nível.",
        incompatibilities = listOf("Proteção Contra Explosões", "Proteção Contra Fogo", "Proteção Contra Projéteis"),
        applicableItems = listOf("Capacete", "Peitoral", "Calças", "Botas")
    ),
    EnchantmentInfo(
        name = "Proteção Contra Explosões",
        maxLevel = 4,
        description = "Reduz o dano causado por explosões e repulsão.",
        incompatibilities = listOf("Proteção", "Proteção Contra Fogo", "Proteção Contra Projéteis"),
        applicableItems = listOf("Capacete", "Peitoral", "Calças", "Botas")
    ),
    EnchantmentInfo(
        name = "Proteção Contra Fogo",
        maxLevel = 4,
        description = "Reduz o dano causado pelo fogo.",
        incompatibilities = listOf("Proteção", "Proteção Contra Explosões", "Proteção Contra Projéteis"),
        applicableItems = listOf("Capacete", "Peitoral", "Calças", "Botas")
    ),
    EnchantmentInfo(
        name = "Proteção Contra Projéteis",
        maxLevel = 4,
        description = "Reduz os danos do projétil, como flechas, tridents lançados, bolas de fogo de ghast, etc.",
        incompatibilities = listOf("Proteção", "Proteção Contra Explosões", "Proteção Contra Fogo"),
        applicableItems = listOf("Capacete", "Peitoral", "Calças", "Botas")
    ),
    EnchantmentInfo(
        name = "Rajada",
        maxLevel = 1,
        description = "Dispara 3 flechas ao invés de uma, consumindo apenas uma flecha por vez.",
        incompatibilities = listOf("Perfuração"),
        applicableItems = listOf("Besta")
    ),
    EnchantmentInfo(
        name = "Rajada de Vento",
        maxLevel = 3,
        description = "Libera uma rajada de vento ao acertar um golpe com a maça.",
        applicableItems = listOf("Maça")
    ),
    EnchantmentInfo(
        name = "Remendo",
        maxLevel = 1,
        description = "Utiliza a experiência para reparar os itens.",
        incompatibilities = listOf("Infinidade", "Estocada"),
        applicableItems = listOf("Capacete", "Peitoral", "Calças", "Botas", "Espada", "Machado", "Maça", "Lança", "Arco", "Besta", "Tridente", "Picareta", "Pá", "Vara de Pesca", "Élitros", "Escudo")
    ),
    EnchantmentInfo(
        name = "Repulsão",
        maxLevel = 2,
        description = "Aumenta a repulsão ao atingir inimigos.",
        applicableItems = listOf("Espada", "Lança")
    ),
    EnchantmentInfo(
        name = "Respiração",
        maxLevel = 3,
        description = "Aumenta o tempo de respiração abaixo d'água.",
        applicableItems = listOf("Capacete")
    ),
    EnchantmentInfo(
        name = "Ruína dos Artrópodes",
        maxLevel = 5,
        description = "Aumenta o dano e aplica lentidão IV a criaturas artrópodes (Aranha, Aranha da Caverna, Abelha, Traça, Endermite).",
        incompatibilities = listOf("Afiação", "Julgamento"),
        applicableItems = listOf("Espada", "Machado", "Maça", "Lança")
    ),
    EnchantmentInfo(
        name = "Ruptura",
        maxLevel = 4,
        description = "Aumenta o dano da maça contra armaduras.",
        incompatibilities = listOf("Densidade", "Julgamento", "Ruína dos Artrópodes"),
        applicableItems = listOf("Maça")
    ),
    EnchantmentInfo(
        name = "Saque",
        maxLevel = 3,
        description = "Aumenta o saque de criaturas. Níveis mais altos aumentam o saque caído.",
        applicableItems = listOf("Espada", "Lança")
    ),
    EnchantmentInfo(
        name = "Sorte no Mar",
        maxLevel = 3,
        description = "Aumenta a probabilidade de pescar algum tesouro e diminui a chance de pescar lixo.",
        applicableItems = listOf("Vara de Pesca")
    ),
    EnchantmentInfo(
        name = "Toque Suave",
        maxLevel = 1,
        description = "Os blocos são largados em sua forma original.",
        incompatibilities = listOf("Fortuna"),
        applicableItems = listOf("Picareta", "Pá", "Machado")
    ),
    EnchantmentInfo(
        name = "Velocidade das Almas",
        maxLevel = 3,
        description = "Aumenta a velocidade quando caminhando sobre areia das almas e terra das almas, mas danifica as botas com o tempo.",
        applicableItems = listOf("Botas")
    ),
    EnchantmentInfo(
        name = "Passos Furtivos",
        maxLevel = 3,
        description = "Reduz as vibrações emitidas pelo jogador, evitando detectores de som e Wardens.",
        applicableItems = listOf("Calças")
    )
)

fun Int.toRoman(): String = when (this) {
    1 -> "I"; 2 -> "II"; 3 -> "III"; 4 -> "IV"; 5 -> "V"
    else -> this.toString()
}

@Composable
fun EncyclopediaScreen(onBack: () -> Unit) {
    BackHandler { onBack() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedEnchantment by remember { mutableStateOf<EnchantmentInfo?>(null) }

    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) ALL_ENCHANTMENTS
        else ALL_ENCHANTMENTS.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.applicableItems.any { item -> item.contains(searchQuery, ignoreCase = true) }
        }
    }

    if (selectedEnchantment != null) {
        EnchantmentDetailDialog(
            enchantment = selectedEnchantment!!,
            onDismiss = { selectedEnchantment = null }
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(ObsidianBlack.copy(alpha = 0.9f))
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = GoldYellow
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            "Grimório de Encantamentos",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldYellow
                        )
                        Text(
                            "${ALL_ENCHANTMENTS.size} encantamentos no total",
                            fontSize = 12.sp,
                            color = MutedSlateText
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar encantamento ou item...", color = MutedSlateText, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldYellow) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldYellow,
                        unfocusedBorderColor = MutedSlateText.copy(alpha = 0.4f),
                        focusedTextColor = OffWhiteText,
                        unfocusedTextColor = OffWhiteText,
                        cursorColor = GoldYellow,
                        focusedContainerColor = Color(0xFF1A1A2E),
                        unfocusedContainerColor = Color(0xFF1A1A2E)
                    )
                )
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = padding.calculateTopPadding() + 12.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered, key = { it.name }) { enchantment ->
                EnchantmentCard(
                    enchantment = enchantment,
                    onClick = { selectedEnchantment = enchantment }
                )
            }
            if (filtered.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                        Text(
                            "Nenhum encantamento encontrado.",
                            color = MutedSlateText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnchantmentCard(enchantment: EnchantmentInfo, onClick: () -> Unit) {
    val levelText = if (enchantment.maxLevel == 1) "I" else "I–${enchantment.maxLevel.toRoman()}"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1A2E))
            .border(1.dp, GoldYellow.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    enchantment.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = GoldYellow,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GoldYellow.copy(alpha = 0.15f)
                ) {
                    Text(
                        "Nível $levelText",
                        fontSize = 11.sp,
                        color = GoldYellow,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                enchantment.description,
                fontSize = 13.sp,
                color = OffWhiteText.copy(alpha = 0.8f),
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(8.dp))
            // Items chips
            androidx.compose.foundation.layout.FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                enchantment.applicableItems.take(5).forEach { item ->
                    ItemChip(item)
                }
                if (enchantment.applicableItems.size > 5) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MutedSlateText.copy(alpha = 0.15f)
                    ) {
                        Text(
                            "+${enchantment.applicableItems.size - 5}",
                            fontSize = 10.sp,
                            color = MutedSlateText,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            if (enchantment.incompatibilities.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    "✗ ${enchantment.incompatibilities.joinToString(", ")}",
                    fontSize = 11.sp,
                    color = Color(0xFFFF6B6B).copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun ItemChip(name: String) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = DiamondCyan.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, DiamondCyan.copy(alpha = 0.4f))
    ) {
        Text(
            name,
            fontSize = 10.sp,
            color = DiamondCyan,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun EnchantmentDetailDialog(enchantment: EnchantmentInfo, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A2E),
        shape = RoundedCornerShape(16.dp),
        title = {
            Column {
                Text(enchantment.name, color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "Nível máximo: ${enchantment.maxLevel.toRoman()}",
                    color = MutedSlateText,
                    fontSize = 12.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Description
                Column {
                    Text("Descrição", color = GoldYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(enchantment.description, color = OffWhiteText, fontSize = 14.sp, lineHeight = 20.sp)
                }
                // Applicable items
                Column {
                    Text("Aplicável em", color = GoldYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    androidx.compose.foundation.layout.FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        enchantment.applicableItems.forEach { ItemChip(it) }
                    }
                }
                // Incompatibilities
                if (enchantment.incompatibilities.isNotEmpty()) {
                    Column {
                        Text("Incompatível com", color = Color(0xFFFF6B6B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        enchantment.incompatibilities.forEach { incompat ->
                            Text("• $incompat", color = OffWhiteText.copy(alpha = 0.8f), fontSize = 13.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar", color = GoldYellow)
            }
        }
    )
}
