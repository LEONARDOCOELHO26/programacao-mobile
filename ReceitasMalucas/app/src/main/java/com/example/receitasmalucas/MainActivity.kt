package com.example.receitasmalucas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.receitasmalucas.ui.theme.ReceitasMalucasTheme

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            ReceitasMalucasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    IngredientSelectionScreen { selectedIngredients ->
                        if (selectedIngredients.size == 3) {
                            val intent = Intent(this, ReceitaActivity::class.java)
                            intent.putStringArrayListExtra("selectedIngredients", ArrayList(selectedIngredients))
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Selecione exatamente 3 ingredientes", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IngredientSelectionScreen(onIngredientsSelected: (List<String>) -> Unit) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    // Lista de ingredientes e suas imagens
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Campo de busca
        BasicTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    innerTextField()
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_search),
                        contentDescription = "Search",
                        tint = Color.Gray
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de ingredientes filtrados
        ingredients.filter { it.first.contains(searchQuery.text, ignoreCase = true) }
            .forEach { (ingredientName, imageRes) ->
                IngredientItem(
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

        Spacer(modifier = Modifier.height(16.dp))

        // Botão para prosseguir
        Button(
            onClick = { onIngredientsSelected(selectedIngredients) },
            enabled = selectedIngredients.size == 3,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Avançar")
        }
    }
}

@Composable
fun IngredientItem(name: String, imageRes: Int, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onClick() }
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Exibição da imagem do ingrediente
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.size(50.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 18.sp,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun IngredientSelectionScreenPreview() {
    ReceitasMalucasTheme {
        IngredientSelectionScreen {}
    }
}
