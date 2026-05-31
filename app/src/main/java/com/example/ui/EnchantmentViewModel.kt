package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.EnchantmentRepository
import com.example.data.ItemEnchantment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Screen navigation states
sealed class AppScreen {
    object Home : AppScreen()
    object Main : AppScreen()
    data class Detail(val categoryId: String) : AppScreen()
}

// Enchantment option model with mutual exclusivity logic
data class EnchantmentItem(
    val name: String,
    val exclusiveWith: String? = null,
    val exclusivityGroup: String? = null  // For grouped exclusivities (e.g., multiple protection types)
)

// Main category configuration for items
data class CategoryConfig(
    val id: String,
    val displayName: String,
    val description: String,
    val emoji: String,
    val enchantments: List<EnchantmentItem>
) {
    // Calculate the maximum number of enchantments that can actually be equipped
    // considering exclusivity groups and mutual exclusivity constraints
    fun getMaxSelectableEnchantments(): Int {
        val processedGroups = mutableSetOf<String>()
        val processedExclusive = mutableSetOf<String>()
        var count = 0

        for (enchantment in enchantments) {
            // Skip if already processed as part of an exclusivity group
            if (enchantment.exclusivityGroup != null && enchantment.exclusivityGroup in processedGroups) {
                continue
            }
            
            // Skip if already processed as part of an exclusive pair
            if (enchantment.exclusiveWith != null && enchantment.name in processedExclusive) {
                continue
            }

            // Mark group as processed
            if (enchantment.exclusivityGroup != null) {
                processedGroups.add(enchantment.exclusivityGroup)
            }

            // Mark exclusive pair as processed
            if (enchantment.exclusiveWith != null) {
                processedExclusive.add(enchantment.name)
                processedExclusive.add(enchantment.exclusiveWith)
            }

            count++
        }

        return count
    }
}

class EnchantmentViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize Room Database
    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "minecraft_encantamentos_db"
    ).build()

    private val repository = EnchantmentRepository(db.dao())

    // Flow for raw user selections fetched from the local DB
    val dbSelections: StateFlow<List<ItemEnchantment>> = repository.allEnchantments
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current navigation state
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Home)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Temporary checked states for the currently selected item (allows smooth animation, saving on tick or button press)
    private val _tempSelections = MutableStateFlow<Map<String, Set<String>>>(emptyMap())
    val tempSelections: StateFlow<Map<String, Set<String>>> = _tempSelections.asStateFlow()

    // Status notifications/toasts customized for Minecraft enchanting vibe
    private val _saveStatus = MutableStateFlow<String?>(null)
    val saveStatus: StateFlow<String?> = _saveStatus.asStateFlow()

    // Scroll state persistence for MainScreen
    var savedScrollIndex by mutableStateOf(0)
    var savedScrollOffset by mutableStateOf(0)

    // Definition of items and available enchantments matching the complete list provided
    val categories = listOf(
        // ARMOR PIECES
        CategoryConfig(
            id = "CAPACETE",
            displayName = "Capacete",
            description = "Proteção de cabeça com respiração aquática e capacidades sensoriais.",
            emoji = "🪖",
            enchantments = listOf(
                EnchantmentItem("PROTEÇÃO 4", exclusivityGroup = "protecao_capacete"),
                EnchantmentItem("PROTEÇÃO CONTRA PROJÉTEIS 4", exclusivityGroup = "protecao_capacete"),
                EnchantmentItem("PROTEÇÃO CONTRA FOGO 4", exclusivityGroup = "protecao_capacete"),
                EnchantmentItem("PROTEÇÃO CONTRA EXPLOSÕES 4", exclusivityGroup = "protecao_capacete"),
                EnchantmentItem("INQUEBRÁVEL 3"),
                EnchantmentItem("RESPIRAÇÃO 3"),
                EnchantmentItem("AFINIDADE AQUÁTICA"),
                EnchantmentItem("ESPINHOS 3"),
                EnchantmentItem("REMENDO")
            )
        ),
        CategoryConfig(
            id = "PEITORAL",
            displayName = "Peitoral",
            description = "Proteção central pesada para torso e costas.",
            emoji = "👕",
            enchantments = listOf(
                EnchantmentItem("PROTEÇÃO 4", exclusivityGroup = "protecao_peitoral"),
                EnchantmentItem("PROTEÇÃO CONTRA PROJÉTEIS 4", exclusivityGroup = "protecao_peitoral"),
                EnchantmentItem("PROTEÇÃO CONTRA FOGO 4", exclusivityGroup = "protecao_peitoral"),
                EnchantmentItem("PROTEÇÃO CONTRA EXPLOSÕES 4", exclusivityGroup = "protecao_peitoral"),
                EnchantmentItem("INQUEBRÁVEL 3"),
                EnchantmentItem("ESPINHOS 3"),
                EnchantmentItem("REMENDO")
            )
        ),
        CategoryConfig(
            id = "CALÇAS",
            displayName = "Calças",
            description = "Proteção para pernas com mobilidade e sigilo.",
            emoji = "👖",
            enchantments = listOf(
                EnchantmentItem("PROTEÇÃO 4", exclusivityGroup = "protecao_calcas"),
                EnchantmentItem("PROTEÇÃO CONTRA PROJÉTEIS 4", exclusivityGroup = "protecao_calcas"),
                EnchantmentItem("PROTEÇÃO CONTRA FOGO 4", exclusivityGroup = "protecao_calcas"),
                EnchantmentItem("PROTEÇÃO CONTRA EXPLOSÕES 4", exclusivityGroup = "protecao_calcas"),
                EnchantmentItem("INQUEBRÁVEL 3"),
                EnchantmentItem("ESPINHOS 3"),
                EnchantmentItem("PASSOS FURTIVOS 3"),
                EnchantmentItem("REMENDO")
            )
        ),
        CategoryConfig(
            id = "BOTAS",
            displayName = "Botas",
            description = "Proteção para pés com queda amortecida e locomoção especial.",
            emoji = "🥾",
            enchantments = listOf(
                EnchantmentItem("PROTEÇÃO 4", exclusivityGroup = "protecao_botas"),
                EnchantmentItem("PROTEÇÃO CONTRA PROJÉTEIS 4", exclusivityGroup = "protecao_botas"),
                EnchantmentItem("PROTEÇÃO CONTRA FOGO 4", exclusivityGroup = "protecao_botas"),
                EnchantmentItem("PROTEÇÃO CONTRA EXPLOSÕES 4", exclusivityGroup = "protecao_botas"),
                EnchantmentItem("PESO PENA 4"),
                EnchantmentItem("INQUEBRÁVEL 3"),
                EnchantmentItem("ESPINHOS 3"),
                EnchantmentItem("PASSOS PROFUNDOS 3", exclusivityGroup = "passos_botas"),
                EnchantmentItem("PASSOS GELADOS 2", exclusivityGroup = "passos_botas"),
                EnchantmentItem("VELOCIDADE DAS ALMAS 3"),
                EnchantmentItem("REMENDO")
            )
        ),
        // MELEE WEAPONS
        CategoryConfig(
            id = "ESPADA",
            displayName = "Espada",
            description = "Arma de combate próximo com dano versátil.",
            emoji = "⚔️",
            enchantments = listOf(
                EnchantmentItem("AFIAÇÃO 5", exclusivityGroup = "afiacao_espada"),
                EnchantmentItem("JULGAMENTO 5", exclusivityGroup = "afiacao_espada"),
                EnchantmentItem("RUÍNA DOS ARTRÓPODES 5", exclusivityGroup = "afiacao_espada"),
                EnchantmentItem("REPULSÃO 2"),
                EnchantmentItem("ASPECTO FLAMEJANTE 2"),
                EnchantmentItem("SAQUE 3"),
                EnchantmentItem("ALCANCE 3"),
                EnchantmentItem("INQUEBRÁVEL 3"),
                EnchantmentItem("REMENDO")
            )
        ),
        CategoryConfig(
            id = "MACHADO",
            displayName = "Machado",
            description = "Ferramenta e arma versátil para combate e colheita.",
            emoji = "🪓",
            enchantments = listOf(
                EnchantmentItem("AFIAÇÃO 5", exclusivityGroup = "afiacao_machado"),
                EnchantmentItem("JULGAMENTO 5", exclusivityGroup = "afiacao_machado"),
                EnchantmentItem("RUÍNA DOS ARTRÓPODES 5", exclusivityGroup = "afiacao_machado"),
                EnchantmentItem("TOQUE SUAVE", exclusivityGroup = "toque_machado"),
                EnchantmentItem("FORTUNA 3", exclusivityGroup = "toque_machado"),
                EnchantmentItem("EFICIÊNCIA 5"),
                EnchantmentItem("INQUEBRÁVEL 3"),
                EnchantmentItem("REMENDO")
            )
        ),
        CategoryConfig(
            id = "MAÇA",
            displayName = "Maça",
            description = "Arma devastadora das fortalezas ominosas com rajadas de vento.",
            emoji = "⚒️",
            enchantments = listOf(
                EnchantmentItem("DENSIDADE 5", exclusivityGroup = "afiacao_maca"),
                EnchantmentItem("RUPTURA 4", exclusivityGroup = "afiacao_maca"),
                EnchantmentItem("JULGAMENTO 5", exclusivityGroup = "afiacao_maca"),
                EnchantmentItem("RUÍNA DOS ARTRÓPODES 5", exclusivityGroup = "afiacao_maca"),
                EnchantmentItem("ASPECTO FLAMEJANTE 2"),
                EnchantmentItem("RAJADA DE VENTO 3"),
                EnchantmentItem("INQUEBRÁVEL 3"),
                EnchantmentItem("REMENDO")
            )
        ),
        CategoryConfig(
            id = "LANÇA",
            displayName = "Lança",
            description = "Arma de longo alcance com estocadas precisas.",
            emoji = "🔱",
            enchantments = listOf(
                EnchantmentItem("AFIAÇÃO 5", exclusivityGroup = "afiacao_lanca"),
                EnchantmentItem("JULGAMENTO 5", exclusivityGroup = "afiacao_lanca"),
                EnchantmentItem("RUÍNA DOS ARTRÓPODES 5", exclusivityGroup = "afiacao_lanca"),
                EnchantmentItem("ASPECTO FLAMEJANTE 2"),
                EnchantmentItem("REPULSÃO 2"),
                EnchantmentItem("SAQUE 3"),
                EnchantmentItem("ESTOCADA 3", exclusiveWith = "REMENDO"),
                EnchantmentItem("INQUEBRÁVEL 3"),
                EnchantmentItem("REMENDO", exclusiveWith = "ESTOCADA 3")
            )
        ),
        // RANGED WEAPONS
        CategoryConfig(
            id = "ARCO",
            displayName = "Arco",
            description = "Arma à distância clássica com múltiplas possibilidades.",
            emoji = "🏹",
            enchantments = listOf(
                EnchantmentItem("FORÇA 5"),
                EnchantmentItem("IMPACTO 2"),
                EnchantmentItem("CHAMA"),
                EnchantmentItem("INFINIDADE", exclusiveWith = "REMENDO"),
                EnchantmentItem("REMENDO", exclusiveWith = "INFINIDADE"),
                EnchantmentItem("INQUEBRÁVEL 3")
            )
        ),
        CategoryConfig(
            id = "BESTA",
            displayName = "Besta",
            description = "Arma de disparo automático com rápida cadência.",
            emoji = "🎯",
            enchantments = listOf(
                EnchantmentItem("PERFURAÇÃO 4", exclusivityGroup = "disparo_besta"),
                EnchantmentItem("RAJADA", exclusivityGroup = "disparo_besta"),
                EnchantmentItem("CARGA RÁPIDA 3"),
                EnchantmentItem("INQUEBRÁVEL 3"),
                EnchantmentItem("REMENDO")
            )
        ),
        CategoryConfig(
            id = "TRIDENTE",
            displayName = "Tridente",
            description = "Arma aquática versátil com múltiplas funções.",
            emoji = "🗡️",
            enchantments = listOf(
                EnchantmentItem("PENETRAÇÃO 5"),
                EnchantmentItem("LEALDADE 3", exclusivityGroup = "movimento_tridente"),
                EnchantmentItem("CORRENTEZA 3", exclusivityGroup = "movimento_tridente"),
                EnchantmentItem("CONDUTIVIDADE", exclusiveWith = "CORRENTEZA 3"),
                EnchantmentItem("INQUEBRÁVEL 3"),
                EnchantmentItem("REMENDO")
            )
        ),
        // TOOLS
        CategoryConfig(
            id = "PICARETA",
            displayName = "Picareta",
            description = "A melhor amiga do minerador, do carvão à netherite.",
            emoji = "⛏️",
            enchantments = listOf(
                EnchantmentItem("TOQUE SUAVE", exclusivityGroup = "toque_picareta"),
                EnchantmentItem("FORTUNA 3", exclusivityGroup = "toque_picareta"),
                EnchantmentItem("EFICIÊNCIA 5"),
                EnchantmentItem("INQUEBRÁVEL 3"),
                EnchantmentItem("REMENDO")
            )
        ),
        CategoryConfig(
            id = "PÁ",
            displayName = "Pá",
            description = "Escavação rápida e eficiente de blocos soltos.",
            emoji = "🥄",
            enchantments = listOf(
                EnchantmentItem("TOQUE SUAVE", exclusivityGroup = "toque_pa"),
                EnchantmentItem("FORTUNA 3", exclusivityGroup = "toque_pa"),
                EnchantmentItem("EFICIÊNCIA 5"),
                EnchantmentItem("INQUEBRÁVEL 3"),
                EnchantmentItem("REMENDO")
            )
        ),
        CategoryConfig(
            id = "VARA",
            displayName = "Vara de Pesca",
            description = "Essencial para pescar tesouros e peixes no oceano.",
            emoji = "🎣",
            enchantments = listOf(
                EnchantmentItem("ISCA 3"),
                EnchantmentItem("SORTE NO MAR 3"),
                EnchantmentItem("INQUEBRÁVEL 3"),
                EnchantmentItem("REMENDO")
            )
        ),
        // SPECIAL ITEMS
        CategoryConfig(
            id = "ELYTRA",
            displayName = "Élitros",
            description = "Asas do dragão do End para voar livremente.",
            emoji = "🦇",
            enchantments = listOf(
                EnchantmentItem("INQUEBRÁVEL 3"),
                EnchantmentItem("REMENDO")
            )
        ),
        CategoryConfig(
            id = "ESCUDO",
            displayName = "Escudo",
            description = "Proteção defensiva contra ataques próximos.",
            emoji = "🛡️",
            enchantments = listOf(
                EnchantmentItem("INQUEBRÁVEL 3"),
                EnchantmentItem("REMENDO")
            )
        )
    )

    // Synchronize UI State showing combined local database selections and current temporary state
    val uiState: StateFlow<Map<String, Set<String>>> = combine(dbSelections, _tempSelections) { dbList, temp ->
        val result = mutableMapOf<String, Set<String>>()
        
        // Seed default items from database contents
        for (item in dbList) {
            val enchantStrings = if (item.checkedEnchantments.isEmpty()) {
                emptySet()
            } else {
                item.checkedEnchantments.split(",").toSet()
            }
            result[item.itemId] = enchantStrings
        }

        // Apply temporary local edits in-memory (taking priority for instantaneous reactive UI)
        temp.forEach { (key, value) ->
            result[key] = value
        }

        // Seed empty lists for missing categories
        categories.forEach { cat ->
            if (!result.containsKey(cat.id)) {
                result[cat.id] = emptySet()
            }
        }

        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    init {
        // Hydrate database values initially
        viewModelScope.launch {
            dbSelections.collect { list ->
                val initialMap = mutableMapOf<String, Set<String>>()
                list.forEach { item ->
                    if (item.checkedEnchantments.isNotEmpty()) {
                        initialMap[item.itemId] = item.checkedEnchantments.split(",").toSet()
                    }
                }
                _tempSelections.value = initialMap
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
        _saveStatus.value = null // clear notifications on screen change
    }

    // Toggle logic emphasizing strict mutual exclusivity boundaries
    fun toggleEnchantment(itemId: String, enchantmentName: String, isChecked: Boolean) {
        val currentSelections = _tempSelections.value[itemId]?.toMutableSet() ?: mutableSetOf()
        val category = categories.find { it.id == itemId } ?: return
        val currentOption = category.enchantments.find { it.name == enchantmentName } ?: return

        if (isChecked) {
            currentSelections.add(enchantmentName)
            
            // Handle pair-wise mutual exclusivity constraints
            currentOption.exclusiveWith?.let { exclusivePair ->
                currentSelections.remove(exclusivePair)
            }
            
            // Handle grouped mutual exclusivity (only one per group)
            currentOption.exclusivityGroup?.let { groupId ->
                category.enchantments
                    .filter { it.exclusivityGroup == groupId && it.name != enchantmentName }
                    .forEach { currentSelections.remove(it.name) }
            }
        } else {
            currentSelections.remove(enchantmentName)
        }

        val updatedMap = _tempSelections.value.toMutableMap()
        updatedMap[itemId] = currentSelections
        _tempSelections.value = updatedMap

        // Auto-save selections instantaneously to database to preserve reliability
        saveStateToDb(itemId, currentSelections.toList())
    }

    // Helper to persist the enchantment state directly in Room
    private fun saveStateToDb(itemId: String, list: List<String>) {
        viewModelScope.launch {
            repository.saveItemEnchantments(itemId, list)
        }
    }

    // Explicit visual feedback trigger: Play an enchanting glitter or levels increased status toast
    fun triggerExplicitSaveFeedback(itemId: String) {
        viewModelScope.launch {
            val displayName = categories.find { it.id == itemId }?.displayName ?: itemId
            _saveStatus.value = "Nível de Encantamento de $displayName memorizado com sucesso! ✨📖"
        }
    }

    fun dismissSaveStatus() {
        _saveStatus.value = null
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            categories.forEach { cat ->
                repository.saveItemEnchantments(cat.id, emptyList())
            }
            _tempSelections.value = emptyMap()
            _saveStatus.value = "Todos os encantamentos limpos! 🏺🔨"
        }
    }
}
