package com.example.receitasmalucas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.receitasmalucas.ui.theme.ReceitasMalucasTheme
import androidx.compose.foundation.text.BasicTextField

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReceitasMalucasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF5DEB3)
                ) {
                    IngredientSelectionScreen(
                        onIngredientsSelected = { selectedIngredients ->
                            val intent = Intent(this, ReceitaActivity::class.java)
                            intent.putStringArrayListExtra("selectedIngredients", ArrayList(selectedIngredients))
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun IngredientSelectionScreen(onIngredientsSelected: (List<String>) -> Unit) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val ingredients = listOf(
        "Frango" to R.drawable.frango,
        "Arroz" to R.drawable.arroz,
        "Batata" to R.drawable.batata,
        "Ovo" to R.drawable.ovo,
        "Feijão" to R.drawable.feijao,
        "Brócolis" to R.drawable.brocolis,
        "Queijo" to R.drawable.queijo,
        "Azeite de oliva" to R.drawable.azeite_de_oliva,
        "Cenoura" to R.drawable.cenoura,
        "Alho" to R.drawable.alho
    )
    val selectedIngredients = remember { mutableStateListOf<String>() }
    val filteredIngredients = ingredients.filter {
        it.first.contains(searchQuery.text, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5DEB3))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Digite o ingrediente") },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = stringResource(R.string.search),
                        tint = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Escolha até 3 ingredientes:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 120.dp),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredIngredients) { (ingredientName, imageRes) ->
                    IngredientCard(
                        name = ingredientName,
                        imageRes = imageRes,
                        isSelected = selectedIngredients.contains(ingredientName),
                        onClick = {
                            if (selectedIngredients.contains(ingredientName)) {
                                selectedIngredients.remove(ingredientName)
                            } else if (selectedIngredients.size < 3) {
                                selectedIngredients.add(ingredientName)
                            }
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { onIngredientsSelected(selectedIngredients) },
            containerColor = if (selectedIngredients.size == 3) Color(0xFFDAA520) else Color.Gray,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Gerar Receita",
                    tint = White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Gerar Receita",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        FloatingActionButton(
            onClick = { onIngredientsSelected(emptyList()) },
            containerColor = Color(0xFFDAA520),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Receita com IA",
                    tint = White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Receita com IA",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun IngredientCard(name: String, imageRes: Int, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color(0xFFE5C097) else Color(0xFFF5DEB3)
    val borderColor = if (isSelected) Color(0xFFDAA520) else Color.Transparent

    Column(
        modifier = Modifier
            .padding(8.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = name,
            modifier = Modifier
                .size(80.dp)
                .background(Color.White, shape = RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp,
            color = DarkGray
        )
    }
}
