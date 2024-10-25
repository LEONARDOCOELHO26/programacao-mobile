package com.example.receitasmalucas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.receitasmalucas.databinding.ActivityReceitaBinding
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import kotlin.random.Random

class ReceitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceitaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReceitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura a toolbar com o botão de voltar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Receita"

        // Receber dados enviados da MainActivity
        val ingredientes = intent.getStringArrayListExtra("selectedIngredients") ?: arrayListOf()
        Log.d("ReceitaActivity", "Ingredientes selecionados: $ingredientes")

        // Buscar uma receita aleatória com a combinação de ingredientes
        val receita = buscarReceitaAleatoria(ingredientes)

        // Exibir dados na tela
        receita?.let {
            binding.textViewTituloReceita.text = it.nome
            binding.textViewListaIngredientes.text = ingredientes.joinToString("\n") { "- $it" } +
                    "\nOutros Ingredientes: ${it.outrosIngredientes}"
            binding.textViewDescricaoModoPreparo.text = it.modoPreparo
        } ?: run {
            // Caso não encontre uma receita
            binding.textViewTituloReceita.text = "Nenhuma receita encontrada"
            binding.textViewListaIngredientes.text = ""
            binding.textViewDescricaoModoPreparo.text = ""
        }

        // Ação do botão de compartilhar
        binding.buttonCompartilhar.setOnClickListener {
            receita?.let { rec ->
                compartilharReceita(rec.nome, ingredientes, rec.modoPreparo)
            }
        }

        // Ação do botão de voltar
        binding.buttonVoltar.setOnClickListener {
            finish()
        }
    }

    // Função para compartilhar a receita
    private fun compartilharReceita(titulo: String, ingredientes: List<String>, preparo: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "Confira a receita: $titulo\n\nIngredientes:\n${ingredientes.joinToString(", ")}\n\nModo de Preparo:\n$preparo"
            )
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Compartilhar via"))
    }

    // Função para buscar uma receita aleatória com base nos ingredientes do arquivo XLSX
    private fun buscarReceitaAleatoria(ingredientes: List<String>): Receita? {
        val receitas = carregarReceitasDoXLSX() // Carrega todas as receitas do arquivo XLSX

        // Filtrar as receitas que contêm todos os ingredientes fornecidos
        val receitasFiltradas = receitas.filter { receita ->
            val ingredientesReceita = listOf(receita.ingrediente1, receita.ingrediente2, receita.ingrediente3)
            ingredientesReceita.all { it in ingredientes }
        }

        return if (receitasFiltradas.isNotEmpty()) {
            receitasFiltradas[Random.nextInt(receitasFiltradas.size)]
        } else {
            null
        }
    }

    // Função para carregar as receitas de um arquivo XLSX
    private fun carregarReceitasDoXLSX(): List<Receita> {
        val receitas = mutableListOf<Receita>()

        try {
            val inputStream: InputStream = assets.open("receitas.xlsx") // Substitua pelo nome do seu arquivo XLSX
            val workbook = XSSFWorkbook(inputStream)
            val sheet = workbook.getSheetAt(0) // Considerando que os dados estão na primeira aba

            // Iterar sobre as linhas da planilha, começando na linha 1 (ignorando o cabeçalho)
            for (row in sheet.iterator().asSequence().drop(1)) {
                val nome = row.getCell(0)?.stringCellValue ?: ""
                val ingrediente1 = row.getCell(1)?.stringCellValue ?: ""
                val ingrediente2 = row.getCell(2)?.stringCellValue ?: ""
                val ingrediente3 = row.getCell(3)?.stringCellValue ?: ""
                val outrosIngredientes = row.getCell(4)?.stringCellValue ?: ""
                val modoPreparo = row.getCell(5)?.stringCellValue ?: ""

                if (nome.isNotBlank() && ingrediente1.isNotBlank() && ingrediente2.isNotBlank() && ingrediente3.isNotBlank()) {
                    val receita = Receita(
                        nome = nome.trim(),
                        ingrediente1 = ingrediente1.trim(),
                        ingrediente2 = ingrediente2.trim(),
                        ingrediente3 = ingrediente3.trim(),
                        outrosIngredientes = outrosIngredientes.trim(),
                        modoPreparo = modoPreparo.trim()
                    )
                    receitas.add(receita)
                    Log.d("ReceitaActivity", "Receita carregada: $receita")
                }
            }

            inputStream.close()
            workbook.close()
            Log.d("ReceitaActivity", "Receitas carregadas com sucesso do XLSX")

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ReceitaActivity", "Erro ao carregar o arquivo XLSX: ${e.message}")
        }

        return receitas
    }
}

// Classe para representar uma Receita
data class Receita(
    val nome: String,
    val ingrediente1: String,
    val ingrediente2: String,
    val ingrediente3: String,
    val outrosIngredientes: String,
    val modoPreparo: String
)
