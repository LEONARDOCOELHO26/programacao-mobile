package com.example.receitasmalucas

import okhttp3.*
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.receitasmalucas.databinding.ActivityReceitaBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.IOException
import kotlin.random.Random

class ReceitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceitaBinding
    private var isLiked = false
    private var receita: Receita? = null
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.receita)
            setDisplayHomeAsUpEnabled(false)
        }
        binding.toolbar.setBackgroundColor(android.graphics.Color.TRANSPARENT)

        val ingredientes = intent.getStringArrayListExtra("selectedIngredients") ?: arrayListOf()
        if (ingredientes.isEmpty()) {
            solicitarReceitaIA()
        } else {
            receita = buscarReceitaAleatoria(ingredientes)
            exibirReceita(receita)
        }

        binding.buttonCurtir.setOnClickListener {
            isLiked = !isLiked
            updateLikeButton()
        }

        binding.buttonCompartilhar.setOnClickListener {
            receita?.let { rec -> compartilharReceita(rec.nome, ingredientes, rec.modoPreparo) }
        }

        binding.buttonVoltar.setOnClickListener { finish() }
    }

    private fun exibirReceita(receita: Receita?) {
        receita?.let {
            binding.textViewTituloReceita.text = it.nome
            binding.textViewListaIngredientes.text = it.obterListaIngredientes()
            binding.textViewDescricaoModoPreparo.text = it.modoPreparo
        } ?: run {
            binding.textViewTituloReceita.text = getString(R.string.nenhuma_receita_encontrada)
            binding.textViewListaIngredientes.text = ""
            binding.textViewDescricaoModoPreparo.text = ""
        }
    }

    private fun updateLikeButton() {
        binding.buttonCurtir.text = if (isLiked) getString(R.string.descurtir) else getString(R.string.curtir)
    }

    private fun compartilharReceita(titulo: String, ingredientes: List<String>, preparo: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "Confira a receita: $titulo\n\nIngredientes:\n${ingredientes.joinToString(", ")}\n\nModo de Preparo:\n$preparo"
            )
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.compartilhar_via)))
    }

    private fun buscarReceitaAleatoria(ingredientes: List<String>): Receita? {
        val receitas = carregarReceitasDoXLSX()
        val receitasFiltradas = receitas.filter { receita ->
            val ingredientesReceita = listOf(receita.ingrediente1, receita.ingrediente2, receita.ingrediente3)
            ingredientesReceita.all { it in ingredientes }
        }
        return if (receitasFiltradas.isNotEmpty()) receitasFiltradas[Random.nextInt(receitasFiltradas.size)] else null
    }

    private fun solicitarReceitaIA() {
        val apiKey = "chave-api"
        if (apiKey.isEmpty()) {
            Log.e("ReceitaActivity", "A chave da API está ausente.")
            runOnUiThread {
                Toast.makeText(this, "Erro: Chave da API ausente.", Toast.LENGTH_LONG).show()
            }
            return
        }

        val prompt = "Me sugira uma receita com ingredientes disponíveis."

        val jsonObject = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            put("max_tokens", 100)
            put("temperature", 0.7)
        }

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                response.body?.use { responseBody ->
                    if (response.isSuccessful) {
                        val result = JSONObject(responseBody.string())
                            .getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")
                        withContext(Dispatchers.Main) {
                            binding.textViewTituloReceita.text = "Receita da IA"
                            binding.textViewDescricaoModoPreparo.text = result.trim()
                        }
                    } else {
                        val errorBody = responseBody.string()
                        Log.e("ReceitaActivity", "Erro na resposta da IA: $errorBody")
                        withContext(Dispatchers.Main) {
                            binding.textViewTituloReceita.text = "Erro ao gerar receita"
                            binding.textViewDescricaoModoPreparo.text = "Detalhes do erro: $errorBody"
                            Toast.makeText(this@ReceitaActivity, "Erro ao gerar receita. Verifique o log.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ReceitaActivity", "Erro ao solicitar receita da IA", e)
                withContext(Dispatchers.Main) {
                    binding.textViewTituloReceita.text = "Erro de conexão"
                    binding.textViewDescricaoModoPreparo.text = "Verifique sua conexão com a internet e tente novamente."
                    Toast.makeText(this@ReceitaActivity, "Erro de conexão.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun carregarReceitasDoXLSX(): List<Receita> {
        val receitas = mutableListOf<Receita>()
        try {
            assets.open("receitas.xlsx").use { inputStream ->
                val workbook = XSSFWorkbook(inputStream)
                val sheet = workbook.getSheetAt(0)
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
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ReceitaActivity", "Erro ao carregar o arquivo XLSX: ${e.message}")
        }
        return receitas
    }
}

data class Receita(
    val nome: String,
    val ingrediente1: String,
    val ingrediente2: String,
    val ingrediente3: String,
    val outrosIngredientes: String,
    val modoPreparo: String
) {
    fun obterListaIngredientes(): String {
        return "- $ingrediente1\n- $ingrediente2\n- $ingrediente3\nOutros Ingredientes: $outrosIngredientes"
    }
}
