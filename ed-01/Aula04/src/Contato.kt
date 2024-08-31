class Contato(val nome: String, val idade: Int, val altura: Float, val gênero: Char, val email: String) : Imprimível {
    // Função para inicializar o contato (opcional)
    fun Contato() {
        // Inicializa o contato, se necessário
    }

    // Implementação do método imprimir da interface Imprimível
    override fun imprimir() {
        println("Contato: Nome: $nome, Idade: $idade, Altura: $altura, Gênero: $gênero, Email: $email")
    }
}